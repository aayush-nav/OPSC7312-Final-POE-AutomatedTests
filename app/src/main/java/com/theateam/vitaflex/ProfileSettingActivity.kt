package com.theateam.vitaflex

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theateam.vitaflex.databinding.ActivityProfileSettingBinding
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

class ProfileSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSettingBinding

    private lateinit var btnChangeAge: ImageButton
    private lateinit var btnChangeGender: ImageButton
    private lateinit var btnChangeWeight: ImageButton
    private lateinit var btnChangeHeight: ImageButton
    private lateinit var txtBMI: TextView
    private lateinit var txtAge: TextView
    private lateinit var txtGender: TextView
    private lateinit var txtWeight: TextView
    private lateinit var txtHeight: TextView
    private lateinit var backButton: ImageButton

    private var BMI: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProfileSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setContentView(R.layout.activity_profile_setting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Load saved language preference
        loadLanguage()


        btnChangeAge = findViewById(R.id.imbChangeAge)
        btnChangeGender = findViewById(R.id.imbChangeGender)
        btnChangeWeight = findViewById(R.id.imbChangeWeight)
        btnChangeHeight = findViewById(R.id.imbChangeHeight)
        txtBMI = findViewById(R.id.txvProfileUserBMI)
        txtAge = findViewById(R.id.txvProfileUserAge)
        txtGender = findViewById(R.id.txvProfileUserGender)
        txtWeight = findViewById(R.id.txvProfileUserWeight)
        txtHeight = findViewById(R.id.txvProfileUserHeight)
        backButton = findViewById(R.id.Profile_back_btn)


        backButton.setOnClickListener() {
            finish()
        }

        txtBMI = findViewById(R.id.txvProfileUserBMI)



        //this will populate the text fields with user information when the screen is started
        getAge()
        getGender()
        getWeight()
        getHeight()
        getBMI()


        //this will show a popup to the user and run the updateAge method with the input from the user
        btnChangeAge.setOnClickListener()
        {
            popUpScreen("Age", "Age", this)
            {
                    input ->
                if(input.isEmpty())
                {
                    Toast.makeText(this, getString(R.string.please_enter_an_age), Toast.LENGTH_LONG).show()
                }
                else
                {
                    updateAge(input.toInt())
                }
            }
        }

        //this will show a popup to the user and run the updateGender method with the input from the user
        btnChangeGender.setOnClickListener()
        {
            popUpScreen("Gender", "Gender", this)
            {
                    input ->
                if(input.isEmpty())
                {
                    Toast.makeText(this,
                        getString(R.string.please_enter_a_gender), Toast.LENGTH_LONG).show()
                }
                else
                {
                    updateGender(input)
                }
            }
        }

        //this will show a popup to the user and run the updateWeight method with the input from the user
        btnChangeWeight.setOnClickListener()
        {
            popUpScreen("Weight", "Weight in kg", this)
            {
                    input ->
                if(input.isEmpty())
                {
                    Toast.makeText(this,
                        getString(R.string.please_enter_a_weight), Toast.LENGTH_LONG).show()
                }
                else
                {
                    updateWeight(input.toInt())
                }
            }
        }

        //this will show a popup to the user and run the updateHeigt method with the input from the user
        btnChangeHeight.setOnClickListener()
        {
            popUpScreen("Height", "Height", this)
            {
                    input ->
                if(input.isEmpty())
                {
                    Toast.makeText(this,
                        getString(R.string.please_enter_a_height), Toast.LENGTH_LONG).show()
                }
                else
                {
                    updateHeight(input.toInt())
                }
            }
        }





    }



    //this will update the user's height in the database with the newly input data from the user
    private fun updateHeight(newHeight: Int) {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("VitaflexApp")

        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)
        val checkUserEmail = userEmail?.replace(".", "").toString()

        val updateMap = mapOf<String, Any>(
            "height" to newHeight // Specify the field to update
        )

        dbRef.child("User Personal Info Entries").child(checkUserEmail).updateChildren(updateMap)
            .addOnSuccessListener {
                Log.d("UpdateUserName", "User's height successfully updated!")
                Toast.makeText(this,
                    getString(R.string.height_updated_successfully), Toast.LENGTH_SHORT).show()

                getBMI()
                getHeight()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this,
                    getString(R.string.height_update_failed, exception.message), Toast.LENGTH_SHORT).show()
                Log.e("UpdateUserName", "Failed to update height", exception)
            }
    }

    //this will update the user's weight in the database with the newly input data from the user
    private fun updateWeight(newWeight: Int) {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("VitaflexApp")

        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)
        val checkUserEmail = userEmail?.replace(".", "").toString()

        val updateMap = mapOf<String, Any>(
            "weight" to newWeight // Specify the field to update
        )

        dbRef.child("User Personal Info Entries").child(checkUserEmail).updateChildren(updateMap)
            .addOnSuccessListener {
                Log.d("UpdateUserName", "User's weight successfully updated!")
                Toast.makeText(this,
                    getString(R.string.weight_updated_successfully), Toast.LENGTH_SHORT).show()

                getBMI()
                getWeight()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this,
                    getString(R.string.weight_update_failed, exception.message), Toast.LENGTH_SHORT).show()
                Log.e("UpdateUserName", "Failed to update weight", exception)
            }
    }

    //this will update the user's gender in the database with the newly input data from the user
    private fun updateGender(newGender: String) {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("VitaflexApp")

        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)
        val checkUserEmail = userEmail?.replace(".", "").toString()

        val updateMap = mapOf<String, Any>(
            "gender" to newGender // Specify the field to update
        )

        dbRef.child("User Personal Info Entries").child(checkUserEmail).updateChildren(updateMap)
            .addOnSuccessListener {
                Log.d("UpdateUserName", "User's gender successfully updated!")
                Toast.makeText(this,
                    getString(R.string.gender_updated_successfully), Toast.LENGTH_SHORT).show()

                getGender()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this,
                    getString(R.string.gender_update_failed, exception.message), Toast.LENGTH_SHORT).show()
                Log.e("UpdateUserName", "Failed to update gender", exception)
            }
    }

    //this will update the user's age in the database with the newly input data from the user
    private fun updateAge(newAge:Int) {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("VitaflexApp")

        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)
        val checkUserEmail = userEmail?.replace(".", "").toString()

        val updateMap = mapOf<String, Any>(
            "age" to newAge // Specify the field to update
        )

        dbRef.child("User Personal Info Entries").child(checkUserEmail).updateChildren(updateMap)
            .addOnSuccessListener {
                Log.d("UpdateUserName", "User's age successfully updated!")
                Toast.makeText(this,
                    getString(R.string.age_updated_successfully), Toast.LENGTH_SHORT).show()


                getAge()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this,
                    getString(R.string.age_update_failed, exception.message), Toast.LENGTH_SHORT).show()
                Log.e("UpdateUserName", "Failed to update age", exception)
            }
    }

    //this will get the user's height and weight from the database and calculate their BMI to be displayed back to them
    private fun getBMI() {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("VitaflexApp")

        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)
        val checkUserEmail = userEmail?.replace(".", "").toString()


        var weight = 0.0
        var heightInMetres = 0.0
        var calcHeight = 0.0

        dbRef.child("User Personal Info Entries").child(checkUserEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(UserInfo::class.java)
                    if (user != null) {

                        weight = user.weight.toString().toDouble()
                        heightInMetres = user.height.toString().toDouble()/100

                        calcHeight = heightInMetres * heightInMetres

                        BMI = weight/calcHeight

                        val roundedBMI = BigDecimal(BMI).setScale(2, RoundingMode.HALF_UP).toDouble()


                        txtBMI.setText("$roundedBMI")


                        Log.d("UserInfo", "User info: $user")
                    } else {
                        Log.e("UserInfo", "No data found for key: $checkUserEmail")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("UserInfo", "Failed to get data: ${error.message}")
                }
            })



    }

    //this will get the user's age from the database to be displayed back to them
    private fun getAge() {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("VitaflexApp")

        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)
        val checkUserEmail = userEmail?.replace(".", "").toString()

        dbRef.child("User Personal Info Entries").child(checkUserEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(UserInfo::class.java)
                    if (user != null) {

                        txtAge.setText("${user.age}")


                        Log.d("UserInfo", "User info: $user")
                    } else {
                        Log.e("UserInfo", "No data found for key: $checkUserEmail")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("UserInfo", "Failed to get data: ${error.message}")
                }
            })
    }


    //this will get the user's gender from the database to be displayed back to them
    private fun getGender() {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("VitaflexApp")

        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)
        val checkUserEmail = userEmail?.replace(".", "").toString()

        dbRef.child("User Personal Info Entries").child(checkUserEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(UserInfo::class.java)
                    if (user != null) {

                        txtGender.setText("${user.gender}")


                        Log.d("UserInfo", "User info: $user")
                    } else {
                        Log.e("UserInfo", "No data found for key: $checkUserEmail")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("UserInfo", "Failed to get data: ${error.message}")
                }
            })
    }

    //this will get the user's weight from the database to be displayed back to them
    private fun getWeight() {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("VitaflexApp")

        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)
        val checkUserEmail = userEmail?.replace(".", "").toString()

        dbRef.child("User Personal Info Entries").child(checkUserEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(UserInfo::class.java)
                    if (user != null) {


                        val weightPreferences = applicationContext.getSharedPreferences("WeightPref", Context.MODE_PRIVATE)
                        val isKg = weightPreferences.getBoolean("WEIGHT_UNIT", true)

                        if(isKg == true)
                        {
                            txtWeight.setText("${user.weight} kg")
                        }
                        else
                        {
                            val weightPounds = user.weight?.times(2.204)
                            val roundedWeightPounds = weightPounds?.let { BigDecimal(it).setScale(2, RoundingMode.HALF_UP).toDouble() }
                            txtWeight.setText("${roundedWeightPounds} lbs")
                        }



                        Log.d("UserInfo", "User info: $user")
                    } else {
                        Log.e("UserInfo", "No data found for key: $checkUserEmail")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("UserInfo", "Failed to get data: ${error.message}")
                }
            })
    }


    //this will get the user's height from the database to be displayed back to them
    private fun getHeight() {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("VitaflexApp")

        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)
        val checkUserEmail = userEmail?.replace(".", "").toString()

        dbRef.child("User Personal Info Entries").child(checkUserEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(UserInfo::class.java)
                    if (user != null) {

                        txtHeight.setText("${user.height} cm")


                        Log.d("UserInfo", "User info: $user")
                    } else {
                        Log.e("UserInfo", "No data found for key: $checkUserEmail")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("UserInfo", "Failed to get data: ${error.message}")
                }
            })
    }


    //Author: Aaron
    //Source: https://stackoverflow.com/questions/10903754/input-text-dialog-android
    //this will show a popup screen that will allow the user to change their personal information
    private fun popUpScreen(forgotString: String, enterString: String, context: Context, onInputListener: (input: String) -> Unit) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Changing $forgotString?")
        alertDialogBuilder.setMessage("Enter your $enterString")

        val inputEditText = EditText(context)
        alertDialogBuilder.setView(inputEditText)

        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            val userInput = inputEditText.text.toString()
            onInputListener(userInput)
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }



    //data class to store the user's personal information
    data class UserInfo (var age:Int? = null, var gender:String? = null, var height:Int? = null, var weight:Int? = null)
    {
        constructor() : this(0, "",0,0,)
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