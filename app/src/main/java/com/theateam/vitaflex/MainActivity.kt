package com.theateam.vitaflex

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.theateam.vitaflex.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {
    // Object for Navigation view
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTheme(R.style.home_page_theme)

//        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Load saved language preference
        loadLanguage()

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open_drawer, R.string.close_drawer)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val headerView = binding.navView.getHeaderView(0)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        headerView.findViewById<TextView>(R.id.tvName).text = intent.getStringExtra("userEmail")

        binding.navView.setNavigationItemSelectedListener {
            val intentProfile = Intent(this@MainActivity, ProfileSettingActivity::class.java)
            //intentHome.putExtra("userEmail", email)
            val intentSettings = Intent(this@MainActivity, SettingsActivity::class.java)


            //starts intents for taking the user to the home and support screens, and will allow the user
            when (it.itemId) {
                R.id.navSettings -> startActivity(intentSettings)
                R.id.navProfile -> startActivity(intentProfile)
                R.id.navLogout -> LogoutAlertDialog()
            }
            true
        }


        // Name in Action Bar
        supportActionBar?.setTitle("Welcome to Vitaflex")


        // onclick event handlers for the 4 buttons

        //this button will take the user to the recipes screen
        binding.recipesLayout.setOnClickListener() {
            val intent = Intent(this, ListOfAllRecipesActivity::class.java)
            startActivity(intent)
        }

        //this button will take the user to the meals screen
        binding.mealsLayout.setOnClickListener() {
            val intent = Intent(this, MealsActivity::class.java)
            startActivity(intent)
        }

        //this button will take the user to the settings screen
        binding.settingsLayout.setOnClickListener() {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        //this button will take the user to the progress screen
        binding.progressLayout.setOnClickListener() {
            val intent = Intent(this, ProgressActivity::class.java)
            startActivity(intent)
        }

        binding.workoutLayout.setOnClickListener(){
            val intent = Intent(this, WorkoutsActivity::class.java)
            startActivity(intent)
        }
    }


    // Logout Dialog:
    // Author: DreamDevelopers
    // Link: https://www.youtube.com/watch?v=sFfP5qZojHQ&ab_channel=DreamDevelopers

    //this method will show a dialog to the user that will ask them whether they would like to log out or not
    private fun LogoutAlertDialog() {
        val alertdialog: AlertDialog = AlertDialog.Builder(this).create()
        alertdialog.setTitle("Logout")
        alertdialog.setMessage("Are you sure you want to Logout?")

        alertdialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes"){
                dialog, which ->
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            dialog.dismiss()
//            binding.navView.getHeaderView(0).findViewById<TextView>(R.id.tvEmail).text = ""
        }

        alertdialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No"){
                dialog, which ->
            dialog.dismiss()
        }
        alertdialog.show()

    }


    // this method must be overridden so that the navigation drawer can be opened
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle("Logout")
        alertDialog.setMessage("Are you sure you want to Logout?")

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes") { dialog, which ->
            super.onBackPressed()
            dialog.dismiss()
        }

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No") { dialog, which ->
            dialog.dismiss()
        }
        alertDialog.show()
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