package com.theateam.vitaflex

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

//@RunWith(AndroidJUnit4::class)
class DatabaseHelperTest {

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
    fun testInsertData() {
        val success = dbHelper.insertData("2024-11-01", "Test Exercise", "3 sets of 10", "testuser@gmail.com")
        assertTrue("Data should be inserted successfully", success)
    }

    @Test
    fun testGetExercisesByMonth() {
        // Insert sample data
        dbHelper.insertData("2024-11-01", "Push Ups", "3 sets of 15", "testuser@gmail.com")
        dbHelper.insertData("2024-11-02", "Running", "30 minutes", "testuser@gmail.com")

        // Fetch exercises by month
        val exercisesMap = dbHelper.getExercisesByMonth("testuser@gmail.com")

        // Assert that the exercises are grouped correctly
        assertTrue(exercisesMap.isNotEmpty())
        val month = exercisesMap.keys.first()
        val exercises = exercisesMap[month]
        assertEquals("There should be 2 exercises in November 2024", 2, exercises?.size)
    }

    @Test
    fun testGetUnsyncedExercises() {
        // Insert sample data with different sync statuses
        dbHelper.insertData("2024-11-03", "Test Sync Exercise 1", "2 sets of 20", "testuser@gmail.com")
        dbHelper.insertData("2024-11-04", "Test Sync Exercise 2", "5 sets of 10", "testuser@gmail.com")

        // Mark the first exercise as synced
        dbHelper.markExerciseAsSynced(1)

        // Fetch unsynced exercises
        val unsyncedExercises = dbHelper.getUnsyncedExercises("testuser@gmail.com")

        // Assert that only unsynced exercises are retrieved
        assertTrue("Only unsynced exercises should be retrieved", unsyncedExercises.isNotEmpty())
        assertEquals(1, unsyncedExercises.values.flatten().size)
    }
}
