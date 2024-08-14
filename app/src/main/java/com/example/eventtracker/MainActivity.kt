package com.example.eventtracker

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fabAddEvent: FloatingActionButton = findViewById(R.id.fab_add_event)
        fabAddEvent.setOnClickListener {
            startActivity(Intent(this, EventEntryActivity::class.java))
        }

        // Load events
        val events = EventData.loadEvents(this)
        // Handle displaying events as needed
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
