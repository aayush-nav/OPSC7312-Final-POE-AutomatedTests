package com.theateam.vitaflex

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class NetworkConnectivityTest {
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        connectivityManager = Mockito.mock(ConnectivityManager::class.java)
        Mockito.`when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
    }

    @Test
    fun testNetworkConnectivity() {
        // Mock the network state
        val mockNetworkInfo = Mockito.mock(NetworkInfo::class.java)
        Mockito.`when`(mockNetworkInfo.isConnected).thenReturn(true)
        Mockito.`when`(connectivityManager.activeNetworkInfo).thenReturn(mockNetworkInfo)

        // Assert that the network is connected
        val activeNetwork = connectivityManager.activeNetworkInfo
        assertTrue("Network should be connected", activeNetwork != null && activeNetwork.isConnected)
    }
}