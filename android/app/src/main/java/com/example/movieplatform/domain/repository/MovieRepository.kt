package com.example.movieplatform.domain.repository

import com.example.movieplatform.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getMovies(): Flow<List<Movie>>
    suspend fun fetchMovies(): Result<List<Movie>>
    suspend fun discoverMovies(
        genres: String? = null,
        yearMin: Int? = null,
        yearMax: Int? = null,
        ratingMin: Float? = null,
        votesMin: Int? = null,
        durationMin: Int? = null,
        durationMax: Int? = null,
        sortBy: String? = "popularity"
    ): Result<List<Movie>>
}
