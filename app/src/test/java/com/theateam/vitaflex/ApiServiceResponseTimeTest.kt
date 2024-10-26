package com.theateam.vitaflex

import junit.framework.TestCase.assertTrue
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call

class ApiServiceResponseTimeTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        // Set up MockWebServer
        mockWebServer = MockWebServer()
        mockWebServer.start()

        // Initialize Retrofit with the MockWebServer's URL
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    @Test
    fun testApiResponseTime() {
        // Prepare a mock response
        val mockResponse = MockResponse()
            .setBody("[{\"id\":\"1\",\"name\":\"Test Recipe\",\"ingredients\":[],\"totalCalories\":100}]")
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        // Measure the time taken to get the response
        val startTime = System.currentTimeMillis()
        val call: Call<List<Recipe>> = apiService.getRecipes() // Replace with your actual method
        val response = call.execute()
        val endTime = System.currentTimeMillis()

        // Calculate the time difference
        val duration = endTime - startTime

        // Assert that the API response time is under 2000 milliseconds (2 seconds)
        assertTrue("API response should be under 2 seconds, but took ${duration}ms", duration < 2000)

        // Assert the response is successful (optional)
        assertTrue("API response should be successful", response.isSuccessful)
    }

    @After
    fun tearDown() {
        // Shut down the MockWebServer
        mockWebServer.shutdown()
    }
}