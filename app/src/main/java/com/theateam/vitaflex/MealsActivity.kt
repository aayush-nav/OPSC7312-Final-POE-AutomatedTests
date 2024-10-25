package com.theateam.vitaflex

import MealExpandableListAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MealsActivity : AppCompatActivity() {

    private lateinit var expandableListView: ExpandableListView
    private lateinit var dayButton: Button
    private lateinit var weekButton: Button
    private lateinit var refreshMealButton: ImageButton
    private lateinit var addMealButton: FloatingActionButton
    private lateinit var backButton: ImageButton

    private lateinit var mealAdapter: MealExpandableListAdapter
    private lateinit var mealCategories: List<String>  // Changed to List since it doesn't need to be mutable
    private lateinit var mealItems: MutableMap<String, List<Meal>>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_meals)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Load saved language preference
        loadLanguage()

        expandableListView = findViewById(R.id.MealsExpendableListView)
        dayButton = findViewById(R.id.MealsDaybtn)
        weekButton = findViewById(R.id.MealsWeekbtn)
        refreshMealButton = findViewById(R.id.MealsRefreshBtn)
        backButton = findViewById(R.id.MealsBackBtn)
        addMealButton = findViewById(R.id.floatingBtnAdd)

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        fetchAllMeals()

        //this button will take the user to the add new meals page
        addMealButton.setOnClickListener {
            val intent = Intent(this, DailyMealsActivity::class.java)
            startActivity(intent)
        }

        refreshMealButton.setOnClickListener() {
            fetchAllMeals()
        }

        dayButton.setOnClickListener { fetchMealsForDay() }
        weekButton.setOnClickListener { fetchMealsForWeek() }

//        mealCategories = mutableListOf("Breakfast", "Lunch", "Dinner", "Snack")
//        getString(R.string.no_network_message)
        mealCategories = mutableListOf(getString(R.string.breakfast), getString(R.string.lunch), getString(R.string.dinner), getString(R.string.snack))
        mealItems = mutableMapOf()

        // Initialize adapter with both mealCategories and mealItems
        mealAdapter = MealExpandableListAdapter(this, mealCategories, mealItems)
        expandableListView.setAdapter(mealAdapter)


    }

    //this will fetch all of the meals that were saved on the current day and display them to the user
    private fun fetchMealsForDay() {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Retrieve the user's email from SharedPreferences
        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)
//        val userEmail = "user@example.com"
        // Check if userEmail is not null
        if (userEmail != null) {
            ApiClient.apiService.getMeals().enqueue(object : Callback<List<Meal>> {
                override fun onResponse(call: Call<List<Meal>>, response: Response<List<Meal>>) {
                    if (response.isSuccessful) {
                        val meals = response.body()
                        meals?.let {
                            // Filter by date and user email
                            val todayMeals = it.filter { meal ->
                                meal.date == currentDate && meal.email == userEmail
                            }
                            displayMealsByCategory(todayMeals)
                        }
                    } else {
                        Toast.makeText(this@MealsActivity,
                            getString(R.string.failed_to_fetch_meals_for_today), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Meal>>, t: Throwable) {
                    Toast.makeText(this@MealsActivity,
                        getString(R.string.error_2, t.message), Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, getString(R.string.user_email_not_found), Toast.LENGTH_SHORT).show()
        }
    }


    //this will fetch all of the meals that were saved in the current week and display them to the user
    private fun fetchMealsForWeek() {
        val calendar = Calendar.getInstance()
        val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)

        // Retrieve the user's email from SharedPreferences
        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)
//        val userEmail = "user@example.com"
        // Check if userEmail is not null
        if (userEmail != null) {
            ApiClient.apiService.getMeals().enqueue(object : Callback<List<Meal>> {
                override fun onResponse(call: Call<List<Meal>>, response: Response<List<Meal>>) {
                    if (response.isSuccessful) {
                        val meals = response.body()
                        meals?.let {
                            // Filter by week and user email
                            val weekMeals = it.filter { meal ->
                                val mealDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(meal.date)
                                calendar.time = mealDate
                                calendar.get(Calendar.WEEK_OF_YEAR) == currentWeek && meal.email == userEmail
                            }
                            displayMealsByCategory(weekMeals)
                        }
                    } else {
                        Toast.makeText(this@MealsActivity,
                            getString(R.string.failed_to_fetch_meals_for_this_week), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Meal>>, t: Throwable) {
                    Toast.makeText(this@MealsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, getString(R.string.user_email_not_found_1), Toast.LENGTH_SHORT).show()
        }
    }

    //this will display all of the meals that have been created and saved by the current user and display it back to them in a segmented format
    private fun fetchAllMeals() {
        // Retrieve the user's email from SharedPreferences
        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)

        // Check if userEmail is not null
        if (userEmail != null) {
            ApiClient.apiService.getMeals().enqueue(object : Callback<List<Meal>> {
                override fun onResponse(call: Call<List<Meal>>, response: Response<List<Meal>>) {
                    if (response.isSuccessful) {
                        val meals = response.body()
                        meals?.let {
                            // Filter by user email to get all meals for this user
                            val userMeals = it.filter { meal ->
                                meal.email == userEmail
                            }
                            displayMealsByCategory(userMeals)
                        }
                    } else {
                        Toast.makeText(this@MealsActivity,
                            getString(R.string.failed_to_fetch_all_meals), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Meal>>, t: Throwable) {
                    Toast.makeText(this@MealsActivity,
                        getString(R.string.error_4, t.message), Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, getString(R.string.user_email_not_found_3), Toast.LENGTH_SHORT).show()
        }
    }



//    private fun displayMealsByCategory(meals: List<Meal>) {
//        val breakfastMeals = meals.filter { it.mealCategory == "Breakfast" }
//        val lunchMeals = meals.filter { it.mealCategory == "Lunch" }
//        val dinnerMeals = meals.filter { it.mealCategory == "Dinner" }
//        val snackMeals = meals.filter { it.mealCategory == "Snack" }
//
//        // Update meal items map with filtered meals
//        mealItems["Breakfast"] = breakfastMeals
//        mealItems["Lunch"] = lunchMeals
//        mealItems["Dinner"] = dinnerMeals
//        mealItems["Snack"] = snackMeals
//
//        // Notify adapter about data changes
//        mealAdapter.notifyDataSetChanged()
//    }


    private fun displayMealsByCategory(meals: List<Meal>) {
        val groupedMeals = meals.groupBy { it.mealCategory } // Group meals by their category

        // Update meal items map with filtered meals
        mealItems.clear() // Clear existing items
        mealItems.putAll(groupedMeals) // Assign to mealItems

        // Notify adapter about data changes
        mealAdapter.notifyDataSetChanged()
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