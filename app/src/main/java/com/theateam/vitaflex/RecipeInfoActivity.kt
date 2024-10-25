package com.theateam.vitaflex

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.theateam.vitaflex.databinding.ActivityRecipeInfoBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class RecipeInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeInfoBinding

    private var totalCalories: Double = 0.0
    private lateinit var ingredients: ArrayList<Ingredient>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRecipeInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setContentView(R.layout.activity_recipe_info)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Load saved language preference
        loadLanguage()

        // Retrieve the ingredients and total calories from the intent
        ingredients = intent.getSerializableExtra("ingredients") as ArrayList<Ingredient>
//        val totalCalories = intent.getIntExtra("totalCalories",0)
//
//        // Set the total calories in the TextView
//        binding.totalCaloriesTextView.text = "Total Calories: $totalCalories"


        //this button will save the current recipe to the api database when clicked
        binding.saveRecipeButton.setOnClickListener {
            val recipeName = binding.createRecipe1RecipeNameEditText.text.toString()

            if (recipeName.isNotEmpty()) {
                // Save the recipe (implementation can involve saving to database or shared preferences)

                for (ingredient in ingredients) {
                    totalCalories += ingredient.calories!!
                }

                val totalCalories = ingredients.sumByDouble { it.calories ?: 0.0 }

                saveRecipe(recipeName, totalCalories, ingredients)
//                finish()  // Close the activity once saved

                val intent = Intent(this, ListOfAllRecipesActivity::class.java)
                startActivity(intent)

            } else {
                binding.createRecipe1RecipeNameEditText.error = "Please enter a recipe name"
            }
        }

        binding.recipeInfoBackButton.setOnClickListener() {
            finish()
        }
    }

    //this method will create a recipe object from the information provided, and will then add it to the list of recipes, and will pass the recipe object to the addRecipe method to store in the api
    private fun saveRecipe(name: String, calories: Double, ingredients: List<Ingredient>) {
        // Save recipe in SharedPreferences
        val sharedPreferences = getSharedPreferences("recipes", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Get the existing recipes from SharedPreferences
        val gson = Gson()
        val recipesJson = sharedPreferences.getString("recipeList", "[]")
        val type = object : TypeToken<ArrayList<Recipe>>() {}.type
        val recipeList: ArrayList<Recipe> = gson.fromJson(recipesJson, type)

        // Add the new recipe to the list
        val recipe = Recipe(name, calories, ingredients)
        recipeList.add(recipe)
        addRecipe(recipe)

        // Save the updated list back to SharedPreferences
        editor.putString("recipeList", gson.toJson(recipeList))
        editor.apply()

        // Log the save for debugging
        Log.d("RecipeInfoActivity", "Recipe saved: $name with $calories calories")
    }

    //this method will add a new recipe to the API database that will be able to be viewed by all users of the app
    private fun addRecipe(recipe: Recipe) {
        ApiClient.apiService.addRecipe(recipe).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RecipeInfoActivity,
                        getString(R.string.recipe_added_successfully_3), Toast.LENGTH_SHORT).show()
                    Log.d("API_SUCCESS", "Recipe added successfully: ${response.body()?.string()}")
                } else {
                    Toast.makeText(this@RecipeInfoActivity,
                        getString(R.string.failed_to_add_recipe), Toast.LENGTH_SHORT).show()
                    Log.e("API_ERROR", "Error: ${response.code()}, ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("API_FAILURE", "Failed to make request: ${t.message}")
                Toast.makeText(this@RecipeInfoActivity,
                    getString(R.string.error_5, t.message), Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun loadLanguage() {
        val sharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val savedLanguage = sharedPref.getString("language", "en")  // Default to English
        setLocale(savedLanguage ?: "en")
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}