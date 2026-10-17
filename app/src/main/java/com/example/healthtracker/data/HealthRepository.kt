package com.example.healthtracker.data

import com.example.healthtracker.data.database.HealthRecord
import com.example.healthtracker.data.database.HealthRecordDao
import com.example.healthtracker.data.database.User
import com.example.healthtracker.data.database.UserDao
import kotlinx.coroutines.flow.Flow

class HealthRepository(
    private val userDao: UserDao,
    private val healthRecordDao: HealthRecordDao
) {
    fun getLatestUser(): Flow<User?> = userDao.getLatestUser()

    suspend fun insertUser(user: User): Long = userDao.insert(user)

    fun getRecordsForUser(userId: Long): Flow<List<HealthRecord>> =
        healthRecordDao.getRecordsForUser(userId)

    suspend fun insertHealthRecord(record: HealthRecord) {
        healthRecordDao.insert(record)
    }
}
