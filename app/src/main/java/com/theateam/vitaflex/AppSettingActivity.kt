package com.theateam.vitaflex

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.theateam.vitaflex.databinding.ActivityAppSettingBinding
import java.util.Calendar
import java.util.Locale

class AppSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppSettingBinding

    private var workoutTime: Calendar? = null
    private var mealTime: Calendar? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAppSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setContentView(R.layout.activity_app_setting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Load saved language preference
        loadSavedLanguage()

        binding.appSettingsBackBtn.setOnClickListener(){
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Request notification and alarm permissions
        requestPermissions()




        sharedPreferences = getSharedPreferences("AppSettingsPrefs", Context.MODE_PRIVATE)
        sharedPrefs = getSharedPreferences("appSettingsModePrefs", MODE_PRIVATE)

        binding.appSettingsWorkoutNotificationTimeTextView.setOnClickListener {
            showTimePickerDialog { hour, minute ->
                workoutTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }
                binding.appSettingsWorkoutNotificationTimeTextView.text = String.format("%02d:%02d", hour, minute)
            }
        }

        binding.appSettingsMealNotificationTimeTextView.setOnClickListener {
            showTimePickerDialog { hour, minute ->
                mealTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }
                binding.appSettingsMealNotificationTimeTextView.text = String.format("%02d:%02d", hour, minute)
            }
        }

        binding.appSettingsSaveSettingsBtn.setOnClickListener {
            if (binding.appSettingsWorkoutNotificationSwitch.isChecked && workoutTime != null) {
                setAlarm(workoutTime!!, "Workout Reminder")
            }
            if (binding.appSettingsMealNotificationSwitch.isChecked && mealTime != null) {
                setAlarm(mealTime!!, "Meal Reminder")
            }


            // Get selected language
            val selectedLanguage = if (binding.appSettingsEnglishRadioBtn.isChecked) "en" else "af"

            // Save the selected language in SharedPreferences
            saveLanguagePreference(selectedLanguage)

            // Apply the language change and restart the app to reflect changes
            setLocale(selectedLanguage)

            saveSettings()
            Toast.makeText(this, getString(R.string.settings_saved), Toast.LENGTH_SHORT).show()



        }

        //this will check whether dark or light mode has been selected and will apply it to the app
        val isDarkModeEnabled = sharedPrefs.getBoolean("DARK_MODE", false)
        if (isDarkModeEnabled) {
            binding.appSettingsDarkModeRadioBtn.isChecked = true
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            binding.appSettingsLightModeRadioBtn.isChecked = true
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        binding.appSettingsAppModeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.appSettings_lightMode_radioBtn -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    saveThemePreference(false)
                }
                R.id.appSettings_darkMode_radioBtn -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    saveThemePreference(true)
                }
            }
        }
    }

    //time picker pop up
    private fun showTimePickerDialog(onTimeSelected: (Int, Int) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            onTimeSelected(selectedHour, selectedMinute)
        }, hour, minute, true)

        timePickerDialog.show()
    }

    //this method will set the alarm depending on when the user has selected on the notification to be sent
    private fun setAlarm(time: Calendar, notificationMessage: String) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderBroadcastReceiver::class.java).apply {
            putExtra("NOTIFICATION_MESSAGE", notificationMessage)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            time.timeInMillis.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time.timeInMillis, pendingIntent)
    }

    //this will request that the user allows the app to send notifications and alarms
    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                // Navigate to the settings page to allow scheduling exact alarms
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }
    }

    //this will store the user's settings choices in stored preferences
    private fun saveSettings() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("workoutSwitch", binding.appSettingsWorkoutNotificationSwitch.isChecked)
        editor.putBoolean("mealSwitch", binding.appSettingsMealNotificationSwitch.isChecked)

        workoutTime?.let {
            editor.putInt("workoutHour", it.get(Calendar.HOUR_OF_DAY))
            editor.putInt("workoutMinute", it.get(Calendar.MINUTE))
        }

        mealTime?.let {
            editor.putInt("mealHour", it.get(Calendar.HOUR_OF_DAY))
            editor.putInt("mealMinute", it.get(Calendar.MINUTE))
        }

        editor.apply()
    }

    private fun loadSettings() {
        val workoutSwitchState = sharedPreferences.getBoolean("workoutSwitch", false)
        val mealSwitchState = sharedPreferences.getBoolean("mealSwitch", false)

        binding.appSettingsWorkoutNotificationSwitch.isChecked = workoutSwitchState
        binding.appSettingsMealNotificationSwitch.isChecked = mealSwitchState

        val workoutHour = sharedPreferences.getInt("workoutHour", -1)
        val workoutMinute = sharedPreferences.getInt("workoutMinute", -1)
        if (workoutHour != -1 && workoutMinute != -1) {
            workoutTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, workoutHour)
                set(Calendar.MINUTE, workoutMinute)
            }
            binding.appSettingsWorkoutNotificationTimeTextView.text = String.format("%02d:%02d", workoutHour, workoutMinute)
        }

        val mealHour = sharedPreferences.getInt("mealHour", -1)
        val mealMinute = sharedPreferences.getInt("mealMinute", -1)
        if (mealHour != -1 && mealMinute != -1) {
            mealTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, mealHour)
                set(Calendar.MINUTE, mealMinute)
            }
            binding.appSettingsMealNotificationTimeTextView.text = String.format("%02d:%02d", mealHour, mealMinute)
        }
    }

    //this will store the preference of which theme is selected
    private fun saveThemePreference(isDarkModeEnabled: Boolean) {
        val editor = sharedPrefs.edit()
        editor.putBoolean("DARK_MODE", isDarkModeEnabled)
        editor.apply()
    }


    private fun saveLanguagePreference(language: String) {
        val sharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("language", language)
        editor.apply()
    }

    private fun loadSavedLanguage() {
        val sharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val savedLanguage = sharedPref.getString("language", "en")  // Default to English

        // Set the appropriate radio button based on the saved preference
        if (savedLanguage == "af") {
            binding.appSettingsAfrikaansRadioBtn.isChecked = true
        } else {
            binding.appSettingsEnglishRadioBtn.isChecked = true
        }
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Restart the activity to apply the language change across the app
        val intent = Intent(this, SettingsActivity::class.java)  // You may change this to your launch activity
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
