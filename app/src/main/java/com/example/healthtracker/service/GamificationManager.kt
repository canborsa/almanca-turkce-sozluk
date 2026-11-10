package com.example.healthtracker.service

import com.example.healthtracker.data.entity.Badge
import com.example.healthtracker.data.repository.VitaLogRepository
import java.text.SimpleDateFormat
import java.util.*

class GamificationManager(private val repository: VitaLogRepository) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    suspend fun checkAndAwardMealBadge() {
        val badge = repository.getBadgeByCode("first_meal")
        if (badge != null && badge.unlockedDate == null) {
            val updated = badge.copy(unlockedDate = dateFormat.format(Date()))
            repository.unlockBadge(updated)
        }
    }

    suspend fun checkSugarFreeStreak(streak: Int) {
        val today = dateFormat.format(Date())
        if (streak >= 3) {
            val badge3 = repository.getBadgeByCode("sugar_free_3")
            if (badge3 != null && badge3.unlockedDate == null) {
                repository.unlockBadge(badge3.copy(unlockedDate = today))
            }
        }
        if (streak >= 7) {
            val badge7 = repository.getBadgeByCode("sugar_free_7")
            if (badge7 != null && badge7.unlockedDate == null) {
                repository.unlockBadge(badge7.copy(unlockedDate = today))
            }
        }
        if (streak >= 30) {
            val badge30 = repository.getBadgeByCode("sugar_free_30")
            if (badge30 != null && badge30.unlockedDate == null) {
                repository.unlockBadge(badge30.copy(unlockedDate = today))
            }
        }
    }

    suspend fun checkWaterGoalCompleted(todayConsumed: Int, targetMl: Int) {
        if (todayConsumed >= targetMl) {
            val badge = repository.getBadgeByCode("water_champion")
            if (badge != null && badge.unlockedDate == null) {
                repository.unlockBadge(badge.copy(unlockedDate = dateFormat.format(Date())))
            }
        }
    }
}
