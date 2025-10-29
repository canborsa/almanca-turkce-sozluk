package com.example.healthtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.data.HealthRepository
import com.example.healthtracker.data.database.User
import kotlinx.coroutines.launch

class AddUserViewModel(private val repository: HealthRepository) : ViewModel() {
    fun addUser(firstName: String, lastName: String, age: Int) {
        viewModelScope.launch {
            repository.insertUser(User(firstName = firstName, lastName = lastName, age = age))
        }
    }
}
