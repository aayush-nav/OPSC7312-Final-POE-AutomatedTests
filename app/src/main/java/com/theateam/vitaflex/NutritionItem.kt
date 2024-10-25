package com.theateam.vitaflex

//class to store information about the ingredient selected
class NutritionItem {
    // Getters and setters
    var name: String? = null
    var calories: Double = 0.0
    var protein_g: Double = 0.0
    var carbohydrates_total_g: Double = 0.0
    var cholesterol_mg: Double = 0.0
    var fat_total_g: Double = 0.0
    var fiber_g: Double = 0.0
    var sugar_g: Double = 0.0
}

class NutritionResponse {
    var items: List<NutritionItem>? = null
}
