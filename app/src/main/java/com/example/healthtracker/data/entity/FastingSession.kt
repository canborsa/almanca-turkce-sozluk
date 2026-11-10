package com.example.healthtracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fasting_sessions")
data class FastingSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val scheduleType: String = "16:8", // 16:8, 14:10, 12:12, custom
    val targetDurationHours: Int = 16,
    val startTimeMillis: Long,
    val endTimeMillis: Long? = null,
    val isCompleted: Boolean = false,
    val notes: String? = null
)
