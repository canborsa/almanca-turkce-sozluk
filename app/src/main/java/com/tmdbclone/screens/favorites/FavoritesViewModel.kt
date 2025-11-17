package com.tmdbclone.screens.favorites

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tmdbclone.TMDBCloneApp
import com.tmdbclone.convex.ConvexClient
import dev.convex.convex.Value
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// ... (FavoriteMovie data class remains the same)
data class FavoriteMovie(
    val _id: String,
    val movieId: Long,
    val title: String,
    val posterPath: String
)

data class FavoritesUiState(
    val favoriteMovies: List<FavoriteMovie> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class FavoritesViewModel : ViewModel() {

    var uiState by mutableStateOf(FavoritesUiState())
        private set

    private val convex = ConvexClient.client
    private val sessionManager = TMDBCloneApp.sessionManager

    // This ViewModel is only active when the user is authenticated.
    init {
        fetchFavorites()
    }

    private fun fetchFavorites() {
        val userId = sessionManager.getUserId()
        if (userId == null) {
            uiState = uiState.copy(errorMessage = "User not logged in.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                convex.query("favorites:getFavorites", mapOf("userId" to Value.fromString(userId)))
                    .collectLatest { value ->
                        val movies = value.toList().map { movieValue ->
                            val movieMap = movieValue.toMap()
                            FavoriteMovie(
                                _id = (movieMap["_id"] as Value.String).string,
                                movieId = (movieMap["movieId"] as Value.Float64).number.toLong(),
                                title = (movieMap["title"] as Value.String).string,
                                posterPath = (movieMap["posterPath"] as Value.String).string
                            )
                        }
                        uiState = uiState.copy(favoriteMovies = movies, isLoading = false)
                    }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = "Failed to load favorites: ${e.message}")
            }
        }
    }
}
