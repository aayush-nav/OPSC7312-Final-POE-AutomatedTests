package com.theateam.vitaflex

import java.io.Serializable

//data class to represent an ingredient
data class Ingredient(
    val name: String,
    val calories: Double?,
    val protein: Double?,
    val carbs: Double?,
    val cholesterol: Double?,
    val fat: Double?,
    val fiber: Double?,
    val sugar: Double?
) : Serializable