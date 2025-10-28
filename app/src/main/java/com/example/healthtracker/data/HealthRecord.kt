package com.example.healthtracker.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "health_records",
        foreignKeys = [ForeignKey(entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE)])
data class HealthRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val type: String, // "BLOOD_PRESSURE" or "SUGAR"
    val value1: String, // Systolic for BP, or sugar value
    val value2: String?, // Diastolic for BP, null for sugar
    val date: Long
)
