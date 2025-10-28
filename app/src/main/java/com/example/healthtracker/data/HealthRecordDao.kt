package com.example.healthtracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthRecordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(record: HealthRecord)

    @Query("SELECT * FROM health_records WHERE userId = :userId ORDER BY date DESC")
    fun getRecordsForUser(userId: Int): Flow<List<HealthRecord>>
}
