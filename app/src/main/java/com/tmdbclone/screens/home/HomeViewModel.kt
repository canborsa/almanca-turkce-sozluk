package com.tmdbclone.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tmdbclone.network.ApiClient
import com.tmdbclone.network.Movie
import kotlinx.coroutines.launch

// Represents the state of the Home Screen UI
data class HomeUiState(
    val trendingMovies: List<Movie> = emptyList(),
    val popularMovies: List<Movie> = emptyList(),
    val upcomingMovies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class HomeViewModel : ViewModel() {

    var uiState by mutableStateOf(HomeUiState())
        private set

    init {
        fetchAllMovies()
    }

    fun fetchAllMovies() { // Made public to allow refresh
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                // Fetch online content
                val trending = ApiClient.tmdbApi.getTrendingMovies(ApiClient.API_KEY).results
                val popular = ApiClient.tmdbApi.getPopularMovies(ApiClient.API_KEY).results
                val upcoming = ApiClient.tmdbApi.getUpcomingMovies(ApiClient.API_KEY).results

                uiState = uiState.copy(
                    trendingMovies = trending,
                    popularMovies = popular,
                    upcomingMovies = upcoming,
                    isLoading = false
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Network error: ${e.message}"
                )
            }
        }
    }
}