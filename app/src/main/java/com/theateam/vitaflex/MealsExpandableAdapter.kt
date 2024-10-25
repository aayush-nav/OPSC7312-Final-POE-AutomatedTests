import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.theateam.vitaflex.Meal
import com.theateam.vitaflex.MealType
import com.theateam.vitaflex.R
import java.math.BigDecimal
import java.math.RoundingMode

class MealExpandableListAdapter(
    private val context: Context,
    private val mealCategories: List<String>,
    private val mealsMap: Map<String, List<Meal>>
) : BaseExpandableListAdapter() {

    // Returns the number of meal categories
    override fun getGroupCount(): Int = mealCategories.size

    // Counts total meal items across all meals for a given category
    override fun getChildrenCount(groupPosition: Int): Int {
        val category = mealCategories[groupPosition]
        return mealsMap[category]?.sumBy { it.mealItems.size } ?: 0 // Total count of meal items
    }

    // Returns the meal category name for a given group position
    override fun getGroup(groupPosition: Int): String = mealCategories[groupPosition]

    // Retrieve the specific meal item based on its position in the list
    override fun getChild(groupPosition: Int, childPosition: Int): MealType {
        val mealCategory = mealCategories[groupPosition]
        val meals = mealsMap[mealCategory] ?: emptyList()

        var currentChildPosition = childPosition
        for (meal in meals) {
            if (currentChildPosition < meal.mealItems.size) {
                return meal.mealItems[currentChildPosition] // Return the specific meal item
            }
            currentChildPosition -= meal.mealItems.size // Decrement the position
        }

        throw IndexOutOfBoundsException() // Fallback in case of invalid position
    }

    // Return the group ID based on its position
    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    // Return the child ID based on its position
    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    // Indicates whether IDs are stable (used for optimization)
    override fun hasStableIds(): Boolean = false

    // Indicates if a child item is selectable
    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true

    // Populate the group view with meal category name and total calories
    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.group_meal, parent, false)
        val mealCategoryTextView = view.findViewById<TextView>(R.id.tv_meal_category)
        val mealCaloriesTextView = view.findViewById<TextView>(R.id.tv_meal_calories)

        val mealCategory = getGroup(groupPosition)
        mealCategoryTextView.text = mealCategory

        // Calculate total calories for the meal items in this category
        val totalCalories = mealsMap[mealCategory]?.sumByDouble { meal ->
            meal.mealItems.sumByDouble { item -> item.calories }
        } ?: 0.0

        val caloriePreferences = context.getSharedPreferences("CaloriesPref", Context.MODE_PRIVATE)
        val calories = caloriePreferences.getBoolean("CALORIES_UNIT", true)

        if(calories == true)
        {
            mealCaloriesTextView.text = String.format("%.1f cal", totalCalories) // Show the current meal item's calories
        }
        else
        {
            val kiloJoules = totalCalories.times(4.184)
            mealCaloriesTextView.text = String.format("%.1f kJ", kiloJoules) // Show the current meal item's calories
        }


        return view
    }


    //all of the meal items for the current user will be displayed back to them from this adapter
    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_meal, parent, false)

        // Access the meal item
        val mealItem = getChild(groupPosition, childPosition) // Get the specific meal item

        // Find the TextViews
        val mealDateTextView = view.findViewById<TextView>(R.id.tv_meal_date)
        val mealNameTextView = view.findViewById<TextView>(R.id.tv_meal_name)
        val mealCaloriesTextView = view.findViewById<TextView>(R.id.tv_meal_calories)

        // Get the meal category to access the parent meals
        val mealCategory = getGroup(groupPosition)
        val meals = mealsMap[mealCategory] ?: emptyList()

        // Find the parent meal corresponding to the meal item
        var currentChildIndex = childPosition
        var mealDate = "" // Variable to store meal date
        for (meal in meals) {
            if (currentChildIndex < meal.mealItems.size) {
                mealDate = meal.date // Store the date of the current parent meal
                break
            }
            currentChildIndex -= meal.mealItems.size // Decrement to find the correct meal
        }

        // Set the date in the TextView
        mealDateTextView.text = mealDate

        // Display the current meal item's details
        mealNameTextView.text = mealItem.name // Show only the current meal item's name

        val caloriePreferences = context.getSharedPreferences("CaloriesPref", Context.MODE_PRIVATE)
        val calories = caloriePreferences.getBoolean("CALORIES_UNIT", true)

        //this will check whether calories or kiloJoules should be displayed and display accordingly
        if(calories == true)
        {
            mealCaloriesTextView.text = String.format("%.1f cal", mealItem.calories) // Show the current meal item's calories
        }
        else
        {
            val kiloJoules = mealItem.calories.times(4.184)
            mealCaloriesTextView.text = String.format("%.1f kJ", kiloJoules) // Show the current meal item's calories
        }


        return view
    }

}

