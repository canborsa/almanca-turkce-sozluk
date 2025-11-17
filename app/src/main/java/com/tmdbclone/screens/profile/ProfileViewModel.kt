package com.tmdbclone.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tmdbclone.auth.AuthViewModel
import kotlinx.coroutines.launch

class ProfileViewModel(private val authViewModel: AuthViewModel) : ViewModel() {

    fun signOut() {
        // The actual sign-out logic is in AuthViewModel to keep it centralized.
        // This ViewModel just triggers it.
        authViewModel.signOut()
    }
}
