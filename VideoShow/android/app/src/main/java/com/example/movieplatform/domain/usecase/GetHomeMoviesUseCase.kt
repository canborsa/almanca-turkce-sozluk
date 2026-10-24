package com.example.movieplatform.domain.usecase

import com.example.movieplatform.domain.model.Movie
import com.example.movieplatform.domain.repository.MovieRepository

class GetHomeMoviesUseCase(private val repository: MovieRepository) {
    suspend fun executeTrending(): Result<List<Movie>> = repository.getTrendingMovies()
    suspend fun executePopular(): Result<List<Movie>> = repository.getPopularMovies()
    suspend fun executeTopRated(): Result<List<Movie>> = repository.getTopRatedMovies()
}
