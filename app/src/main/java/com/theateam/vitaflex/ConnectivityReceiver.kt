package com.theateam.vitaflex

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.widget.Toast

class ConnectivityReceiver : BroadcastReceiver() {

    private lateinit var email: String

    override fun onReceive(context: Context, intent: Intent) {

        // Retrieve the user's email from SharedPreferences
        val sharedPreferences = context.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
        email = sharedPreferences.getString("USER_EMAIL", null).toString()

        if (isConnected(context)) {
            // Sync unsynced data and load all data from Firestore
            if (context is WorkoutsActivity) {
//                Toast.makeText(context,
//                    context.getString(R.string.connected_to_the_internet), Toast.LENGTH_LONG).show()


                context.changeTXV(context.getString(R.string.online_1))
                context.syncExercisesToFirestore(email)
                context.loadAllDataWhenOnline()
            }
        } else {
            // Load only unsynced data when offline
            if (context is WorkoutsActivity) {

                context.changeTXV(context.getString(R.string.offline_go_back_online_to_sync_data))
                context.loadUnsyncedData()
            }
        }
    }


    private fun isConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            return activeNetwork?.isConnectedOrConnecting == true
        }
    }
}