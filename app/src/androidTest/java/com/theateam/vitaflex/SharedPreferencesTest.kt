package com.theateam.vitaflex

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SharedPreferencesTest {
    @Test
    fun testSaveAndRetrieveData() {
        // Obtain the application context
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Access SharedPreferences in a private mode for testing
        val sharedPreferences = context.getSharedPreferences("TestPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Put a string value in SharedPreferences and commit
        editor.putString("test_key", "test_value")
        editor.apply()

        // Retrieve the value and validate
        val savedValue = sharedPreferences.getString("test_key", null)
        assertEquals("The retrieved value should match the saved value", "test_value", savedValue)
    }
}