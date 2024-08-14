package com.example.eventtracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {

    private val CHANNEL_ID = "event_channel"

    override fun onReceive(context: Context?, intent: Intent?) {
        val eventName = intent?.getStringExtra("EVENT_NAME") ?: "Unknown Event"
        val eventDate = intent?.getStringExtra("EVENT_DATE") ?: "Unknown Date"
        val eventTime = intent?.getStringExtra("EVENT_TIME") ?: "Unknown Time"

        createNotificationChannel(context)

        val builder = NotificationCompat.Builder(context!!, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Event Reminder")
            .setContentText("Event: $eventName\nDate: $eventDate\nTime: $eventTime")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(1, builder.build())
        }
    }

    private fun createNotificationChannel(context: Context?) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = "Event Channel"
            val descriptionText = "Channel for event notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

