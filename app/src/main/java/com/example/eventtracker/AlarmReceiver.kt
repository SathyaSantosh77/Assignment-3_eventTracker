package com.example.eventtracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val eventName = intent.getStringExtra("EVENT_NAME")
        val eventLocation = intent.getStringExtra("EVENT_LOCATION")
        val eventDate = intent.getStringExtra("EVENT_DATE")
        val eventTime = intent.getStringExtra("EVENT_TIME")

        val notificationBuilder = NotificationCompat.Builder(context, "event_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Event Reminder")
            .setContentText("Reminder: $eventName at $eventLocation on $eventDate at $eventTime")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(2, notificationBuilder.build())
        }
    }
}
