package com.example.movieplatform.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieplatform.domain.model.Movie
import com.example.movieplatform.domain.usecase.GetHomeMoviesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val trending: List<Movie>, val popular: List<Movie>, val topRated: List<Movie>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(private val getHomeMoviesUseCase: GetHomeMoviesUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            val trendingRes = getHomeMoviesUseCase.executeTrending()
            val popularRes = getHomeMoviesUseCase.executePopular()
            val topRatedRes = getHomeMoviesUseCase.executeTopRated()

            if (trendingRes.isSuccess || popularRes.isSuccess) {
                _uiState.value = HomeUiState.Success(
                    trending = trendingRes.getOrDefault(emptyList()),
                    popular = popularRes.getOrDefault(emptyList()),
                    topRated = topRatedRes.getOrDefault(emptyList())
                )
            } else {
                _uiState.value = HomeUiState.Error("Veriler yüklenirken hata oluştu.")
            }
        }
    }
}
