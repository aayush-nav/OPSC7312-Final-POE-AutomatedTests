package com.theateam.vitaflex

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class InputValidationTest {
    private lateinit var dbHelper: DatabaseHelper

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase(DATABASE_NAME)
        dbHelper = DatabaseHelper(context)
    }

    @After
    fun tearDown() {
        dbHelper.close()
    }

    @Test
    fun testEmptyInput() {
        val result = dbHelper.insertData("", "Push Ups", "3 sets of 15", "testuser@gmail.com")
        assertFalse("Empty date should not be accepted", result)
    }

    @Test
    fun testNegativeCalories() {
        val result = dbHelper.insertData("2024-11-01", "Running", "-200 calories", "testuser@gmail.com")
        assertFalse("Negative calories should not be accepted", result)
    }

    @Test
    fun testInvalidExerciseDuration() {
        val result = dbHelper.insertData("2024-11-01", "Cycling", "Negative duration", "testuser@gmail.com")
        assertFalse("Invalid exercise duration should not be accepted", result)
    }

    @Test
    fun testValidData() {
        val result = dbHelper.insertData("2024-11-01", "Running", "30 minutes", "testuser@gmail.com")
        assertTrue("Valid data should be accepted", result)
    }

    @Test
    fun testErrorMessages() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Assume we have a method to validate input and return an error message if input is invalid
        val validationMessageEmptyDate = validateInput("", "Push Ups", "3 sets of 15")
        assertEquals("Date is required", validationMessageEmptyDate)

        val validationMessageNegativeCalories = validateInput("2024-11-01", "Running", "-200 calories")
        assertEquals("Calories cannot be negative", validationMessageNegativeCalories)

        val validationMessageInvalidDuration = validateInput("2024-11-01", "Cycling", "Negative duration")
        assertEquals("Duration must be positive", validationMessageInvalidDuration)
    }

    @Test
    fun testDatabaseDoesNotStoreInvalidData() {
        // Insert invalid data
        dbHelper.insertData("2024-11-01", "Running", "-200 calories", "testuser@gmail.com")
        val result = dbHelper.getExercisesByMonth("testuser@gmail.com")

        // Ensure no data was stored in the database for the invalid entry
        assertTrue("Invalid data should not be stored in the database", result.isEmpty())
    }

    /**
     * Mock validation function to simulate validation logic.
     */
    private fun validateInput(date: String, exerciseName: String, duration: String): String {
        return when {
            date.isBlank() -> "Date is required"
            duration.contains("-") -> "Duration must be positive"
            duration.contains("calories") && duration.startsWith("-") -> "Calories cannot be negative"
            else -> "Valid"
        }
    }
}
