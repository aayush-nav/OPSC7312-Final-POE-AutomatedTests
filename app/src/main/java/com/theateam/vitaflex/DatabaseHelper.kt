package com.theateam.vitaflex

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Locale

const val DATABASE_NAME = "exercise_log.db"
const val TABLE_NAME = "exercise_log"
const val COL_ID = "id"
const val COL_DATE = "date"
const val COL_EXERCISE_NAME = "exercise_name"
const val COL_AMOUNT = "amount"
const val COL_EMAIL = "email"
const val COL_SYNC_STATUS = "sync_status"

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 4) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE " + TABLE_NAME + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_DATE + " TEXT,"
                + COL_EXERCISE_NAME + " TEXT,"
                + COL_AMOUNT + " TEXT,"
                + COL_EMAIL + " TEXT,"
                + COL_SYNC_STATUS + " INTEGER DEFAULT 0)")  // 0: not synced, 1: synced
        db.execSQL(createTable)


        val insertData = "INSERT INTO $TABLE_NAME ($COL_DATE, $COL_EXERCISE_NAME, $COL_AMOUNT, $COL_EMAIL, $COL_SYNC_STATUS) VALUES " +
                "('2024-10-01', 'Push Ups', '3 sets of 15', 'futuregorilla99@gmail.com', 0)," +
                "('2024-10-02', 'Running', '30 minutes', 'futuregorilla99@gmail.com', 0)," +
                "('2024-10-03', 'Squats', '4 sets of 20', 'futuregorilla99@gmail.com', 0)," +
                "('2024-10-05', 'Cycling', '45 minutes', 'futuregorilla99@gmail.com', 0)"
        db.execSQL(insertData)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Insert a new exercise with syncStatus = 0 (not synced)
    fun insertData(date: String, exerciseName: String, amount: String, email: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COL_DATE, date)
            put(COL_EXERCISE_NAME, exerciseName)
            put(COL_AMOUNT, amount)
            put(COL_EMAIL, email)
            put(COL_SYNC_STATUS, 0)
        }
        val result = db.insert(TABLE_NAME, null, contentValues)
        return result != -1L
    }

    // Mark an exercise as synced after successfully syncing it to Firestore
    fun markExerciseAsSynced(exerciseId: Int) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COL_SYNC_STATUS, 1)  // Mark as synced
        }
        db.update(TABLE_NAME, contentValues, "$COL_ID=?", arrayOf(exerciseId.toString()))
    }

    fun getExercisesByMonth(email: String): Map<String, List<Exercise>> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL_EMAIL=?", arrayOf(email))
        val exerciseMap = mutableMapOf<String, MutableList<Exercise>>()

        // Define the date formatters
        val inputFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

        try {
            if (cursor.moveToFirst()) {
                do {
                    // Safely get the column indices
                    val dateIndex = cursor.getColumnIndexOrThrow(COL_DATE)
                    val exerciseNameIndex = cursor.getColumnIndexOrThrow(COL_EXERCISE_NAME)
                    val amountIndex = cursor.getColumnIndexOrThrow(COL_AMOUNT)

                    // Retrieve values from the cursor
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID))
                    val date = cursor.getString(dateIndex) ?: ""
                    val exerciseName = cursor.getString(exerciseNameIndex) ?: ""
                    val amount = cursor.getString(amountIndex) ?: ""

                    // Parse and format the date
                    val formattedMonth = if (date.isNotEmpty()) {
                        try {
                            val parsedDate = inputFormatter.parse(date)
                            outputFormatter.format(parsedDate ?: "")
                        } catch (e: Exception) {
                            Log.e("DateParsingError", "Error formatting date: ${e.message}")
                            ""
                        }
                    } else ""

                    // Create Exercise object
                    val exercise = Exercise(id, date, exerciseName, amount)

                    // Group exercises by formatted month
                    if (formattedMonth.isNotEmpty()) {
                        if (exerciseMap.containsKey(formattedMonth)) {
                            exerciseMap[formattedMonth]?.add(exercise)
                        } else {
                            exerciseMap[formattedMonth] = mutableListOf(exercise)
                        }
                    }

                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("DatabaseError", "Error reading exercises: ${e.message}")
        } finally {
            cursor.close()  // Always close the cursor when done
        }

        return exerciseMap
    }


    fun getUnsyncedExercises(email: String): Map<String, List<Exercise>> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL_EMAIL=? AND $COL_SYNC_STATUS=0", arrayOf(email))
        val exerciseMap = mutableMapOf<String, MutableList<Exercise>>()

        // Define the date formatters
        val inputFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

        try {
            if (cursor.moveToFirst()) {
                do {
                    // Safely get the column indices
                    val dateIndex = cursor.getColumnIndexOrThrow(COL_DATE)
                    val exerciseNameIndex = cursor.getColumnIndexOrThrow(COL_EXERCISE_NAME)
                    val amountIndex = cursor.getColumnIndexOrThrow(COL_AMOUNT)

                    // Retrieve values from the cursor
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID))
                    val date = cursor.getString(dateIndex) ?: ""
                    val exerciseName = cursor.getString(exerciseNameIndex) ?: ""
                    val amount = cursor.getString(amountIndex) ?: ""

//                    // Extract year-month (YYYY-MM) for grouping by month
//                    val month = if (date.length >= 7) date.substring(0, 7) else ""
//
//                    // Create Exercise object
//                    val exercise = Exercise(id, date, exerciseName, amount)
//
//                    // Group exercises by month
//                    if (month.isNotEmpty()) {
//                        if (exerciseMap.containsKey(month)) {
//                            exerciseMap[month]?.add(exercise)
//                        } else {
//                            exerciseMap[month] = mutableListOf(exercise)
//                        }
//                    }

                    // Parse and format the date
                    val formattedMonth = if (date.isNotEmpty()) {
                        try {
                            val parsedDate = inputFormatter.parse(date)
                            outputFormatter.format(parsedDate ?: "")
                        } catch (e: Exception) {
                            Log.e("DateParsingError", "Error formatting date: ${e.message}")
                            ""
                        }
                    } else ""

                    // Create Exercise object
                    val exercise = Exercise(id, date, exerciseName, amount)

                    // Group exercises by formatted month
                    if (formattedMonth.isNotEmpty()) {
                        if (exerciseMap.containsKey(formattedMonth)) {
                            exerciseMap[formattedMonth]?.add(exercise)
                        } else {
                            exerciseMap[formattedMonth] = mutableListOf(exercise)
                        }
                    }
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("DatabaseError", "Error reading unsynced exercises: ${e.message}")
        } finally {
            cursor.close()  // Always close the cursor when done
        }

        return exerciseMap
    }

}

data class Exercise(val id: Int, val date: String, val exerciseName: String, val amount: String)