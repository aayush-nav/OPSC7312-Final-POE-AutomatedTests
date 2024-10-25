package com.theateam.vitaflex

import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiServiceTest2 {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService

    @Before
    fun setUp() {
        // Set up a MockWebServer to intercept API calls
        mockWebServer = MockWebServer()
        mockWebServer.start()

        // Initialize the Retrofit instance using the MockWebServer URL
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/")) // Use the MockWebServer URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create the ApiService instance
        apiService = retrofit.create(ApiService::class.java)
    }

    @After
    fun tearDown() {
        // Shut down the MockWebServer after tests
        mockWebServer.shutdown()
    }

    @Test
    fun testGetMeals() {
        // Mock response JSON matching the structure of Meal and MealType
        val mockResponse = """
            [
              {
                "id": "A8JClHfe5yYkuMGTKjCF",
                "date": "2024-08-21",
                "mealItems": [
                  {
                    "name": "Fruit salad",
                    "calories": 253.2
                  }
                ],
                "mealCategory": "Lunch",
                "email": "hi@gmail.com"
              },
              {
                "id": "CzqjTNABocPIRpLemFIj",
                "date": "2024-09-24",
                "mealItems": [
                  {
                    "name": "milkshake",
                    "calories": 120.4
                  }
                ],
                "mealCategory": "Lunch",
                "email": "anjali.payal69@gmail.com"
              },
              {
                "id": "D7KepL9hBPdOuFKz3MF1",
                "date": "2024-09-23",
                "mealCategory": "Breakfast",
                "email": "anjali.payal69@gmail.com",
                "mealItems": [
                  {
                    "name": "Fruit salad",
                    "calories": 253.2
                  },
                  {
                    "name": "200 g yogurt",
                    "calories": 128.5
                  },
                  {
                    "name": "milkshake",
                    "calories": 120.4
                  }
                ]
              },
              {
                "id": "LtfF5q88bgw7eQWsdw7Y",
                "date": "2024-09-30",
                "mealItems": [
                  {
                    "name": "yogurt",
                    "calories": 64.2
                  }
                ],
                "mealCategory": "Breakfast",
                "email": "anjali.payal69@gmail.com"
              },
              {
                "id": "VwVSwQla86iAxDqDo91k",
                "date": "2024-09-30",
                "mealItems": [
                  {
                    "name": "Fruit salad",
                    "calories": 253.2
                  }
                ],
                "mealCategory": "Breakfast",
                "email": "hi@gmail.com"
              },
              {
                "id": "z0CNrTc3MYrNgLC0sLRI",
                "date": "2024-10-01",
                "mealItems": [
                  {
                    "name": "Apple pie",
                    "calories": 53
                  }
                ],
                "mealCategory": "Snack",
                "email": "anjali.payal69@gmail.com"
              }
            ]
        """.trimIndent()

        // Enqueue the mocked response
        mockWebServer.enqueue(MockResponse().setBody(mockResponse).setResponseCode(200))

        // Call the API service
        val call: Call<List<Meal>> = apiService.getMeals()
        val response: Response<List<Meal>> = call.execute()

        // Assert that the API response is successful
        assertEquals(200, response.code())
        val meals = response.body()

        // Validate the response structure and data
        assertEquals(6, meals?.size)

        // Validate the first meal
        val firstMeal = meals?.get(0)
        assertEquals("hi@gmail.com", firstMeal?.email)
        assertEquals("2024-08-21", firstMeal?.date)
        assertEquals("Lunch", firstMeal?.mealCategory)
        assertEquals(1, firstMeal?.mealItems?.size)

        // Validate the first meal's first item
        val firstMealItem = firstMeal?.mealItems?.get(0)
        assertEquals("Fruit salad", firstMealItem?.name)
        assertEquals(253.2, firstMealItem?.calories)

        // Validate additional meal entries
        val secondMeal = meals?.get(1)
        assertEquals("anjali.payal69@gmail.com", secondMeal?.email)
        assertEquals("2024-09-24", secondMeal?.date)
        assertEquals("Lunch", secondMeal?.mealCategory)

        val thirdMeal = meals?.get(2)
        assertEquals("anjali.payal69@gmail.com", thirdMeal?.email)
        assertEquals("Breakfast", thirdMeal?.mealCategory)
        assertEquals(3, thirdMeal?.mealItems?.size)

        // Validate the third meal's first item
        val thirdMealFirstItem = thirdMeal?.mealItems?.get(0)
        assertEquals("Fruit salad", thirdMealFirstItem?.name)
        assertEquals(253.2, thirdMealFirstItem?.calories)
    }
}
