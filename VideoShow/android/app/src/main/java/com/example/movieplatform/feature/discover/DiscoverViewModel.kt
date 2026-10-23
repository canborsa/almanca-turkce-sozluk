package com.example.movieplatform.feature.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieplatform.domain.model.Movie
import com.example.movieplatform.domain.usecase.DiscoverMoviesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class DiscoverUiState {
    object Loading : DiscoverUiState()
    data class Success(val movies: List<Movie>) : DiscoverUiState()
    data class Error(val message: String) : DiscoverUiState()
}

class DiscoverViewModel(private val discoverMoviesUseCase: DiscoverMoviesUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow<DiscoverUiState>(DiscoverUiState.Loading)
    val uiState: StateFlow<DiscoverUiState> = _uiState

    fun applyFilters(genre: String, yearMin: String, ratingMin: String, sortBy: String) {
        viewModelScope.launch {
            _uiState.value = DiscoverUiState.Loading
            val filters = mutableMapOf<String, String>()
            if (genre.isNotEmpty()) filters["genres"] = genre
            if (yearMin.isNotEmpty()) filters["year_min"] = yearMin
            if (ratingMin.isNotEmpty()) filters["min_rating"] = ratingMin
            filters["sort_by"] = sortBy

            val result = discoverMoviesUseCase.execute(filters)
            if (result.isSuccess) {
                _uiState.value = DiscoverUiState.Success(result.getOrDefault(emptyList()))
            } else {
                _uiState.value = DiscoverUiState.Error("Film bulunamadı.")
            }
        }
    }
}
