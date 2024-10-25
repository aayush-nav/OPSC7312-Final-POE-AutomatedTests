package com.theateam.vitaflex

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

// Define the NewMealsPagerAdapter class
class NewMealsPagerAdapter (activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 2 // Number of fragments (tabs)
    }

    // Create a fragment based on the position
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ExistingRecipesFragment() // First tab
            1 -> OtherFoodItemsFragment() // Second tab
            else -> ExistingRecipesFragment()
        }
    }
}