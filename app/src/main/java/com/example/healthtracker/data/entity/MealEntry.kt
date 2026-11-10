package com.example.healthtracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_entries")
data class MealEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val time: String, // HH:mm
    val mealType: String, // Breakfast, Lunch, Dinner, Snack
    val foodName: String,
    val portionMultiplier: Float = 1.0f,
    val portionUnit: String = "serving", // g, serving, piece, bowl, slice
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val sugar: Float = 0f,
    val fiber: Float = 0f,
    val sodiumMg: Float = 0f,
    val notes: String? = null
)
