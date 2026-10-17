package com.example.healthtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.healthtracker.data.HealthRepository

class RecordListViewModel(private val repository: HealthRepository) : ViewModel() {
    val latestUser = repository.getLatestUser().asLiveData()

    fun getRecordsForUser(userId: Long) = repository.getRecordsForUser(userId).asLiveData()

    fun isBloodSugarOutOfRange(age: Int, sugar: Int): Boolean {
        val (low, high) = when {
            age < 6 -> 100 to 180
            age in 6..12 -> 90 to 180
            age in 13..19 -> 90 to 130
            age in 20..64 -> 90 to 130
            else -> 70 to 180 // 65+
        }
        return sugar < low || sugar > high
    }

    fun isBloodPressureOutOfRange(age: Int, systolic: Int, diastolic: Int): Boolean {
        val (normalSystolic, normalDiastolic) = when {
            age in 18..39 -> 119 to 70
            age in 40..59 -> 124 to 77
            age >= 60 -> 139 to 68
            else -> 120 to 80 // Default/younger
        }
        // Check for high or low pressure
        return systolic > normalSystolic + 10 || systolic < normalSystolic - 10 ||
               diastolic > normalDiastolic + 10 || diastolic < normalDiastolic - 10
    }
}
