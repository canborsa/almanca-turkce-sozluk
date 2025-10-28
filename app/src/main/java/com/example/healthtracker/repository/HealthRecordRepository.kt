package com.example.healthtracker.repository

import com.example.healthtracker.data.HealthRecord
import com.example.healthtracker.data.HealthRecordDao
import kotlinx.coroutines.flow.Flow

class HealthRecordRepository(private val healthRecordDao: HealthRecordDao) {

    fun getRecordsForUser(userId: Int): Flow<List<HealthRecord>> {
        return healthRecordDao.getRecordsForUser(userId)
    }

    suspend fun insert(record: HealthRecord) {
        healthRecordDao.insert(record)
    }
}
