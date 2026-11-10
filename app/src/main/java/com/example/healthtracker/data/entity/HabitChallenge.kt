package com.example.healthtracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit_challenges")
data class HabitChallenge(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // sugar_free, flour_free, water_streak, custom
    val title: String,
    val description: String,
    val startDate: String, // YYYY-MM-DD
    val targetDays: Int = 30,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val isActive: Boolean = true
)

@Entity(tableName = "habit_logs")
data class HabitLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val challengeId: Long,
    val date: String, // YYYY-MM-DD
    val status: String // completed, partial, rest, missed
)
