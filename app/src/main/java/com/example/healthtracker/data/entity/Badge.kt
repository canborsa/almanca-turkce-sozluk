package com.example.healthtracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "badges")
data class Badge(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String, // first_meal, sugar_free_3, sugar_free_7, water_goal, etc.
    val title: String,
    val description: String,
    val category: String, // nutrition, habit, hydration, fitness
    val iconName: String,
    val unlockedDate: String? = null, // YYYY-MM-DD when earned, null if locked
    val tier: String = "bronze" // bronze, silver, gold, platinum
)
