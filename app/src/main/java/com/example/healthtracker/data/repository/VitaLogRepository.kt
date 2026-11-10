package com.example.healthtracker.data.repository

import androidx.lifecycle.LiveData
import com.example.healthtracker.data.dao.VitaLogDao
import com.example.healthtracker.data.entity.*

class VitaLogRepository(private val dao: VitaLogDao) {
    // Profile
    val userProfile: LiveData<UserProfile?> = dao.getUserProfile()
    suspend fun getUserProfileDirect(): UserProfile? = dao.getUserProfileDirect()
    suspend fun saveProfile(profile: UserProfile) = dao.insertOrUpdateProfile(profile)

    // Foods
    val allFoods: LiveData<List<FoodItem>> = dao.getAllFoods()
    suspend fun searchFoods(query: String): List<FoodItem> = dao.searchFoods(query)
    suspend fun insertFood(food: FoodItem): Long = dao.insertFood(food)
    suspend fun checkAndSeedFoods(defaultFoods: List<FoodItem>) {
        if (dao.getFoodCount() == 0) {
            dao.insertFoods(defaultFoods)
        }
    }

    // Meals
    fun getMealsForDate(date: String): LiveData<List<MealEntry>> = dao.getMealsForDate(date)
    suspend fun getMealsForDateDirect(date: String): List<MealEntry> = dao.getMealsForDateDirect(date)
    suspend fun getMealsForDateRangeDirect(startDate: String, endDate: String): List<MealEntry> =
        dao.getMealsForDateRangeDirect(startDate, endDate)
    suspend fun insertMeal(meal: MealEntry): Long = dao.insertMeal(meal)
    suspend fun deleteMeal(meal: MealEntry) = dao.deleteMeal(meal)

    // Water
    fun getWaterEntriesForDate(date: String): LiveData<List<WaterEntry>> = dao.getWaterEntriesForDate(date)
    suspend fun getWaterEntriesForDateDirect(date: String): List<WaterEntry> = dao.getWaterEntriesForDateDirect(date)
    suspend fun getWaterEntriesForDateRangeDirect(startDate: String, endDate: String): List<WaterEntry> =
        dao.getWaterEntriesForDateRangeDirect(startDate, endDate)
    suspend fun logWater(amountMl: Int, date: String, time: String, beverage: String = "water"): Long =
        dao.insertWater(WaterEntry(date = date, time = time, amountMl = amountMl, beverageType = beverage))

    // Weight & Measurements
    val allWeightRecords: LiveData<List<WeightRecord>> = dao.getAllWeightRecords()
    suspend fun getWeightRecordsForDateRangeDirect(startDate: String, endDate: String): List<WeightRecord> =
        dao.getWeightRecordsForDateRangeDirect(startDate, endDate)
    suspend fun insertWeightRecord(record: WeightRecord): Long = dao.insertWeightRecord(record)

    // Activities
    fun getActivitiesForDate(date: String): LiveData<List<ActivityRecord>> = dao.getActivitiesForDate(date)
    suspend fun getActivitiesForDateRangeDirect(startDate: String, endDate: String): List<ActivityRecord> =
        dao.getActivitiesForDateRangeDirect(startDate, endDate)
    suspend fun insertActivity(activity: ActivityRecord): Long = dao.insertActivity(activity)

    // Fasting
    val latestFastingSession: LiveData<FastingSession?> = dao.getLatestFastingSession()
    suspend fun getLatestFastingSessionDirect(): FastingSession? = dao.getLatestFastingSessionDirect()
    suspend fun saveFastingSession(session: FastingSession): Long = dao.insertOrUpdateFasting(session)

    // Habits & Challenges
    val activeChallenges: LiveData<List<HabitChallenge>> = dao.getActiveChallenges()
    suspend fun getAllChallengesDirect(): List<HabitChallenge> = dao.getAllChallengesDirect()
    suspend fun insertChallenge(challenge: HabitChallenge): Long = dao.insertChallenge(challenge)
    suspend fun updateChallenge(challenge: HabitChallenge) = dao.updateChallenge(challenge)
    suspend fun getLogsForChallenge(challengeId: Long): List<HabitLog> = dao.getLogsForChallenge(challengeId)
    suspend fun logHabitDay(challengeId: Long, date: String, status: String): Long =
        dao.insertHabitLog(HabitLog(challengeId = challengeId, date = date, status = status))

    // Badges
    val allBadges: LiveData<List<Badge>> = dao.getAllBadges()
    val unlockedBadges: LiveData<List<Badge>> = dao.getUnlockedBadges()
    suspend fun getBadgeByCode(code: String): Badge? = dao.getBadgeByCode(code)
    suspend fun unlockBadge(badge: Badge) = dao.updateBadge(badge)

    // Wipe data
    suspend fun clearAllData() {
        dao.clearMeals()
        dao.clearWater()
        dao.clearWeight()
        dao.clearActivities()
        dao.clearFasting()
        dao.clearHabitLogs()
    }
}
