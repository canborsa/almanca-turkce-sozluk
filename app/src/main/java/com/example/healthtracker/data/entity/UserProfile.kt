package com.example.healthtracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Long = 1,
    var name: String = "VitaLog User",
    var age: Int = 28,
    var heightCm: Float = 175f,
    var startWeightKg: Float = 75f,
    var currentWeightKg: Float = 75f,
    var targetWeightKg: Float = 70f,
    var gender: String = "unspecified", // male, female, unspecified
    var activityLevel: String = "moderate", // sedentary, light, moderate, active
    var goalType: String = "lose_weight", // lose_weight, maintain, gain_weight, healthy_habits
    var dailyCalorieTarget: Int = 2000,
    var dailyWaterTargetMl: Int = 2500,
    var targetProteinG: Int = 120,
    var targetCarbsG: Int = 220,
    var targetFatG: Int = 65,
    var coachingStyle: String = "supportive", // supportive, calm, energetic, data_driven
    var language: String = "en", // en, tr, es, de, fr, pt
    // Module toggles (true = enabled/visible, false = hidden)
    var moduleCalories: Boolean = true,
    var moduleWater: Boolean = true,
    var moduleHabits: Boolean = true,
    var moduleFasting: Boolean = true,
    var moduleWeight: Boolean = true,
    var moduleActivity: Boolean = true,
    var moduleCoach: Boolean = true,
    var moduleReports: Boolean = true
)
