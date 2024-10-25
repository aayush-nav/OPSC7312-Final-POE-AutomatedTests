package com.theateam.vitaflex

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.biometric.BiometricPrompt
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theateam.vitaflex.databinding.ActivityLoginBinding
import java.util.Locale
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity() {


    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient


    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth

    private lateinit var googleEmail: String



    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTheme(R.style.splash_screen)

        // setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Load saved language preference
        loadLanguage()

        binding.loginBackBtn.setOnClickListener {
            finish()
        }

        binding.loginScreenForgotPasswordBtn.setOnClickListener {

        }

//        binding.loginScreenLoginBtn.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        }
//
//        binding.loginScreenSignInWithGoogleImageButton.setOnClickListener{
//
//        }



        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            handleSignInResult(result)
        }


        val webclientid = "846530981193-oer7dnl613o3o3967ulqlll8j8p6bkft.apps.googleusercontent.com"


        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(webclientid).requestEmail().build()
        gsc = GoogleSignIn.getClient(this, gso)


        //this will assign the value auth to allow authentication via firebase
        auth = Firebase.auth

        binding.loginScreenLoginBtn.setOnClickListener()
        {
            var email: String = binding.loginScreenEmailEditText.text.toString()
            var password: String = binding.loginScreenPasswordEditTextPassword.text.toString()


            //this will do data validation to ensure that all fields have data in them
            if(binding.loginScreenEmailEditText.text.isEmpty() || binding.loginScreenPasswordEditTextPassword.text.isEmpty())
            {
                Toast.makeText(this,
                    getString(R.string.please_ensure_that_all_fields_have_been_entered), Toast.LENGTH_SHORT).show()
            }
            else
            {
                login(email, password)
            }
        }



        binding.loginScreenSignInWithGoogleImageButton.setOnClickListener()
        {
            loginGoogle()
        }

        binding.loginScreenLoginBiometricsbtn.setOnClickListener()
        {
            checkBiometrics()

            executor = ContextCompat.getMainExecutor(this)
            biometricPrompt = BiometricPrompt(this, executor,
                object : BiometricPrompt.AuthenticationCallback()
                {
                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    )
                    {
                        super.onAuthenticationError(errorCode, errString)
                        Toast.makeText(
                            applicationContext,
                            "Authentication error: $errString", Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)


                        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
                        val userEmail = sharedPreferences.getString("USER_EMAIL", null)

                        Toast.makeText(applicationContext,
                            getString(R.string.log_in_successful, userEmail), Toast.LENGTH_LONG).show()
                        val intent = Intent(this@LoginActivity,MainActivity::class.java)
                        startActivity(intent)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Toast.makeText(applicationContext, "Authentication failed",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                })



            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build()

            biometricPrompt.authenticate(promptInfo)
        }


    }


    private fun checkBiometrics() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.")
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Log.e("MY_APP_TAG", "No biometric features available on this device.")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Prompts the user to create credentials that your app accepts.
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                }
                startActivityForResult(enrollIntent, 1)
            }
        }
    }



    //this will check if the current user logging in exists in the authentication so that they can log in
    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("USER_EMAIL", email)
                    editor.apply()

                    val googlePreferences = applicationContext.getSharedPreferences("GooglePref", Context.MODE_PRIVATE)
                    val googleEditor = googlePreferences.edit()
                    googleEditor.putBoolean("GOOGLE", true)
                    googleEditor.apply()


                    val userEmail = sharedPreferences.getString("USER_EMAIL", null)

                    Toast.makeText(this,
                        getString(R.string.log_in_successful, userEmail), Toast.LENGTH_LONG).show()
                    val intent = Intent(this@LoginActivity,MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, getString(R.string.log_in_failed), Toast.LENGTH_LONG).show()
                }
            }
    }


    //this will log the user in with google and will run further methods to check if the user exists in the database
    private fun loginGoogle() {
        // Sign out the user to ensure they can choose another account if desired
        gsc.signOut().addOnCompleteListener {
            val loginIntent = gsc.signInIntent
            googleSignInLauncher.launch(loginIntent)
        }


        val googlePreferences = applicationContext.getSharedPreferences("GooglePref", Context.MODE_PRIVATE)
        val googleEditor = googlePreferences.edit()
        googleEditor.putBoolean("GOOGLE", false)
        googleEditor.apply()
    }

    //this method will present the user with the google sign in pop up and will handle the information taken from the user
    private fun handleSignInResult(result: ActivityResult) {
        val data = result.data
        if (result.resultCode == Activity.RESULT_OK && data != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                googleEmail = account.email.toString()

                if (account != null && account.idToken != null) {

                    // Check if this email is already registered in Firebase
                    checkIfUserExists(googleEmail)
                } else {
                    Toast.makeText(this,
                        getString(R.string.sign_in_failed_please_try_again, googleEmail), Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Log.w("Login with Google", "Google sign in failed", e)
            }
        } else {
            Log.w("Google Sign-In", "Sign-in failed or was canceled")
        }
    }


    //this method will check if the user exists in the database using the information taken from the user's google account
    private fun checkIfUserExists(email: String) {

        var arrUserEmailList = arrayListOf<String>()

        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("VitaflexApp")

        dbRef.child("Users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (userSnapshot in dataSnapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        if (user != null) {
                            Log.d("User", "User email: ${user.email}")
                            arrUserEmailList.add(user.email.toString())
                        } else {
                            Log.e("User", "User data is null for snapshot: ${userSnapshot.key}")
                        }
                    }

                    // Once data is retrieved, check if the email exists in the list
                    if (arrUserEmailList.contains(email)) {
                        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("USER_EMAIL", email)
                        editor.apply()

                        Toast.makeText(this@LoginActivity,
                            getString(R.string.log_in_successful_1), Toast.LENGTH_LONG).show()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@LoginActivity,
                            getString(R.string.account_does_not_exist), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("User", "Failed to get user data: ${error.message}")
                }
            })
    }


    //user data class to store the user's name and email in an object
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