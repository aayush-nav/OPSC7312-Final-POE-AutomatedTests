package com.theateam.vitaflex

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class WorkoutsActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var expandableListView: ExpandableListView
    private lateinit var adapter: WorkoutExpandableListAdapter
    private lateinit var btnAddExercise: Button
    private lateinit var connectedTv:TextView

    private lateinit var backBtn:Button

//    private val email = "user@example.com"  // hardcoded for now



    private lateinit var connectivityReceiver: ConnectivityReceiver

    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_workouts)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Retrieve the user's email from SharedPreferences
        val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        email = sharedPreferences.getString("USER_EMAIL", null).toString()

        db = DatabaseHelper(this)
        expandableListView = findViewById(R.id.expandableListViewWorkouts)
        btnAddExercise = findViewById(R.id.listOfWorkoutsCreateWorkoutBtn)
        connectedTv = findViewById(R.id.connectedTv)

        backBtn = findViewById(R.id.selectedWorkoutBackBtn)
        loadExercises()

        btnAddExercise.setOnClickListener {
            val intent = Intent(this, AddExerciseActivity::class.java)
            startActivity(intent)
        }

        backBtn.setOnClickListener {
            finish()
        }


        connectivityReceiver = ConnectivityReceiver()
    }

    private fun loadExercises() {
        // Fetch exercises only for the current user (email)
        val exerciseMap = db.getExercisesByMonth(email!!)
        adapter = WorkoutExpandableListAdapter(this, exerciseMap.keys.toList(), exerciseMap)
        expandableListView.setAdapter(adapter)
    }

    override fun onResume() {
        super.onResume()
        loadExercises()
        registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(connectivityReceiver)
    }

    fun syncExercisesToFirestore(email: String) {
        val unsyncedExercises = db.getUnsyncedExercises(email)  // Fetch unsynced exercises

        val firestore = FirebaseFirestore.getInstance()

        unsyncedExercises.forEach { (month, exerciseList) ->
            exerciseList.forEach { exercise ->
                val exerciseFirebase = ExerciseFirebase(
                    date = exercise.date,
                    exerciseName = exercise.exerciseName,
                    amount = exercise.amount,
                    email = email
                )

                firestore.collection("exercises").document(email).collection("userExercises")
                    .add(exerciseFirebase)
                    .addOnSuccessListener { documentReference ->
                        Log.d("FirestoreSync", "Exercise synced successfully: ${documentReference.id}")

                        // Mark this exercise as synced in the SQLite database
                        db.markExerciseAsSynced(exercise.id)  // Ensure your Exercise class has 'id'
                    }
                    .addOnFailureListener { error ->
                        Log.e("FirestoreSync", "Failed to sync exercise: ${error.message}")
                    }
            }
        }
    }


    fun loadUnsyncedData() {
        // Only show unsynced exercises when offline
        val unsyncedExercises = db.getUnsyncedExercises(email!!)
        adapter = WorkoutExpandableListAdapter(this, unsyncedExercises.keys.toList(), unsyncedExercises)
        expandableListView.setAdapter(adapter)
    }


    fun loadAllDataWhenOnline() {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("exercises").document(email!!).collection("userExercises")
            .get()
            .addOnSuccessListener { documents ->
                val allExercises = mutableMapOf<String, MutableList<Exercise>>()

                // Fetch data from Firestore
                val firestoreExercises = mutableSetOf<Int>() // Store Firestore exercise IDs to prevent duplicates
                for (document in documents) {
                    val id = document.getString("id")?.toInt() ?: 0
                    val date = document.getString("date") ?: ""
                    val exerciseName = document.getString("exerciseName") ?: ""
                    val amount = document.getString("amount") ?: ""

//                    val month = date.substring(0, 7)
                    val month = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)!!)

                    val exercise = Exercise(id, date, exerciseName, amount)

                    // Track Firestore exercise IDs
                    firestoreExercises.add(id)

                    if (allExercises.containsKey(month)) {
                        allExercises[month]?.add(exercise)
                    } else {
                        allExercises[month] = mutableListOf(exercise)
                    }
                }


                // Update UI
                adapter = WorkoutExpandableListAdapter(this, allExercises.keys.toList(), allExercises)
                expandableListView.setAdapter(adapter)
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching data: ${exception.message}")
            }
    }


    fun changeTXV(text:String)
    {
        connectedTv.setText(text);
    }




}