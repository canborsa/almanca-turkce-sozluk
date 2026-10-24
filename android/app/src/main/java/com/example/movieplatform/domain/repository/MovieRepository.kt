package com.example.movieplatform.domain.repository

import com.example.movieplatform.data.remote.DiscoverRequest
import com.example.movieplatform.domain.model.Movie

interface MovieRepository {
    suspend fun getMovies(page: Int): Result<List<Movie>>
    suspend fun getMovieDetail(id: Int): Result<Movie>
    suspend fun searchMovies(query: String): Result<List<Movie>>
    suspend fun discoverMovies(request: DiscoverRequest): Result<List<Movie>>
}
