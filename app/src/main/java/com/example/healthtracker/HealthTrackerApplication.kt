package com.example.healthtracker

import android.app.Application
import com.example.healthtracker.data.AppDatabase
import com.example.healthtracker.repository.HealthRecordRepository
import com.example.healthtracker.repository.UserRepository

class HealthTrackerApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { UserRepository(database.userDao()) }
    val healthRecordRepository by lazy { HealthRecordRepository(database.healthRecordDao()) }
}
