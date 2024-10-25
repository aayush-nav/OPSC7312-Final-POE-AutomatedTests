package com.theateam.vitaflex

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.theateam.vitaflex.databinding.ActivityAccountSettingBinding
import java.util.Locale

class AccountSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountSettingBinding


    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAccountSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setContentView(R.layout.activity_account_setting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Load saved language preference
        loadLanguage()

        auth = Firebase.auth

        val googlePreferences = applicationContext.getSharedPreferences("GooglePref", Context.MODE_PRIVATE)
        val buttonEnabled = googlePreferences.getBoolean("GOOGLE", true)

        binding.AccountChangeEmailImageButton.isEnabled = buttonEnabled
        binding.AccountChangePasswordImageButton.isEnabled = buttonEnabled



        binding.AccountChangeNameImageButton.setOnClickListener()
        {
            popUpScreen("Name", "Name", this)
            {
                    input ->
                if(input.isEmpty())
                {
                    Toast.makeText(this, getString(R.string.please_enter_a_name), Toast.LENGTH_LONG).show()
                }
                else
                {
                    val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
                    val userEmail = sharedPreferences.getString("USER_EMAIL", null)
                    Toast.makeText(this, userEmail, Toast.LENGTH_SHORT).show()
                    updateUserName(userEmail.toString() ,input)
                }
            }
        }

        binding.AccountChangeEmailImageButton.setOnClickListener()
        {
            popUpScreenEmail(this)
            {inputEmail, inputPassword ->
                if(inputEmail.isEmpty() || inputPassword.isEmpty())
                {
                    Toast.makeText(this,
                        getString(R.string.please_enter_an_email), Toast.LENGTH_LONG).show()
                }
                else
                {
                    val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
                    val userEmail = sharedPreferences.getString("USER_EMAIL", null)
                    updateUserEmail(userEmail.toString(), inputEmail, inputPassword)

                    val editor = sharedPreferences.edit()
                    editor.putString("USER_EMAIL", inputEmail)
                    editor.apply()
                }
            }
        }

        binding.AccountChangePasswordImageButton.setOnClickListener()
        {
            popUpScreen("Password", "Email", this)
            {
                    input ->
                if(input.isEmpty())
                {
                    Toast.makeText(this,
                        getString(R.string.please_enter_an_email_1), Toast.LENGTH_LONG).show()
                }
                else
                {
                    resetPassword(input)
                }
            }
        }



        binding.AccountBackBtn.setOnClickListener() {
            finish()
        }

    }



    //this method will update the user's email using information provided from the popUpScreenEmail method
    private fun updateUserEmail(currentEmail: String, newEmail: String, checkPass: String) {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("VitaflexApp")

        val passCurrentEmail = currentEmail.replace(".", "")
        val passNewEmail = newEmail.replace(".", "")

        val updateMap = mapOf<String, Any>(
            "email" to newEmail // Specify the field to update
        )

        val user = auth.currentUser

        user?.reload()

        if (user != null) {
            // Re-authenticate the user before updating the email
            val credential = EmailAuthProvider.getCredential(currentEmail, checkPass)
            user.reauthenticate(credential).addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    Log.d("UpdateUserEmail", "Re-authentication successful")

                    // Now perform the email update in Firebase Authentication
                    user.updateEmail(newEmail).addOnCompleteListener { emailUpdateTask ->
                        if (emailUpdateTask.isSuccessful) {
                            Log.d("UpdateUserEmail", "User email successfully updated in Auth!")
                            Toast.makeText(this,
                                getString(R.string.email_updated_successfully_in_firebase_authentication), Toast.LENGTH_SHORT).show()

                            // Update the email in the Firebase Realtime Database
                            dbRef.child("Users").child(passCurrentEmail).updateChildren(updateMap)
                                .addOnSuccessListener {
                                    Log.d("UpdateUserEmail", "User's email successfully updated!")
                                    Toast.makeText(this,
                                        getString(R.string.email_updated_successfully), Toast.LENGTH_SHORT).show()

                                    // Move the entire user data under the new email key
                                    dbRef.child("Users").child(passCurrentEmail).get().addOnSuccessListener { snapshot ->
                                        dbRef.child("Users").child(passNewEmail).setValue(snapshot.value)
                                        dbRef.child("Users").child(passCurrentEmail).removeValue()
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(this,
                                        getString(R.string.email_update_failed, exception.message), Toast.LENGTH_SHORT).show()
                                    Log.e("UpdateUserEmail", "Failed to update email", exception)
                                }


                            dbRef.child("User Personal Info Entries").child(passCurrentEmail).get().addOnSuccessListener { snapshot ->
                                dbRef.child("User Personal Info Entries").child(passNewEmail).setValue(snapshot.value)
                                dbRef.child("User Personal Info Entries").child(passCurrentEmail).removeValue()
                            }

                            dbRef.child("User Goals Entries").child(passCurrentEmail).get().addOnSuccessListener { snapshot ->
                                dbRef.child("User Goals Entries").child(passNewEmail).setValue(snapshot.value)
                                dbRef.child("User Goals Entries").child(passCurrentEmail).removeValue()
                            }

                        } else {
                            Log.e("UpdateUserEmail", "Failed to update email in Auth: ${emailUpdateTask.exception?.message}")
                            Toast.makeText(this,
                                getString(
                                    R.string.failed_to_update_email_in_firebase_authentication,
                                    emailUpdateTask.exception?.message
                                ), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("UpdateUserEmail", "Re-authentication failed: ${authTask.exception?.message}")
                    Toast.makeText(this, "Re-authentication failed: ${authTask.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this,
                getString(R.string.there_is_no_currently_signed_in_user), Toast.LENGTH_SHORT).show()
        }
    }

    //this method will update the user's name using information provided from the popUpScreen method
    private fun updateUserName(email: String, newName: String) {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("VitaflexApp")

        // Replace dots in email to avoid Firebase path issues
        val passEmail = email.replace(".", "")

        val updateMap = mapOf<String, Any>(
            "names" to newName // Specify the field to update
        )

        dbRef.child("Users").child(passEmail).updateChildren(updateMap)
            .addOnSuccessListener {
                Log.d("UpdateUserName", "User's name successfully updated!")
                Toast.makeText(this,
                    getString(R.string.name_updated_successfully), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Name update failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("UpdateUserName", getString(R.string.failed_to_update_name), exception)
            }
    }



    //this method will update the user's password using information provided from the popUpScreen method
    private fun resetPassword(input: String)
    {
        auth.currentUser?.updatePassword(input)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this,
                        getString(R.string.password_has_been_reset), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this,
                        getString(R.string.password_reset_failed), Toast.LENGTH_LONG).show()
                }
            }
    }



    //Author: Aaron
    //Source: https://stackoverflow.com/questions/10903754/input-text-dialog-android
    //this will show a popup screen that will allow the user to enter an email to reset their password
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


    //this will show a popup screen that will allow the user to enter an email along with their password to change their email
    private fun popUpScreenEmail(context: Context, onInputListener: (inputEmail: String, inputPassword: String) -> Unit) {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.popup_emailchange, null)

        // Create an AlertDialog builder
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(dialogView)

        // Get references to the input fields from the inflated layout
        val emailField = dialogView.findViewById<EditText>(R.id.edtEmail)
        val passwordField = dialogView.findViewById<EditText>(R.id.edtPassword)

        // Set up the dialog buttons and their actions
        dialogBuilder
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                val email = emailField.text.toString()
                val password = passwordField.text.toString()

                onInputListener(email, password)

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

        // Create and show the dialog
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }


    data class User (var names:String? = null, var email:String? = null)
    {
        constructor() : this("","")
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