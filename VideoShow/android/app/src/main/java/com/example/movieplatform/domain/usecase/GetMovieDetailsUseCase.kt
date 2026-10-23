package com.example.movieplatform.domain.usecase

import com.example.movieplatform.domain.model.Movie
import com.example.movieplatform.domain.repository.MovieRepository

class GetMovieDetailsUseCase(private val repository: MovieRepository) {
    suspend fun execute(movieId: Int): Result<Movie> = repository.getMovieDetail(movieId)
}
