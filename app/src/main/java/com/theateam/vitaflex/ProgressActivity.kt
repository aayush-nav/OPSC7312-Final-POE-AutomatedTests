package com.theateam.vitaflex

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProgressActivity : AppCompatActivity() {

    private lateinit var startDateEditText: EditText
    private lateinit var endDateEditText: EditText
    private lateinit var displayGraphButton: Button
    private lateinit var backbtn: ImageButton
    private lateinit var barChart: BarChart
    private var startDate: String? = null
    private var endDate: String? = null
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_progress)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Load saved language preference
        loadLanguage()

        startDateEditText = findViewById(R.id.startDateEditText)
        endDateEditText = findViewById(R.id.endDateEditText)
        displayGraphButton = findViewById(R.id.displayGraphButton)
        barChart = findViewById(R.id.bar_chart)
        backbtn = findViewById(R.id.progress_back_Button)

        // Retrieve the logged-in user's email from SharedPreferences
        val sharedPreferences = getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        userEmail = sharedPreferences.getString("USER_EMAIL", null)

//        userEmail = "user@example.com"
        // Set up date pickers
        startDateEditText.setOnClickListener { showDatePickerDialog(true) }
        endDateEditText.setOnClickListener { showDatePickerDialog(false) }

        // Set up button click listener to display graph
        displayGraphButton.setOnClickListener {
            if (startDate != null && endDate != null) {
                fetchAndDisplayGraph()
            } else {
                Toast.makeText(this, "Please select a date range", Toast.LENGTH_SHORT).show()
            }
        }

        backbtn.setOnClickListener {
            finish()
        }
    }

    // Function to show the date picker dialog
    private fun showDatePickerDialog(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = "${year}-${String.format("%02d", monthOfYear + 1)}-${
                    String.format(
                        "%02d",
                        dayOfMonth
                    )
                }"
                if (isStartDate) {
                    startDate = selectedDate
                    startDateEditText.setText(selectedDate)
                } else {
                    endDate = selectedDate
                    endDateEditText.setText(selectedDate)
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    // Function to fetch and display the graph
    private fun fetchAndDisplayGraph() {
        if (userEmail == null) {
            Toast.makeText(this, "Error: User email not found", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.apiService.getMeals().enqueue(object : Callback<List<Meal>> {
            override fun onResponse(call: Call<List<Meal>>, response: Response<List<Meal>>) {
                if (response.isSuccessful) {
                    val meals = response.body()
                    meals?.let {
                        // Filter meals by email and date range
                        val filteredMeals = it.filter { meal ->
                            meal.email == userEmail && meal.date >= startDate!! && meal.date <= endDate!!
                        }
                        val allDatesInRange = generateAllDatesInRange(startDate!!, endDate!!)
                        displayCaloriesBarChart(filteredMeals, allDatesInRange)
                    }
                } else {
                    Toast.makeText(this@ProgressActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Meal>>, t: Throwable) {
                Toast.makeText(this@ProgressActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Function to generate all dates between the start and end dates
    private fun generateAllDatesInRange(start: String, end: String): List<String> {
        val dateList = mutableListOf<String>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val startDate = dateFormat.parse(start)
        val endDate = dateFormat.parse(end)
        val calendar = Calendar.getInstance()
        calendar.time = startDate!!

        while (calendar.time.before(endDate) || calendar.time == endDate) {
            dateList.add(dateFormat.format(calendar.time))
            calendar.add(Calendar.DATE, 1) // Move to the next day
        }

        return dateList
    }

    // Function to display the graph
    private fun displayCaloriesBarChart(meals: List<Meal>, allDatesInRange: List<String>) {
        val entries = ArrayList<BarEntry>()
        val caloriesByDateAndCategory = mutableMapOf<String, MutableMap<String, Float>>()

        // Initialize the map for all dates with 0 calories for all categories
        allDatesInRange.forEach { date ->
            caloriesByDateAndCategory[date] = mutableMapOf(
                "Breakfast" to 0f,
                "Lunch" to 0f,
                "Dinner" to 0f,
                "Snacks" to 0f
            )
        }

        // Populate the map with actual meal data
        meals.forEach { meal ->
            val mealDate = meal.date
            val mealCategory = meal.mealCategory
            val totalCalories = meal.mealItems.sumByDouble { it.calories }.toFloat()

            caloriesByDateAndCategory[mealDate]?.set(mealCategory, totalCalories)
        }

        // Convert the map data into BarEntries for each category
        var index = 0f
        caloriesByDateAndCategory.forEach { (date, caloriesMap) ->
            val breakfastCalories = caloriesMap["Breakfast"] ?: 0f
            val lunchCalories = caloriesMap["Lunch"] ?: 0f
            val dinnerCalories = caloriesMap["Dinner"] ?: 0f
            val snacksCalories = caloriesMap["Snacks"] ?: 0f

            // Add entries for each category, offsetting the x-position
            entries.add(BarEntry(index, floatArrayOf(breakfastCalories, lunchCalories, dinnerCalories, snacksCalories)))
            index += 1f
        }

        // Create a BarDataSet with grouped bars
        val barDataSet = BarDataSet(entries, "Calories by Meal Category")
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS, 255)
        barDataSet.stackLabels = arrayOf("Breakfast", "Lunch", "Dinner", "Snacks")

        val barData = BarData(barDataSet)
        barChart.data = barData
        barChart.invalidate() // Refresh the chart

        // Customize X and Y axes
        val xAxis: XAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f // Ensure dates are evenly spaced
        xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (value.toInt() in allDatesInRange.indices) {
                    allDatesInRange[value.toInt()]
                } else ""
            }
        }

        val leftAxis: YAxis = barChart.axisLeft
        leftAxis.axisMinimum = 0f // Minimum value for calories

        barChart.axisRight.isEnabled = false // Disable right Y-axis
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