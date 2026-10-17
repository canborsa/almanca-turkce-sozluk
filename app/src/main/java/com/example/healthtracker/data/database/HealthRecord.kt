package com.example.healthtracker.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "health_records",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HealthRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val bloodSugar: Int,
    val systolicPressure: Int,
    val diastolicPressure: Int,
    val timestamp: Long = System.currentTimeMillis()
)
