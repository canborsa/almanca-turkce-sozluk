package com.tmdbclone.viewmodel.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.convex.android.ConvexClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Represents a movie from Convex DB (which has slightly different fields)
data class ConvexMovie(
    val _id: String,
    val movieId: Long, // Convex v.number() is Long
    val posterUrl: String,
    val title: String,
    val userId: String
)

data class FavoritesUiState(
    val favoriteMovies: List<ConvexMovie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class FavoritesViewModel(private val convex: ConvexClient) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    fun fetchFavorites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val result = convex.query("favorites:getFavorites")

                @Suppress("UNCHECKED_CAST")
                val movies = (result as? List<Map<String, Any>>)?.map { map ->
                    ConvexMovie(
                        _id = map["_id"] as String,
                        movieId = map["movieId"] as Long,
                        posterUrl = map["posterUrl"] as String,
                        title = map["title"] as String,
                        userId = map["userId"] as String
                    )
                } ?: emptyList()

                _uiState.update {
                    it.copy(isLoading = false, favoriteMovies = movies)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
