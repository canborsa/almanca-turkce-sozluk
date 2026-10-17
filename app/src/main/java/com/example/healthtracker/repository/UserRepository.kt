package com.example.healthtracker.repository

import com.example.healthtracker.data.User
import com.example.healthtracker.data.UserDao
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    val allUsers: Flow<List<User>> = userDao.getAllUsers()

    suspend fun insert(user: User) {
        userDao.insert(user)
    }
}
