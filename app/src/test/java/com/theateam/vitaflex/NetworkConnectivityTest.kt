package com.theateam.vitaflex

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NetworkConnectivityTest {
    private lateinit var connectivityManager: ConnectivityManager

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Test
    fun testNetworkConnectivity() {
        val activeNetwork = connectivityManager.activeNetworkInfo
        assertTrue("Network is not connected", activeNetwork != null && activeNetwork.isConnected)
    }
}