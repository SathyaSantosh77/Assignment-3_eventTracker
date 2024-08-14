package com.example.eventtracker

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventHistoryAdapter(private val events: MutableList<Event>, private val context: Context) :
    RecyclerView.Adapter<EventHistoryAdapter.EventViewHolder>() {

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val eventName: TextView = view.findViewById(R.id.event_name)
        val eventLocation: TextView = view.findViewById(R.id.event_location)
        val eventDate: TextView = view.findViewById(R.id.event_date)
        val eventTime: TextView = view.findViewById(R.id.event_time)
        val editButton: Button = view.findViewById(R.id.edit_button)
        val deleteButton: Button = view.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_item, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.eventName.text = event.name
        holder.eventLocation.text = event.location
        holder.eventDate.text = event.date
        holder.eventTime.text = event.time

        holder.editButton.setOnClickListener {
            val intent = Intent(context, EventEntryActivity::class.java).apply {
                putExtra("EVENT_POSITION", position)
                putExtra("EVENT_NAME", event.name)
                putExtra("EVENT_LOCATION", event.location)
                putExtra("EVENT_DATE", event.date)
                putExtra("EVENT_TIME", event.time)
                putExtra("EVENT_REMINDER_OPTION", event.reminderOption)
            }
            context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            events.removeAt(position)
            notifyItemRemoved(position)
            EventData.saveEvents(context, events)
        }
    }

    override fun getItemCount() = events.size
}
