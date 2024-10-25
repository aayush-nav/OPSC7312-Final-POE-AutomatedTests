package com.theateam.vitaflex

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// Calories Ninja API interface with the endpoint for searching food information
interface CaloriesNinjasApi {
    // Endpoint for searching food information
    @GET("/v1/nutrition")
    fun searchFood(
        @Header("X-Api-Key") apiKey: String?,
        @Query("query") foodName: String?
    ): Call<NutritionResponse?>?
}