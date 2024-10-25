package com.theateam.vitaflex

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderBroadcastReceiver : BroadcastReceiver() {

    // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("NOTIFICATION_MESSAGE") ?: "Time for your activity!"
        showNotification(context, message)
    }

    // This function shows a notification with the given message.
    @SuppressLint("MissingPermission")
    private fun showNotification(context: Context, message: String) {
        // Create a notification channel for Android 8.0 (API level 26) and higher
        val channelId = "reminder_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "ReminderChannel"
            val descriptionText = "Channel for workout and meal reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Create the notification
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.vitaflex_only_logo) // Use your app's icon here
            .setContentTitle("Vitaflex Reminder") // App name
            .setContentText(message) // Custom message like "Time to workout!"
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.vitaflex_only_logo)) // App icon
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Remove notification once clicked

        // Create an intent to open the app when the notification is clicked
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        notificationBuilder.setContentIntent(pendingIntent)

        // Show the notification
        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
        }
    }
}