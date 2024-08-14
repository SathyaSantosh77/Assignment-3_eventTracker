package com.example.eventtracker

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.util.*

class EventEntryActivity : AppCompatActivity() {

    private lateinit var dateEditText: EditText
    private lateinit var timeEditText: EditText
    private var eventPosition: Int? = null

    private val CHANNEL_ID = "event_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_entry)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val eventNameEditText: EditText = findViewById(R.id.event_name)
        val eventLocationEditText: EditText = findViewById(R.id.event_location)
        dateEditText = findViewById(R.id.event_date)
        timeEditText = findViewById(R.id.event_time)
        val reminderSpinner: Spinner = findViewById(R.id.reminder_spinner)
        val submitButton: Button = findViewById(R.id.submit_button)
        val logoImageView: ImageView = findViewById(R.id.logo_image)

        // Array of drawable resource IDs
        val images = listOf(R.drawable.img_1, R.drawable.img_2, R.drawable.img_3)
        // Randomly select an image
        val randomImage = images.random()
        // Set the selected image to the ImageView
        logoImageView.setImageResource(randomImage)

        // Check if we are editing an existing event
        if (intent.hasExtra("EVENT_POSITION")) {
            eventPosition = intent.getIntExtra("EVENT_POSITION", -1)
            eventNameEditText.setText(intent.getStringExtra("EVENT_NAME"))
            eventLocationEditText.setText(intent.getStringExtra("EVENT_LOCATION"))
            dateEditText.setText(intent.getStringExtra("EVENT_DATE"))
            timeEditText.setText(intent.getStringExtra("EVENT_TIME"))
            // Set the reminder option spinner
            val reminderOptions = resources.getStringArray(R.array.reminder_options)
            reminderSpinner.setSelection(reminderOptions.indexOf(intent.getStringExtra("EVENT_REMINDER_OPTION")))
        }

        dateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        timeEditText.setOnClickListener {
            showTimePickerDialog()
        }

        submitButton.setOnClickListener {
            val eventName = eventNameEditText.text.toString()
            val eventLocation = eventLocationEditText.text.toString()
            val eventDate = dateEditText.text.toString()
            val eventTime = timeEditText.text.toString()
            val reminderOption = reminderSpinner.selectedItem.toString()

            val event = Event(eventName, eventLocation, eventDate, eventTime, reminderOption)
            val events = EventData.loadEvents(this)

            if (eventPosition != null && eventPosition != -1) {
                // Editing an existing event
                events[eventPosition!!] = event
            } else {
                // Adding a new event
                events.add(event)
            }

            EventData.saveEvents(this, events)

            Log.d("EventEntryActivity", "Event added/edited: $event")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SCHEDULE_EXACT_ALARM), 1)
                } else {
                    scheduleNotification(event)
                }
            } else {
                scheduleNotification(event)
            }

            // Show notification only when the event is created or edited
            showImmediateNotification(event)

            // Show dialog
            showEventCreatedDialog()

            val intent = Intent(this, EventHistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            dateEditText.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            timeEditText.setText("$selectedHour:$selectedMinute")
        }, hour, minute, true)

        timePickerDialog.show()
    }

    private fun scheduleNotification(event: Event) {
        val calendar = Calendar.getInstance()
        val dateParts = event.date.split("/").map { it.toInt() }
        val timeParts = event.time.split(":").map { it.toInt() }
        calendar.set(dateParts[2], dateParts[1] - 1, dateParts[0], timeParts[0], timeParts[1])

        val reminderTimeInMillis = when (event.reminderOption) {
            "1 hour before" -> calendar.timeInMillis - 60 * 60 * 1000
            "3 hours before" -> calendar.timeInMillis - 3 * 60 * 60 * 1000
            "1 day before" -> calendar.timeInMillis - 24 * 60 * 60 * 1000
            "1 week before" -> calendar.timeInMillis - 7 * 24 * 60 * 60 * 1000
            else -> calendar.timeInMillis
        }

        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("EVENT_NAME", event.name)
            putExtra("EVENT_LOCATION", event.location)
            putExtra("EVENT_DATE", event.date)
            putExtra("EVENT_TIME", event.time)
        }
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTimeInMillis, pendingIntent)

        Log.d("EventEntryActivity", "Alarm set for: ${Date(reminderTimeInMillis)}")
    }

    private fun showImmediateNotification(event: Event) {
        createNotificationChannel()
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Event Created")
            .setContentText("Event: ${event.name} has been created successfully.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(1, notificationBuilder.build())
        }
    }

    private fun showEventCreatedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Event Created")
            .setMessage("Your event has been successfully created.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                val event = EventData.loadEvents(this).lastOrNull()
                if (event != null) {
                    scheduleNotification(event)
                }
            } else {
                Log.d("EventEntryActivity", "SCHEDULE_EXACT_ALARM permission denied")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_new_event -> {
                startActivity(Intent(this, EventEntryActivity::class.java))
                true
            }
            R.id.action_view_history -> {
                startActivity(Intent(this, EventHistoryActivity::class.java))
                true
            }
            R.id.action_home -> {
                startActivity(Intent(this, MainActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
