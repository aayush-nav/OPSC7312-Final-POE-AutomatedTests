package com.theateam.vitaflex

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.theateam.vitaflex.databinding.ActivitySignUpStep1Binding
import java.util.Calendar
import java.util.Locale

class SignUpStep1Activity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpStep1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignUpStep1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setTheme(R.style.splash_screen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Load saved language preference
        loadLanguage()

        // Gender Spinner setup
        val genders = arrayListOf('M', 'F')
        val arrayAdapterGenders = ArrayAdapter(this, android.R.layout.simple_spinner_item, genders)
        arrayAdapterGenders.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.signUpStep1GenderSelectSpinner.adapter = arrayAdapterGenders

        binding.signUpStep1GenderSelectSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Toast.makeText(this@SignUpStep1Activity,
                    getString(R.string.you_have_selected_2, genders[position]), Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No action required
            }
        }

        // Date of Birth EditText (opens DatePickerDialog)
        binding.signUpStep1DateOfBirthEditText.setOnClickListener {
            showDatePicker(binding.signUpStep1DateOfBirthEditText)
        }

        // this will store the user's personal info in the database and take the user to the next screen when the button is clicked
        binding.signUpStep1ContinueBtn.setOnClickListener {
            val gender = binding.signUpStep1GenderSelectSpinner.selectedItem.toString()
            val dob = binding.signUpStep1DateOfBirthEditText.text.toString()
            val weight = binding.signUpStep1WeightEditText.text.toString().toIntOrNull()
            val height = binding.signUpStep1HeightEditText.text.toString().toIntOrNull()

            if (weight == null || height == null) {
                Toast.makeText(this,
                    getString(R.string.please_enter_valid_weight_and_height), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //method to write to firebase
            writeToFirebase(gender, dob, weight, height)

            val intent = Intent(this@SignUpStep1Activity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    //this method will write the user's personal information to the firebase database using the email as a key to separate each user's records
    private fun writeToFirebase(gender: String, dob: String, weight: Int, height: Int) {
        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)
        val email = userEmail?.replace(".", "").toString()

        val database = Firebase.database
        val dbRef = database.getReference("VitaflexApp")
        val entry = UserPersonalInfo(gender, dob, weight, height)

        dbRef.child("User Personal Info Entries").child(email).setValue(entry)
            .addOnSuccessListener {
                Log.d("CreateUserPInfo", "Personal Info successfully recorded!")
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this,
                    getString(R.string.database_write_failed, exception.message), Toast.LENGTH_SHORT).show()
                Log.e("CreateUserPInfo", "Database write failed", exception)
            }
    }

    //this method will display the date picker to the user to select their birthday
    private fun showDatePicker(editText: EditText) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, theYear, monthOfYear, dayOfMonth ->
            val formattedDate = "$dayOfMonth-${monthOfYear + 1}-$theYear"
            editText.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
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

    //this is a data class to store the user's personal information such as gender, date of birth, weight, and height
    data class UserPersonalInfo(val gender: String, val dob: String, val weight: Int, val height: Int)
}
