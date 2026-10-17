package com.example.healthtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.healthtracker.data.HealthRepository

class ViewModelFactory(private val repository: HealthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddUserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddUserViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(AddRecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddRecordViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(RecordListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecordListViewModel(repository) as T
        }
        // Add other ViewModels here
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
