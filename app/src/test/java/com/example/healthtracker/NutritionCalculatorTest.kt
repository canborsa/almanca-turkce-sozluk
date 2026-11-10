package com.example.healthtracker

import com.example.healthtracker.data.entity.UserProfile
import com.example.healthtracker.service.NutritionCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NutritionCalculatorTest {

    @Test
    fun testBmrAndTdeeCalculations() {
        val bmr = NutritionCalculator.calculateBMR(70f, 175f, 25, "male")
        assertTrue(bmr > 1600f && bmr < 1800f)

        val tdee = NutritionCalculator.calculateTDEE(bmr, "moderate")
        assertTrue(tdee > bmr)
    }

    @Test
    fun testBmiCalculation() {
        val (bmi, category) = NutritionCalculator.calculateBMI(70f, 175f)
        assertEquals(22.9f, bmi, 0.2f)
        assertEquals("Normal weight", category)
    }

    @Test
    fun testRecommendedCalories() {
        val profile = UserProfile(
            currentWeightKg = 70f,
            heightCm = 175f,
            age = 25,
            gender = "male",
            activityLevel = "moderate",
            goalType = "lose_weight"
        )
        val calories = NutritionCalculator.recommendDailyCalories(profile)
        assertTrue("Calories should be in safe range", calories in 1400..2600)
    }
}
