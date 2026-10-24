package com.example.movieplatform.feature.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieplatform.domain.model.Movie
import com.example.movieplatform.domain.repository.MovieRepository
import com.example.movieplatform.domain.usecase.GetMovieDetailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MovieDetailUiState {
    object Loading : MovieDetailUiState()
    data class Success(val movie: Movie) : MovieDetailUiState()
    data class Error(val message: String) : MovieDetailUiState()
}

class MovieDetailViewModel(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MovieDetailUiState>(MovieDetailUiState.Loading)
    val uiState: StateFlow<MovieDetailUiState> = _uiState

    fun loadMovie(id: Int) {
        viewModelScope.launch {
            _uiState.value = MovieDetailUiState.Loading
            val result = getMovieDetailsUseCase.execute(id)
            if (result.isSuccess) {
                _uiState.value = MovieDetailUiState.Success(result.getOrThrow())
            } else {
                _uiState.value = MovieDetailUiState.Error("Film detayları alınamadı.")
            }
        }
    }

    fun toggleFavorite(movieId: Int, currentStatus: Boolean) {
        viewModelScope.launch {
            movieRepository.toggleFavorite(movieId, !currentStatus)
            loadMovie(movieId)
        }
    }

    fun addRating(movieId: Int, rating: Double) {
        viewModelScope.launch {
            movieRepository.addRating(movieId, rating)
            loadMovie(movieId)
        }
    }
}
