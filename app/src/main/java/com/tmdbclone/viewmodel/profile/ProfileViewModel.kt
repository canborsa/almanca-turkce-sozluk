package com.tmdbclone.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.convex.android.ConvexClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Simplified User and Review models for the Profile screen
data class UserProfile(
    val name: String,
    val email: String,
    val profileImageUrl: String?
)

data class UserReview(
    val _id: String,
    val movieId: Long,
    val rating: Long, // Convex v.number() is Long
    val comment: String
)

data class ProfileUiState(
    val user: UserProfile? = null,
    val reviews: List<UserReview> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProfileViewModel(private val convex: ConvexClient) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun fetchProfileData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Fetch user and reviews in parallel
                val userJob = launch { fetchUser() }
                val reviewsJob = launch { fetchUserReviews() }
                userJob.join()
                reviewsJob.join()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun fetchUser() {
        val result = convex.query("users:currentUser")
        (result as? Map<*, *>)?.let { map ->
            val user = UserProfile(
                name = map["name"] as String,
                email = map["email"] as String,
                profileImageUrl = map["profileImageUrl"] as? String
            )
            _uiState.update { it.copy(user = user) }
        }
    }

    private suspend fun fetchUserReviews() {
        val result = convex.query("reviews:getUserReviews")
        @Suppress("UNCHECKED_CAST")
        val reviews = (result as? List<Map<String, Any>>)?.map { map ->
            UserReview(
                _id = map["_id"] as String,
                movieId = map["movieId"] as Long,
                rating = map["rating"] as Long,
                comment = map["comment"] as String
            )
        } ?: emptyList()
        _uiState.update { it.copy(reviews = reviews) }
    }

    fun logout() {
        viewModelScope.launch {
            convex.auth.logout()
            // The navigation to AuthScreen will be handled by the MainActivity's observer
        }
    }
}
