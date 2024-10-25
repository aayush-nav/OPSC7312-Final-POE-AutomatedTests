package com.theateam.vitaflex

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.theateam.vitaflex.databinding.ActivityDailyMealsBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DailyMealsActivity : AppCompatActivity() {


    private val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val calendar = Calendar.getInstance()
    private var selectedDate: String? = null
    private var selectedLayout: LinearLayout? = null

//    private lateinit var binding: ActivityDailyMealsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        binding = ActivityDailyMealsBinding.inflate(layoutInflater)
//        setContentView(binding.root)

         setContentView(R.layout.activity_daily_meals)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Load saved language preference
        loadLanguage()

        val prevArrow: ImageButton = findViewById(R.id.dailyMeals_previousWeek_btn)
        val nextArrow: ImageButton = findViewById(R.id.dailyMeals_nextWeek_btn)
        val currentWeekTextView: TextView = findViewById(R.id.dailyMeals_currentWeek_textView)

        val btnBack : ImageButton = findViewById(R.id.dailyMeals_back_btn)
        val btnBreakfast : Button = findViewById(R.id.dailyMeals_breakfast_btn)
        val btnLunch : Button = findViewById(R.id.dailyMeals_lunch_btn)
        val btnDinner : Button = findViewById(R.id.dailyMeals_dinner_btn)
        val btnSnack : Button = findViewById(R.id.dailyMeals_snack_btn)

        // Display the current week when the activity starts
        displayCurrentWeek(currentWeekTextView)

        // Navigate to the previous week
        prevArrow.setOnClickListener {
            Log.d("DailyMealsActivity", "Previous button clicked")
            updateCalendar(-1)
            displayCurrentWeek(currentWeekTextView)
        }

        // Navigate to the next week
        nextArrow.setOnClickListener {
            Log.d("DailyMealsActivity", "Next button clicked")
            updateCalendar(1)
            displayCurrentWeek(currentWeekTextView)
        }

        // Set click listeners for each day's layout
        setDayClickListener(R.id.dailyMeals_sunday_layout, R.id.dailyMeals_sundayNumber_textView)
        setDayClickListener(R.id.dailyMeals_monday_layout, R.id.dailyMeals_mondayNumber_textView)
        setDayClickListener(R.id.dailyMeals_tuesday_layout, R.id.dailyMeals_tuesdayNumber_textView)
        setDayClickListener(R.id.dailyMeals_wednesday_layout, R.id.dailyMeals_wednesdayNumber_textView)
        setDayClickListener(R.id.dailyMeals_thursday_layout, R.id.dailyMeals_thursdayNumber_textView)
        setDayClickListener(R.id.dailyMeals_friday_layout, R.id.dailyMeals_fridayNumber_textView)
        setDayClickListener(R.id.dailyMeals_saturday_layout, R.id.dailyMeals_saturdayNumber_textView)


        btnBack.setOnClickListener {
            finish()
        }

        btnBreakfast.setOnClickListener {
            val intent = Intent(this, AddNewMealActivity::class.java)
            intent.putExtra("mealType", "Breakfast")
            saveMealCategory("Breakfast")
            intent.putExtra("selectedDate", selectedDate) // Pass the selected date
            startActivity(intent)
        }

        btnLunch.setOnClickListener {
            val intent = Intent(this, AddNewMealActivity::class.java)
            intent.putExtra("mealType", "Lunch")
            saveMealCategory("Lunch")
            intent.putExtra("selectedDate", selectedDate) // Pass the selected date
            startActivity(intent)
        }

        btnDinner.setOnClickListener {
            val intent = Intent(this, AddNewMealActivity::class.java)
            intent.putExtra("mealType", "Dinner")
            saveMealCategory("Dinner")
            intent.putExtra("selectedDate", selectedDate) // Pass the selected date
            startActivity(intent)
        }

        btnSnack.setOnClickListener {
            val intent = Intent(this, AddNewMealActivity::class.java)
            intent.putExtra("mealType", "Snack")
            saveMealCategory("Snack")
            intent.putExtra("selectedDate", selectedDate) // Pass the selected date
            startActivity(intent)
        }


    }

    // this will set a click listener to know which day was selected by the user
    private fun setDayClickListener(layoutId: Int, textViewId: Int) {
        val layout: LinearLayout = findViewById(layoutId)
        layout.setOnClickListener {
            // Highlight the selected layout
            highlightSelectedLayout(layout)

            // Get the selected day from the corresponding TextView
            val dayNumberTextView: TextView = findViewById(textViewId)
            val selectedDay = dayNumberTextView.text.toString().toInt()

            // Clone the calendar object to adjust the day
            val tempCalendar = calendar.clone() as Calendar

            // Set the calendar's day to the selected day
            // This accounts for cross-month boundaries (i.e., moving into October)
            tempCalendar.set(Calendar.DAY_OF_MONTH, selectedDay)

            // Check if we are at the start of a new month (handling cross-month boundaries)
            if (selectedDay < calendar.get(Calendar.DAY_OF_MONTH)) {
                tempCalendar.add(Calendar.MONTH, 1)  // Move to the next month
            }

            // Format the selected date as "YYYY-MM-DD"
            selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(tempCalendar.time)

            Log.d("DailyMealsActivity", "Selected date: $selectedDate")
            saveSelectedDate(selectedDate!!)

            // Enable meal buttons after date selection
            enableMealButtons()

            // Show a Toast with the full date
            Toast.makeText(this,
                getString(R.string.you_have_selected, selectedDate), Toast.LENGTH_SHORT).show()
        }
    }


    //this method will save the selected date in a sharedPreference
    private fun saveSelectedDate(date: String) {
        val sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("selectedDate", date)
        editor.apply()
    }

    //this method will save the selected meal category in a sharedPreference
    private fun saveMealCategory(mealCategory: String) {
        val sharedPreferences = getSharedPreferences("MyMealCategoryPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("mealCategory", mealCategory)
        editor.apply()
    }

    private fun enableMealButtons() {
        val btnBreakfast: Button = findViewById(R.id.dailyMeals_breakfast_btn)
        val btnLunch: Button = findViewById(R.id.dailyMeals_lunch_btn)
        val btnDinner: Button = findViewById(R.id.dailyMeals_dinner_btn)
        val btnSnack: Button = findViewById(R.id.dailyMeals_snack_btn)

        btnBreakfast.isEnabled = true
        btnLunch.isEnabled = true
        btnDinner.isEnabled = true
        btnSnack.isEnabled = true
    }



    private fun highlightSelectedLayout(layout: LinearLayout) {
        // Reset the background color of the previously selected layout
        selectedLayout?.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))

        // Set the background color for the currently selected layout
        layout.setBackgroundColor(Color.parseColor("#37F269"))

        // Store the currently selected layout
        selectedLayout = layout
    }

    // Function to update the calendar by one week (forward or backward)
    private fun updateCalendar(weeks: Int) {
        calendar.add(Calendar.WEEK_OF_YEAR, weeks)
    }

    // Function to display the current week
    private fun displayCurrentWeek(currentWeekTextView: TextView) {
        val startOfWeek = (calendar.clone() as Calendar).apply {
            set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        }
        val endOfWeek = (calendar.clone() as Calendar).apply {
            set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            add(Calendar.DAY_OF_WEEK, 6)
        }

        val startDate = sdf.format(startOfWeek.time)
        val endDate = sdf.format(endOfWeek.time)
        currentWeekTextView.text = "Week of $startDate"

        updateDaysOfWeek(startOfWeek)
    }

    // Function to update the days of the week
    private fun updateDaysOfWeek(startOfWeek: Calendar) {
        val dayLabels = listOf(
            R.id.dailyMeals_sundayNumber_textView,
            R.id.dailyMeals_mondayNumber_textView,
            R.id.dailyMeals_tuesdayNumber_textView,
            R.id.dailyMeals_wednesdayNumber_textView,
            R.id.dailyMeals_thursdayNumber_textView,
            R.id.dailyMeals_fridayNumber_textView,
            R.id.dailyMeals_saturdayNumber_textView
        )
        val dayNames = listOf(
            R.id.dailyMeals_sunday_lbl,
            R.id.dailyMeals_monday_lbl,
            R.id.dailyMeals_tuesday_lbl,
            R.id.dailyMeals_wednesday_lbl,
            R.id.dailyMeals_thursday_lbl,
            R.id.dailyMeals_friday_lbl,
            R.id.dailyMeals_saturday_lbl
        )

        dayLabels.zip(dayNames).forEachIndexed { i, (dayLabelId, dayNameId) ->
            val dayOfMonth = startOfWeek.get(Calendar.DAY_OF_MONTH)
            val dayOfWeek = SimpleDateFormat("E", Locale.getDefault()).format(startOfWeek.time)

            val dayNumber: TextView = findViewById(dayLabelId)
            val dayLabel: TextView = findViewById(dayNameId)

            dayNumber.text = dayOfMonth.toString()
            dayLabel.text = dayOfWeek

            startOfWeek.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private fun loadLanguage() {
        val sharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val savedLanguage = sharedPref.getString("language", "en")  // Default to English
        setLocale(savedLanguage ?: "en")
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}