package com.tmdbclone.viewmodel.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tmdbclone.viewmodel.favorites.ConvexMovie
import dev.convex.android.ConvexClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WatchlistUiState(
    val watchlistMovies: List<ConvexMovie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class WatchlistViewModel(private val convex: ConvexClient) : ViewModel() {

    private val _uiState = MutableStateFlow(WatchlistUiState())
    val uiState: StateFlow<WatchlistUiState> = _uiState.asStateFlow()

    fun fetchWatchlist() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val result = convex.query("watchlist:getWatchlist")

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
                    it.copy(isLoading = false, watchlistMovies = movies)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
