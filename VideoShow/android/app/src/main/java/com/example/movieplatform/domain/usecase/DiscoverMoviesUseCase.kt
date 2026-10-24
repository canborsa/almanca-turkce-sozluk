package com.example.movieplatform.domain.usecase

import com.example.movieplatform.domain.model.Movie
import com.example.movieplatform.domain.repository.MovieRepository

class DiscoverMoviesUseCase(private val repository: MovieRepository) {
    suspend fun execute(filters: Map<String, String>): Result<List<Movie>> = repository.discoverMovies(filters)
}
