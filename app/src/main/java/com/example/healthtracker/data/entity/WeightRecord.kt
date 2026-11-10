package com.example.healthtracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_records")
data class WeightRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val weightKg: Float,
    val waistCm: Float? = null,
    val hipCm: Float? = null,
    val chestCm: Float? = null,
    val bodyFatPercentage: Float? = null,
    val notes: String? = null
)
