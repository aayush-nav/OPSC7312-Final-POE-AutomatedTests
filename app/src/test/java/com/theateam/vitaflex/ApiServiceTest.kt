package com.theateam.vitaflex

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    @Test
    fun testGetRecipes() {
        // Mock JSON response
        val mockJsonResponse = """
        [
          {
            "id": "ZYxNWJZ2qi9Owpt3gObr",
            "name": "Fruit salad",
            "ingredients": [
              {
                "fiber": 0,
                "carbs": 4.9,
                "protein": 3.5,
                "fat": 1.9,
                "name": "250ml milk",
                "cholesterol": 8,
                "calories": 51.3,
                "sugar": 0
              },
              {
                "fiber": 4.3,
                "carbs": 25.6,
                "protein": 0.5,
                "fat": 0.3,
                "name": "1 apple",
                "cholesterol": 0,
                "calories": 96.4,
                "sugar": 18.8
              },
              {
                "fiber": 3,
                "carbs": 27.4,
                "protein": 1.3,
                "fat": 0.4,
                "name": "1 banana",
                "cholesterol": 0,
                "calories": 105.5,
                "sugar": 14.5
              }
            ],
            "totalCalories": 253.2
          },
          {
            "id": "uqeqVP2Ft407gYM1EVMM",
            "name": "Apple pie",
            "ingredients": [
              {
                "fiber": 2.4,
                "carbs": 14.1,
                "protein": 0.3,
                "fat": 0.2,
                "name": "apple",
                "cholesterol": 0,
                "calories": 53,
                "sugar": 10.3
              }
            ],
            "totalCalories": 53
          }
        ]
        """.trimIndent()

        // Enqueue the response
        mockWebServer.enqueue(MockResponse().setBody(mockJsonResponse).setResponseCode(200))

        // Call the API and get the response
        val response: Response<List<Recipe>> = apiService.getRecipes().execute()

        // Assert that the response is successful
        assertTrue(response.isSuccessful)

        // Assert the response body
        val recipes = response.body()
        assertNotNull(recipes)
        assertEquals(2, recipes?.size)

        // Additional assertions on the returned data
        val firstRecipe = recipes?.get(0)
        assertEquals("Fruit salad", firstRecipe?.name)
        assertEquals(253.2, firstRecipe?.totalCalories!!, 0.1)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
}
