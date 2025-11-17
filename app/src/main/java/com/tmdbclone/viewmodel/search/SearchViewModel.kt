package com.tmdbclone.viewmodel.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tmdbclone.data.model.Movie
import com.tmdbclone.data.network.RetrofitInstance
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val searchResults: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class SearchViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
        searchJob?.cancel() // Cancel previous job
        if (newQuery.isNotBlank()) {
            searchJob = viewModelScope.launch {
                delay(500L) // Debounce for 500ms
                searchMovies(newQuery)
            }
        } else {
            _uiState.update { it.copy(searchResults = emptyList()) }
        }
    }

    private suspend fun searchMovies(query: String) {
        _uiState.update { it.copy(isLoading = true) }
        try {
            val results = RetrofitInstance.api.searchMovies(query).results
            _uiState.update {
                it.copy(
                    isLoading = false,
                    searchResults = results
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "Search failed: ${e.message}"
                )
            }
        }
    }
}
