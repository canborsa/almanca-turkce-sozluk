package com.example.healthtracker.ui.records

import androidx.lifecycle.*
import com.example.healthtracker.data.HealthRecord
import com.example.healthtracker.repository.HealthRecordRepository
import kotlinx.coroutines.launch

class HealthRecordViewModel(private val repository: HealthRecordRepository) : ViewModel() {

    fun getRecordsForUser(userId: Int): LiveData<List<HealthRecord>> {
        return repository.getRecordsForUser(userId).asLiveData()
    }

    fun insert(record: HealthRecord) = viewModelScope.launch {
        repository.insert(record)
    }
}

class HealthRecordViewModelFactory(private val repository: HealthRecordRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HealthRecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HealthRecordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
