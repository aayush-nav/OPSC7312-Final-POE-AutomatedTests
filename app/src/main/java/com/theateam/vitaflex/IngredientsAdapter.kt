package com.theateam.vitaflex

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.math.BigDecimal
import java.math.RoundingMode

class IngredientsAdapter(private val context: Context, private val ingredients: List<Ingredient>) : RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder>() {

    class IngredientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ingredientName: TextView = view.findViewById(R.id.ingredient_name)
        val ingredientCalories: TextView = view.findViewById(R.id.ingredient_calories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ingredient_item, parent, false)
        return IngredientViewHolder(view)
    }

    //this will display the ingredient information to the user, including the name and calories
    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.ingredientName.text = "• " + ingredient.name

        val caloriePreferences = context.getSharedPreferences("CaloriesPref", Context.MODE_PRIVATE)
        val calories = caloriePreferences.getBoolean("CALORIES_UNIT", true)

        if(calories == true)
        {
            val roundedCalories =
                ingredient.calories?.let { BigDecimal(it).setScale(2, RoundingMode.HALF_UP).toDouble() }
            holder.ingredientCalories.text = "${roundedCalories} cal"
        }
        else
        {
            val kiloJoules = ingredient.calories?.times(4.184)
            val roundedKiloJoules = kiloJoules?.let { BigDecimal(it).setScale(2, RoundingMode.HALF_UP).toDouble() }
            holder.ingredientCalories.text = "${roundedKiloJoules} kJ"
        }
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }
}
