package com.tmdbclone.screens.watchlist

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

// ... (WatchlistMovie data class remains the same)
data class WatchlistMovie(
    val _id: String,
    val movieId: Long,
    val title: String,
    val posterPath: String
)

data class WatchlistUiState(
    val watchlistMovies: List<WatchlistMovie> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class WatchlistViewModel : ViewModel() {

    var uiState by mutableStateOf(WatchlistUiState())
        private set

    private val convex = ConvexClient.client
    private val sessionManager = TMDBCloneApp.sessionManager

    // This ViewModel is only active when the user is authenticated.
    init {
        fetchWatchlist()
    }

    private fun fetchWatchlist() {
        val userId = sessionManager.getUserId()
        if (userId == null) {
            uiState = uiState.copy(errorMessage = "User not logged in.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                convex.query("watchlist:getWatchlist", mapOf("userId" to Value.fromString(userId)))
                    .collectLatest { value ->
                        val movies = value.toList().map { movieValue ->
                            val movieMap = movieValue.toMap()
                            WatchlistMovie(
                                _id = (movieMap["_id"] as Value.String).string,
                                movieId = (movieMap["movieId"] as Value.Float64).number.toLong(),
                                title = (movieMap["title"] as Value.String).string,
                                posterPath = (movieMap["posterPath"] as Value.String).string
                            )
                        }
                        uiState = uiState.copy(watchlistMovies = movies, isLoading = false)
                    }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = "Failed to load watchlist: ${e.message}")
            }
        }
    }
}
