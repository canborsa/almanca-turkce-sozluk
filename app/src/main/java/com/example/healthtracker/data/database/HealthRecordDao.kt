package com.example.healthtracker.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthRecordDao {
    @Insert
    suspend fun insert(record: HealthRecord)

    @Query("SELECT * FROM health_records WHERE userId = :userId ORDER BY timestamp DESC")
    fun getRecordsForUser(userId: Long): Flow<List<HealthRecord>>
}
