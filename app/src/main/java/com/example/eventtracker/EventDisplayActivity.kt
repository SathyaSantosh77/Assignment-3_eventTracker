package com.example.eventtracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class EventDisplayActivity : AppCompatActivity() {

    private val CHANNEL_ID = "event_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_display)

        val eventName = intent.getStringExtra("EVENT_NAME") ?: "Unknown"
        val eventDate = intent.getStringExtra("EVENT_DATE") ?: "Unknown"
        val eventTime = intent.getStringExtra("EVENT_TIME") ?: "Unknown"
        val reminderOption = intent.getStringExtra("REMINDER_OPTION") ?: "Unknown"

        val displayTextView: TextView = findViewById(R.id.event_display_text_view)
        displayTextView.text = "Event: $eventName\nDate: $eventDate\nTime: $eventTime\nReminder: $reminderOption"

        createNotificationChannel()
        sendNotification(eventName, eventDate, eventTime, reminderOption)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Event Channel"
            val descriptionText = "Channel for event notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(eventName: String, eventDate: String, eventTime: String, reminderOption: String) {
        val intent = Intent(this, EventDisplayActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Event Reminder")
            .setContentText("Event: $eventName\nDate: $eventDate\nTime: $eventTime\nReminder: $reminderOption")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setLights(0xFF00FF00.toInt(), 300, 1000)
            .setVibrate(longArrayOf(100, 200, 100, 200))

        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
    }
}
