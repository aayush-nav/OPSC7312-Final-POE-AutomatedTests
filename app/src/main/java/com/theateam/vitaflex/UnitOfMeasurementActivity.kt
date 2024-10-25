package com.theateam.vitaflex

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.theateam.vitaflex.databinding.ActivityAppSettingBinding
import com.theateam.vitaflex.databinding.ActivityUnitOfMeasurementBinding
import java.util.Locale

class UnitOfMeasurementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUnitOfMeasurementBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //setContentView(R.layout.activity_unit_of_measurement)

        binding = ActivityUnitOfMeasurementBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Load saved language preference
        loadLanguage()


        //this will check which option has been selected for the weight unit of measurement
        binding.measurementWeightRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.measurementWeightKilogramRadioBtn -> {
                    saveWeightPreference(true)
                }
                R.id.measurementWeightPoundsRadioBtn -> {
                    saveWeightPreference(false)
                }
            }
        }

        //back button to return to settings screen
        binding.measurementBackBtn.setOnClickListener {
            finish()
        }


        //this will check whether the user would like to show the calories or kiloJoules of each meal or recipe
        binding.measurementCaloriesRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.measurementCaloriesCalRadioBtn -> {
                    saveCaloriesPreference(true)
                }
                R.id.measurementCaloriesJoulesRadioBtn -> {
                    saveCaloriesPreference(false)
                }
            }
        }
    }


    //this function will save the user's preference for the weight unit of measurement
    private fun saveWeightPreference(isKG: Boolean) {
        val weightPreferences = applicationContext.getSharedPreferences("WeightPref", Context.MODE_PRIVATE)
        val editor = weightPreferences.edit()
        editor.putBoolean("WEIGHT_UNIT", isKG)
        editor.apply()
    }


    //this function will save the user's preference for the calories unit of measurement
    private fun saveCaloriesPreference(isCal: Boolean) {
        val caloriesPreferences = applicationContext.getSharedPreferences("CaloriesPref", Context.MODE_PRIVATE)
        val editor = caloriesPreferences.edit()
        editor.putBoolean("CALORIES_UNIT", isCal)
        editor.apply()
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