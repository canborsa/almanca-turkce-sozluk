package com.tmdbclone.screens.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tmdbclone.network.ApiClient
import com.tmdbclone.network.Movie
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val searchResults: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isInitial: Boolean = true // To show initial message
)

class SearchViewModel : ViewModel() {

    var uiState by mutableStateOf(SearchUiState())
        private set

    private var searchJob: Job? = null

    fun onQueryChange(newQuery: String) {
        uiState = uiState.copy(query = newQuery, isInitial = false)
        searchJob?.cancel() // Cancel previous job
        if (newQuery.isNotBlank()) {
            searchJob = viewModelScope.launch {
                delay(500L) // Debounce for 500ms
                searchMovies(newQuery)
            }
        } else {
            uiState = uiState.copy(searchResults = emptyList(), isLoading = false)
        }
    }

    private fun searchMovies(query: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val results = ApiClient.tmdbApi.searchMovies(ApiClient.API_KEY, query).results
                uiState = uiState.copy(
                    searchResults = results,
                    isLoading = false
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Failed to search movies: ${e.message}"
                )
            }
        }
    }
}