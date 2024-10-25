package com.theateam.vitaflex

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.theateam.vitaflex.databinding.ActivityListOfIngredientsBinding
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

class ListOfIngredientsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListOfIngredientsBinding

    private lateinit var ingredientsAdapter: IngredientsAdapter
    private var ingredientList: ArrayList<Ingredient> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityListOfIngredientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setContentView(R.layout.activity_list_of_ingredients)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Load saved language preference
        loadLanguage()

        // Retrieve the ingredient list from the Intent
        ingredientList = intent.getSerializableExtra("ingredientList") as ArrayList<Ingredient>

        // Set up RecyclerView with an adapter
        ingredientsAdapter = IngredientsAdapter(this, ingredientList)
        binding.ingredientsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.ingredientsRecyclerView.adapter = ingredientsAdapter

        // Calculate total calories
        val totalCalories = ingredientList.sumByDouble { it.calories ?: 0.0 }


        //this will display either calories or kilojoules depending on the user's preferences
        val caloriePreferences = applicationContext.getSharedPreferences("CaloriesPref", Context.MODE_PRIVATE)
        val calories = caloriePreferences.getBoolean("CALORIES_UNIT", true)

        if(calories == true)
        {
            val roundedCalories = totalCalories.let { BigDecimal(it).setScale(2, RoundingMode.HALF_UP).toDouble() }
            binding.totalCaloriesTextView.text = "Total Calories: $roundedCalories cal"
        }
        else
        {
            val kiloJoules = totalCalories.times(4.184)
            val roundedKiloJoules = kiloJoules.let { BigDecimal(it).setScale(2, RoundingMode.HALF_UP).toDouble() }
            binding.totalCaloriesTextView.text = "Total KiloJoules: $roundedKiloJoules kJ"
        }

        // Button to go back and add more ingredients
        binding.addMoreIngredientsButton.setOnClickListener {
            finish() // Go back to the previous activity
        }

        // Next button to proceed to recipe info
        binding.nextButton.setOnClickListener {
            val intent = Intent(this, RecipeInfoActivity::class.java)
            intent.putExtra("ingredients", ingredientList)
            intent.putExtra("totalCalories", totalCalories)
            startActivity(intent)
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