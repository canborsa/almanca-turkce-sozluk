package com.example.movieplatform.domain.repository

import com.example.movieplatform.domain.model.Movie

interface MovieRepository {
    suspend fun getTrendingMovies(): Result<List<Movie>>
    suspend fun getPopularMovies(): Result<List<Movie>>
    suspend fun getTopRatedMovies(): Result<List<Movie>>
    suspend fun discoverMovies(filters: Map<String, String>): Result<List<Movie>>
    suspend fun getMovieDetail(id: Int): Result<Movie>
    suspend fun addRating(movieId: Int, rating: Double): Result<Unit>
    suspend fun toggleFavorite(movieId: Int, isFavorite: Boolean): Result<Unit>
    suspend fun toggleWatchlist(movieId: Int, inWatchlist: Boolean): Result<Unit>
    suspend fun toggleWatched(movieId: Int, isWatched: Boolean): Result<Unit>
}
