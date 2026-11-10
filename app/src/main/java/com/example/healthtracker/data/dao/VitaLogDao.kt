package com.example.healthtracker.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.healthtracker.data.entity.*

@Dao
interface VitaLogDao {
    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): LiveData<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileDirect(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)

    // Food Items
    @Query("SELECT * FROM food_items ORDER BY name ASC")
    fun getAllFoods(): LiveData<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE name LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun searchFoods(query: String): List<FoodItem>

    @Query("SELECT COUNT(*) FROM food_items")
    suspend fun getFoodCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoods(foods: List<FoodItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: FoodItem): Long

    // Meal Entries
    @Query("SELECT * FROM meal_entries WHERE date = :date ORDER BY id ASC")
    fun getMealsForDate(date: String): LiveData<List<MealEntry>>

    @Query("SELECT * FROM meal_entries WHERE date = :date ORDER BY id ASC")
    suspend fun getMealsForDateDirect(date: String): List<MealEntry>

    @Query("SELECT * FROM meal_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC, id ASC")
    suspend fun getMealsForDateRangeDirect(startDate: String, endDate: String): List<MealEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntry): Long

    @Delete
    suspend fun deleteMeal(meal: MealEntry)

    // Water Entries
    @Query("SELECT * FROM water_entries WHERE date = :date ORDER BY id ASC")
    fun getWaterEntriesForDate(date: String): LiveData<List<WaterEntry>>

    @Query("SELECT * FROM water_entries WHERE date = :date")
    suspend fun getWaterEntriesForDateDirect(date: String): List<WaterEntry>

    @Query("SELECT * FROM water_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getWaterEntriesForDateRangeDirect(startDate: String, endDate: String): List<WaterEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWater(water: WaterEntry): Long

    // Weight Records
    @Query("SELECT * FROM weight_records ORDER BY date DESC")
    fun getAllWeightRecords(): LiveData<List<WeightRecord>>

    @Query("SELECT * FROM weight_records WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getWeightRecordsForDateRangeDirect(startDate: String, endDate: String): List<WeightRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightRecord(record: WeightRecord): Long

    // Activities
    @Query("SELECT * FROM activity_records WHERE date = :date ORDER BY id ASC")
    fun getActivitiesForDate(date: String): LiveData<List<ActivityRecord>>

    @Query("SELECT * FROM activity_records WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getActivitiesForDateRangeDirect(startDate: String, endDate: String): List<ActivityRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityRecord): Long

    // Fasting
    @Query("SELECT * FROM fasting_sessions ORDER BY startTimeMillis DESC LIMIT 1")
    fun getLatestFastingSession(): LiveData<FastingSession?>

    @Query("SELECT * FROM fasting_sessions ORDER BY startTimeMillis DESC LIMIT 1")
    suspend fun getLatestFastingSessionDirect(): FastingSession?

    @Query("SELECT * FROM fasting_sessions WHERE startTimeMillis >= :startMillis ORDER BY startTimeMillis ASC")
    suspend fun getFastingSessionsSinceDirect(startMillis: Long): List<FastingSession>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateFasting(session: FastingSession): Long

    // Habit Challenges & Logs
    @Query("SELECT * FROM habit_challenges WHERE isActive = 1")
    fun getActiveChallenges(): LiveData<List<HabitChallenge>>

    @Query("SELECT * FROM habit_challenges")
    suspend fun getAllChallengesDirect(): List<HabitChallenge>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: HabitChallenge): Long

    @Update
    suspend fun updateChallenge(challenge: HabitChallenge)

    @Query("SELECT * FROM habit_logs WHERE challengeId = :challengeId ORDER BY date DESC")
    suspend fun getLogsForChallenge(challengeId: Long): List<HabitLog>

    @Query("SELECT * FROM habit_logs WHERE date = :date")
    suspend fun getLogsForDateDirect(date: String): List<HabitLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitLog(log: HabitLog): Long

    // Badges
    @Query("SELECT * FROM badges ORDER BY id ASC")
    fun getAllBadges(): LiveData<List<Badge>>

    @Query("SELECT * FROM badges WHERE unlockedDate IS NOT NULL ORDER BY unlockedDate DESC")
    fun getUnlockedBadges(): LiveData<List<Badge>>

    @Query("SELECT * FROM badges WHERE code = :code LIMIT 1")
    suspend fun getBadgeByCode(code: String): Badge?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBadges(badges: List<Badge>)

    @Update
    suspend fun updateBadge(badge: Badge)

    // Data wipe / deletion
    @Query("DELETE FROM meal_entries")
    suspend fun clearMeals()

    @Query("DELETE FROM water_entries")
    suspend fun clearWater()

    @Query("DELETE FROM weight_records")
    suspend fun clearWeight()

    @Query("DELETE FROM activity_records")
    suspend fun clearActivities()

    @Query("DELETE FROM fasting_sessions")
    suspend fun clearFasting()

    @Query("DELETE FROM habit_logs")
    suspend fun clearHabitLogs()
}
