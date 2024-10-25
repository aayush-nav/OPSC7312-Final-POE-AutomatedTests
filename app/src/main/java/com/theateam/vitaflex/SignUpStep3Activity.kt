package com.theateam.vitaflex

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.theateam.vitaflex.databinding.ActivitySignUpStep3Binding
import java.util.Locale

class SignUpStep3Activity : AppCompatActivity() {

    //declarations for google sign in launcher to display sign in options
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient


    private lateinit var binding: ActivitySignUpStep3Binding


    //authentication variable used for firebase user authentication
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignUpStep3Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setTheme(R.style.splash_screen)

        val termsButton = findViewById<Button>(R.id.finalStepSignUp_TandC_btn)
        termsButton.setOnClickListener {
            showTermsAndConditions()
        }

        // setContentView(R.layout.activity_sign_up_step3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Load saved language preference
        loadLanguage()

        //this will initialize the google sign in launcher for if it is selected
        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            handleSignInResult(result)
        }


        //web client id for the google sign in authorisation
        val webclientid = "846530981193-oer7dnl613o3o3967ulqlll8j8p6bkft.apps.googleusercontent.com"


        //this will initialize the google sign in options and client
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webclientid).requestEmail().build()
        gsc = GoogleSignIn.getClient(this, gso)


        //this will assign the value auth to allow authentication via firebase
        auth = Firebase.auth

        //OpenAI, 2024
        val specialCharsRegex = Regex("[^a-zA-Z0-9]")

        //this will close the current screen and return to the previous one
        binding.signUpStep3BackBtn.setOnClickListener {
            finish()
        }

        binding.finalStepSignUpSignUpBtn.setOnClickListener {
            var name: String = binding.fullNameStepSignUpFullNameEditText.text.toString()
            var email: String = binding.finalStepSignUpEmailEditTextEmailAddress.text.toString()
            var password: String = binding.finalStepSignUpPasswordEditTextPassword.text.toString()
            var confirmPass: String =
                binding.finalStepSignUpConfirmPasswordEditTextPassword.text.toString()


            //this will do data validation to ensure that all fields have data in them
            if (binding.fullNameStepSignUpFullNameEditText.text.isEmpty() || binding.finalStepSignUpEmailEditTextEmailAddress.text.isEmpty()
                || binding.finalStepSignUpPasswordEditTextPassword.text.isEmpty() || binding.finalStepSignUpConfirmPasswordEditTextPassword.text.isEmpty()
                || !binding.finalStepSignUpAgreeToTCCheckbox.isChecked
            ) {
                Toast.makeText(
                    this,
                    getString(R.string.please_ensure_that_all_fields_have_been_entered_1),
                    Toast.LENGTH_SHORT
                ).show()
                //this will check if the password entered includes a special character and is at least 8 characters long
            } else if (password.length < 8 || !specialCharsRegex.containsMatchIn(password)) {
                Toast.makeText(
                    this,
                    getString(R.string.special_characters),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                //this will make sure that the password and password confirmation are matching each other
                if (password != confirmPass) {
                    Toast.makeText(this,
                        getString(R.string.error_the_passwords_do_not_match), Toast.LENGTH_SHORT)
                        .show()
                } else {
                    //if all values are okay, the sign up method will run
                    signUp(name, email, password)
                }

            }

        }

        //this will run the google sign up method when the google button is clicked
        binding.finalStepSignUpSignInWithGoogleImageButton.setOnClickListener()
        {
            signUpGoogle()
        }
    }

    private fun showTermsAndConditions() {
        // Create an AlertDialog to show the Terms and Conditions text
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Terms and Conditions")

        // Set the message (the terms and conditions text)
        builder.setMessage("""
            Welcome to Vitaflex! By using our app, you agree to the following terms:

            1. Usage: The app is for personal use only. You agree not to misuse the app for unlawful purposes.
            2. Data: Your data is used to provide services. We will not share your data without consent.
            3. API Usage: The app connects to third-party APIs, and we are not responsible for the data they provide.
            4. Health Disclaimer: Vitaflex is for informational purposes only, not a substitute for professional medical advice.
            5. Liability: We are not liable for any damages that arise from using Vitaflex.
            6. Updates: We may modify these terms at any time. Continuing to use the app indicates acceptance of changes.
            7. Termination: We reserve the right to terminate your access if these terms are violated.
        """.trimIndent())

        // Add an "OK" button to close the dialog
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss() // Close the dialog
        }

        // Show the AlertDialog
        builder.show()
    }



    //Adapted from: https://www.youtube.com/watch?v=_318sOlkJBQ&ab_channel=CodingSTUFF
    //Author: CodingSTUFF
    private fun signUpGoogle() {

        //this will sign out the current user and launch the new sign up launcher
        gsc.signOut().addOnCompleteListener {
            val signUpIntent = gsc.signInIntent
            googleSignInLauncher.launch(signUpIntent)
        }

        //this will create a sharedPreference that will tell the app that the user signed up using google
        val googlePreferences = applicationContext.getSharedPreferences("GooglePref", Context.MODE_PRIVATE)
        val googleEditor = googlePreferences.edit()
        googleEditor.putBoolean("GOOGLE", false)
        googleEditor.apply()
    }

    //Adapted from: https://www.youtube.com/watch?v=_318sOlkJBQ&ab_channel=CodingSTUFF
    //Author: CodingSTUFF
    private fun handleSignInResult(result: ActivityResult) {
        val data = result.data
        if (result.resultCode == Activity.RESULT_OK && data != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                // Check if account or idToken is null
                if (account != null && account.idToken != null) {
                    Log.d("Create User with Google", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                }
                else if (account == null)
                {
                    //this will return a message telling the user that their sign up failed
                    Log.w("Create User with Google", "Google sign in failed: Null account")
                    Toast.makeText(this,
                        getString(R.string.sign_in_failed_please_try_again_1), Toast.LENGTH_SHORT).show()
                }
                else if (account.idToken == null)
                {
                    //this will return a message telling the user that their sign up failed
                    Log.w("Create User with Google", "Google sign in failed: Null ID Token")
                    Toast.makeText(this, getString(R.string.sign_in_failed_please_try_again_1), Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Log.w("Create User with Google", "Google sign in failed", e)
            }
        } else {
            Log.w("Google Sign-In", "Sign-in failed or was canceled")
        }
    }

    //this method will use the credentials received from the google sign in methods in order to run the firebase authentication
    private fun firebaseAuthWithGoogle(idToken: String) {
        //the credential of the current user
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        //the firebase authentication will use the google credentials of the current user to sign up to the app
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Create User with Google", "signInWithCredential:success")
                    val user = auth.currentUser
                    val googleName = user?.displayName.toString()
                    val googleEmail = user?.email.toString()

                    //this method will receive the email and name from the current user's google account to store in firebase realtime database
                    writeToFirebase(googleName, googleEmail)

                    Toast.makeText(this,
                        getString(R.string.authentication_successful), Toast.LENGTH_SHORT).show()

                    //this will take the user to the next stage of the sign up process
                    val intent = Intent(this@SignUpStep3Activity, SignUpStep2Activity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Log.w("Create User with Google", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this,
                        getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show()
                }
            }
    }


    //this method will use the firebase create user method to make a new user using email and password
    private fun signUp(userName: String, userEmail: String, userPassword: String) {
        //the firebase authentication will use the email and password of the current user to sign up to the app
        auth.createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    //this will write the user's name and email to the firebase realtime database
                    writeToFirebase(userName, userEmail)


                    //this will create a sharedPreference that will tell the app that the user signed up using google
                    val googlePreferences = applicationContext.getSharedPreferences("GooglePref", Context.MODE_PRIVATE)
                    val googleEditor = googlePreferences.edit()
                    googleEditor.putBoolean("GOOGLE", true)
                    googleEditor.apply()

                    //this will return a message telling the user that their sign up was successful
                    Toast.makeText(this, getString(R.string.sign_up_successful), Toast.LENGTH_LONG).show()
                    val intent = Intent(this@SignUpStep3Activity, SignUpStep2Activity::class.java)
                    startActivity(intent)

                    //this will close the current activity
                    finish()
                } else {
                    //this will return a message telling the user that their sign up failed
                    Toast.makeText(this, getString(R.string.sign_up_failed), Toast.LENGTH_LONG).show()
                }
            }
    }


    //this method will write the user's name and email to the firebase realtime database
    private fun writeToFirebase(names: String, email: String) {
        //relevant data to find the correct database in firebase
        val database = Firebase.database
        val dbRef = database.getReference("VitaflexApp")
        val entry = User(names, email)

        //this will store the current user's email for use later in the app
        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("USER_EMAIL", email)
        editor.apply()

//        val sharedNamePreferences = applicationContext.getSharedPreferences("NamePref", Context.MODE_PRIVATE)
//        val editor1 = sharedNamePreferences.edit()
//        editor1.putString("USER_NAME", names)
//        editor1.apply()


        val passEmail = email.replace(".", "")

        //this will write the user data to the correct part of the firebase realtime database
        dbRef.child("Users").child(passEmail).setValue(entry)
            .addOnSuccessListener {
                Log.d("CreateUser", "User successfully stored!")
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this,
                    getString(R.string.database_write_failed_1, exception.message), Toast.LENGTH_SHORT).show()
                Log.e("CreateUser", "Database write failed", exception)
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


    //data class to store relevant user details
    data class User(val names: String, val email:String)

}


