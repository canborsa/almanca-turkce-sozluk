package com.tmdbclone.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.tmdbclone.BuildConfig
import com.tmdbclone.TMDBCloneApp
import com.tmdbclone.network.ConvexAuthApi
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val isAuthLoading: Boolean = true, // Start as true to check session status
    val errorMessage: String? = null
)

class AuthViewModel : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    private val sessionManager = TMDBCloneApp.sessionManager

    private val convexAuthApi: ConvexAuthApi by lazy {
        // The URL for HTTP actions is the deployment URL ending in .run
        val httpActionUrl = BuildConfig.CONVEX_URL.replace(".cloud", ".run")
        Retrofit.Builder()
            .baseUrl(httpActionUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ConvexAuthApi::class.java)
    }

    init {
        checkSession()
    }

    private fun checkSession() {
        val userId = sessionManager.getUserId()
        uiState = uiState.copy(isAuthenticated = (userId != null), isAuthLoading = false)
    }

    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isAuthLoading = true, errorMessage = null)
            try {
                val body = JsonObject().apply {
                    addProperty("name", name)
                    addProperty("email", email)
                    addProperty("password", password)
                }
                val response = convexAuthApi.signUp(body)
                if (response.isSuccessful) {
                    val userId = response.body()?.get("userId")?.asString
                    if (userId != null) {
                        sessionManager.saveSession(userId)
                        uiState = uiState.copy(isAuthenticated = true, isAuthLoading = false)
                    } else {
                         uiState = uiState.copy(errorMessage = "Sign up failed: Missing user ID", isAuthLoading = false)
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Sign up failed"
                    uiState = uiState.copy(errorMessage = errorBody, isAuthLoading = false)
                }
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message, isAuthLoading = false)
            }
        }
    }

    fun logIn(email: String, password: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isAuthLoading = true, errorMessage = null)
            try {
                val body = JsonObject().apply {
                    addProperty("email", email)
                    addProperty("password", password)
                }
                val response = convexAuthApi.logIn(body)
                 if (response.isSuccessful) {
                    val userId = response.body()?.get("userId")?.asString
                    if (userId != null) {
                        sessionManager.saveSession(userId)
                        uiState = uiState.copy(isAuthenticated = true, isAuthLoading = false)
                    } else {
                         uiState = uiState.copy(errorMessage = "Login failed: Missing user ID", isAuthLoading = false)
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Login failed"
                    uiState = uiState.copy(errorMessage = errorBody, isAuthLoading = false)
                }
            } catch (e: Exception) {
                 uiState = uiState.copy(errorMessage = e.message, isAuthLoading = false)
            }
        }
    }

    fun signOut() {
        sessionManager.clearSession()
        uiState = uiState.copy(isAuthenticated = false)
    }
}
