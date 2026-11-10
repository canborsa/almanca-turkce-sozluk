package com.example.healthtracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val brand: String? = null,
    val servingDescription: String = "100g",
    val servingSizeGrams: Float = 100f,
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val sugar: Float = 0f,
    val fiber: Float = 0f,
    val sodiumMg: Float = 0f,
    val isCustom: Boolean = false,
    val category: String = "General" // General, Turkish, International, Snack, Beverage
)
