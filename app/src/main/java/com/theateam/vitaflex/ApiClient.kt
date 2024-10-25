package com.theateam.vitaflex

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Retrofit configuration
// Object to hold the Retrofit instance
// this is the base url for the api that we have created
object ApiClient {
    private const val BASE_URL = "https://recipe-meal-api-the-a-team.onrender.com"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}