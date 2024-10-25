package com.theateam.vitaflex

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.*

class AddExerciseActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var dtpExerciseDate: EditText
    private lateinit var editTextExerciseName: EditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var editTextSetsReps: EditText
    private lateinit var editTextDuration: EditText
    private lateinit var btnAddExercise: Button
    private lateinit var backBtn: Button
//    private val email = "user@example.com"  // hardcoded for now
    // Retrieve the user's email from SharedPreferences
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_exercise)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        db = DatabaseHelper(this)

        dtpExerciseDate = findViewById(R.id.dtpExerciseDate)
        editTextExerciseName = findViewById(R.id.editTextExerciseName)
        radioGroup = findViewById(R.id.radioGroupExerciseType)
        editTextSetsReps = findViewById(R.id.editTextSetsReps)
        editTextDuration = findViewById(R.id.editTextDuration)
        btnAddExercise = findViewById(R.id.btnAddExercise)
        backBtn = findViewById(R.id.selectedWorkoutBackBtn)


        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        email = sharedPreferences.getString("USER_EMAIL", null).toString()

        // Set default date to today
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dtpExerciseDate.setText(dateFormat.format(calendar.time))

        // Show DatePickerDialog when clicking the EditText
        dtpExerciseDate.setOnClickListener {
            showDatePickerDialog(calendar)
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radioSetsReps) {
                editTextSetsReps.visibility = View.VISIBLE
                editTextDuration.visibility = View.GONE
            } else if (checkedId == R.id.radioDuration) {
                editTextDuration.visibility = View.VISIBLE
                editTextSetsReps.visibility = View.GONE
            }
        }

        btnAddExercise.setOnClickListener {
            val date = dtpExerciseDate.text.toString()
            val exerciseName = editTextExerciseName.text.toString()
            val amount = if (radioGroup.checkedRadioButtonId == R.id.radioSetsReps)
                editTextSetsReps.text.toString()
            else
                editTextDuration.text.toString()

            if (db.insertData(date, exerciseName, amount, email!!)) {
                Toast.makeText(this, "Exercise Added", Toast.LENGTH_SHORT).show()
                finish()  // Go back to previous screen
            } else {
                Toast.makeText(this, "Failed to Add Exercise", Toast.LENGTH_SHORT).show()
            }
        }

        backBtn.setOnClickListener {
            finish()
        }


    }


    // Show DatePickerDialog
    private fun showDatePickerDialog(calendar: Calendar) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Format selected date as YYYY-MM-DD
                val formattedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                dtpExerciseDate.setText(formattedDate)
            },
            year, month, day
        )

        datePickerDialog.show()
    }
}