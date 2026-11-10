package com.example.healthtracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_records")
data class ActivityRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val time: String, // HH:mm
    val activityType: String, // Walking, Running, Cycling, Swimming, Gym, Yoga, Other
    val durationMinutes: Int,
    val distanceKm: Float? = null,
    val steps: Int? = null,
    val caloriesBurned: Float,
    val intensity: String = "moderate", // low, moderate, high
    val notes: String? = null
)
