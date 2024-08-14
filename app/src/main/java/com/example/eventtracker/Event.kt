package com.example.eventtracker

import java.io.Serializable

data class Event(
    val name: String,
    val location: String,
    val date: String,
    val time: String,
    val reminderOption: String
) : Serializable
