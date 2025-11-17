package com.tmdbclone.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tmdbclone.auth.AuthViewModel
import com.tmdbclone.screens.profile.ProfileViewModel

class ViewModelFactory(private val authViewModel: AuthViewModel) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(authViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
