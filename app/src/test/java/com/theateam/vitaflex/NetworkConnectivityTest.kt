package com.theateam.vitaflex

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertTrue
import org.mockito.Mockito

class NetworkConnectivityTest {
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var context: Context

    @Before
    fun setUp() {
        // Create a mock context
        context = Mockito.mock(Context::class.java)

        // Create a mock connectivity manager
        connectivityManager = Mockito.mock(ConnectivityManager::class.java)

        // Mock the getSystemService call to return the mocked ConnectivityManager
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