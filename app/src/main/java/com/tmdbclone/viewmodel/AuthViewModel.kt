package com.tmdbclone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.convex.android.ConvexClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)

class AuthViewModel(private val convex: ConvexClient) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkInitialSession()
    }

    private fun checkInitialSession() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val session = convex.auth.session()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isLoggedIn = session != null
                )
            }
            if (session != null) {
                // Ensure user exists in our DB
                convex.mutation("users:getOrCreateUser")
            }
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                convex.auth.loginWithGoogle()
                // After login, ensure user is in our database
                convex.mutation("users:getOrCreateUser")
                _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Login failed: ${e.message}"
                    )
                }
            }
        }
    }

    fun signUpWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                convex.auth.register(email, pass)
                // After signup, ensure user is in our database
                convex.mutation("users:getOrCreateUser")
                _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Sign up failed: ${e.message}") }
            }
        }
    }

    fun signInWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                convex.auth.login(email, pass)
                 // After login, ensure user is in our database
                convex.mutation("users:getOrCreateUser")
                _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Login failed: ${e.message}") }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            convex.auth.logout()
            _uiState.update { AuthUiState() } // Reset state
        }
    }
}
