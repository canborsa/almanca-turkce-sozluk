package com.example.healthtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.data.HealthRepository
import com.example.healthtracker.data.database.HealthRecord
import kotlinx.coroutines.launch

class AddRecordViewModel(private val repository: HealthRepository) : ViewModel() {
    fun addHealthRecord(userId: Long, bloodSugar: Int, systolic: Int, diastolic: Int) {
        viewModelScope.launch {
            val record = HealthRecord(
                userId = userId,
                bloodSugar = bloodSugar,
                systolicPressure = systolic,
                diastolicPressure = diastolic
            )
            repository.insertHealthRecord(record)
        }
    }
}
