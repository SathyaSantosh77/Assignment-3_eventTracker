package com.example.eventtracker

import android.content.Context
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

object EventData {
    private const val PREFS_NAME = "event_tracker_prefs"
    private const val EVENTS_KEY = "events"

    fun saveEvents(context: Context, events: MutableList<Event>) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(events)
        editor.putString(EVENTS_KEY, json)
        editor.apply()
    }

    fun loadEvents(context: Context): MutableList<Event> {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(EVENTS_KEY, null)
        val type = object : TypeToken<MutableList<Event>>() {}.type
        return if (json != null) {
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }
}
