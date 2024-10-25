package com.theateam.vitaflex

import java.io.Serializable

// Define the MealItem data class
data class MealType (
    val name: String,
    val calories: Double
): Serializable