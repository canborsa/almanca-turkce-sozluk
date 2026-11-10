package com.example.healthtracker.service

import com.example.healthtracker.data.entity.UserProfile
import kotlin.math.roundToInt

object NutritionCalculator {

    /**
     * Calculate Basal Metabolic Rate (BMR) using the Mifflin-St Jeor equation.
     */
    fun calculateBMR(weightKg: Float, heightCm: Float, age: Int, gender: String): Float {
        val base = (10f * weightKg) + (6.25f * heightCm) - (5f * age)
        return when (gender.lowercase()) {
            "male" -> base + 5f
            "female" -> base - 161f
            else -> base - 78f // average
        }
    }

    /**
     * Calculate Total Daily Energy Expenditure (TDEE).
     */
    fun calculateTDEE(bmr: Float, activityLevel: String): Float {
        val multiplier = when (activityLevel.lowercase()) {
            "sedentary" -> 1.2f
            "light" -> 1.375f
            "moderate" -> 1.55f
            "active" -> 1.725f
            else -> 1.4f
        }
        return bmr * multiplier
    }

    /**
     * Compute conservative daily calorie recommendation based on goals.
     */
    fun recommendDailyCalories(profile: UserProfile): Int {
        val bmr = calculateBMR(profile.currentWeightKg, profile.heightCm, profile.age, profile.gender)
        val tdee = calculateTDEE(bmr, profile.activityLevel)
        val target = when (profile.goalType.lowercase()) {
            "lose_weight" -> (tdee - 400f).coerceAtLeast(1400f) // safe deficit
            "gain_weight" -> tdee + 350f
            else -> tdee
        }
        return target.roundToInt()
    }

    /**
     * Conservative macro breakdown (Protein 25%, Carbs 50%, Fat 25%).
     */
    fun recommendMacros(dailyCalories: Int): Triple<Int, Int, Int> {
        val proteinGrams = ((dailyCalories * 0.25f) / 4f).roundToInt()
        val carbsGrams = ((dailyCalories * 0.50f) / 4f).roundToInt()
        val fatGrams = ((dailyCalories * 0.25f) / 9f).roundToInt()
        return Triple(proteinGrams, carbsGrams, fatGrams)
    }

    /**
     * BMI calculation with categorized description.
     */
    fun calculateBMI(weightKg: Float, heightCm: Float): Pair<Float, String> {
        if (heightCm <= 0f) return Pair(0f, "Unknown")
        val heightM = heightCm / 100f
        val bmi = weightKg / (heightM * heightM)
        val roundedBmi = (bmi * 10f).roundToInt() / 10f
        val category = when {
            bmi < 18.5f -> "Underweight"
            bmi < 25.0f -> "Normal weight"
            bmi < 30.0f -> "Overweight"
            else -> "Obesity"
        }
        return Pair(roundedBmi, category)
    }
}
