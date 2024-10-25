package com.theateam.vitaflex

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class ExistingRecipesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private var recipeList = ArrayList<Recipe>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_existing_recipes, container, false)

        // Find the RecyclerView after the view is inflated
        recyclerView = view.findViewById(R.id.listOfAllRecipes_recipe_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext()) // Set layout manager

        // Initialize the adapter
        recipeAdapter = RecipeAdapter(recipeList) { recipe ->
            // Handle recipe click, use requireContext() for Fragment context
            val intent = Intent(requireContext(), RecipeDetailActivity::class.java)
            intent.putExtra("recipe", recipe)
            intent.putExtra("boolAddToMeal", true)
            startActivity(intent)
        }

        // Load saved language preference
        loadLanguage()

        // Set the adapter for RecyclerView
        recyclerView.adapter = recipeAdapter

        // Load saved recipes from API
        loadRecipes()

        return view // Return the inflated view
    }

    //this will load all of the recipes stored in the API and display them in the recycler view
    private fun loadRecipes() {
        // Make the API call to fetch recipes
        ApiClient.apiService.getRecipes().enqueue(object : Callback<List<Recipe>> {
            override fun onResponse(call: Call<List<Recipe>>, response: Response<List<Recipe>>) {
                if (response.isSuccessful && response.body() != null) {
                    // Update the recipe list and notify the adapter
                    recipeList.clear()
                    recipeList.addAll(response.body()!!)
                    recipeAdapter.notifyDataSetChanged()
                    // Log the data for debugging
                    Log.d("API_SUCCESS", "Recipes fetched successfully: ${response.body()}")
                } else {
                    // Handle the error
                    Log.e("API_ERROR", "Failed to load recipes. Error code: ${response.code()}, message: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Recipe>>, t: Throwable) {
                // Log the error
                Log.e("API_FAILURE", "Error: ${t.message}", t)
            }
        })
    }

    private fun loadLanguage() {
        val sharedPref = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
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
