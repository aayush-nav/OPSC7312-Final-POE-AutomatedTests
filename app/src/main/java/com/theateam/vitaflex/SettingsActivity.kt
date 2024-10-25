package com.theateam.vitaflex

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.theateam.vitaflex.databinding.ActivitySettingsBinding
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Load saved language preference
        loadLanguage()

        //this will take the user to the app settings page
        binding.SettingAppSettingsImageButton.setOnClickListener {
            val intent = Intent(this, AppSettingActivity::class.java)
            startActivity(intent)
        }

        //this will take the user to the units of measurement settings page
        binding.SettingsUnitsOfMeasurementsImageButton.setOnClickListener {
            val intent = Intent(this, UnitOfMeasurementActivity::class.java)
            startActivity(intent)
        }

        //this will take the user to the account settings page
        binding.SettingsAccountImageButton.setOnClickListener() {
            val intent = Intent(this, AccountSettingActivity::class.java)
            startActivity(intent)
        }

        //this will take the user to the profile settings page
        binding.SettingsProfileImageButton.setOnClickListener() {
            val intent = Intent(this, ProfileSettingActivity::class.java)
            startActivity(intent)
        }

        //this will take the user to the support page
        binding.SettingsSupportImageButton.setOnClickListener() {
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
        }

        //this will take the user to the about page
        binding.SettingsAboutImageButton.setOnClickListener() {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }

        // Set up the ImageButton as a back button
        val backButton: ImageButton = findViewById(R.id.Settings_back_btn)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()  // Close the current activity so it's not in the back stack
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