package com.example.movieplatform.data.repository

import com.example.movieplatform.core.network.NetworkClient
import com.example.movieplatform.data.remote.DiscoverRequest
import com.example.movieplatform.domain.model.Movie
import com.example.movieplatform.domain.repository.MovieRepository

class MovieRepositoryImpl : MovieRepository {

    private val api = NetworkClient.apiService

    override suspend fun getMovies(page: Int): Result<List<Movie>> {
        return try {
            val response = api.getMovies(page = page)
            if (response.success && response.data != null) {
                val movies = response.data.map { dto ->
                    Movie(
                        id = dto.id,
                        tmdbId = dto.tmdbId,
                        title = dto.title,
                        originalTitle = dto.originalTitle,
                        overview = dto.overview,
                        posterPath = dto.posterPath,
                        backdropPath = dto.backdropPath,
                        voteAverage = dto.voteAverage,
                        voteCount = dto.voteCount,
                        releaseYear = dto.releaseYear,
                        runtime = dto.runtime
                    )
                }
                Result.success(movies)
            } else {
                Result.failure(Exception(response.message ?: "Film listesi yüklenemedi."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMovieDetail(id: Int): Result<Movie> {
        return try {
            val response = api.getMovieDetail(id)
            if (response.success && response.data != null) {
                val dto = response.data
                Result.success(
                    Movie(
                        id = dto.id,
                        tmdbId = dto.tmdbId,
                        title = dto.title,
                        originalTitle = dto.originalTitle,
                        overview = dto.overview,
                        posterPath = dto.posterPath,
                        backdropPath = dto.backdropPath,
                        voteAverage = dto.voteAverage,
                        voteCount = dto.voteCount,
                        releaseYear = dto.releaseYear,
                        runtime = dto.runtime
                    )
                )
            } else {
                Result.failure(Exception(response.message ?: "Film detayı bulunamadı."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchMovies(query: String): Result<List<Movie>> {
        return try {
            val response = api.searchMovies(query)
            if (response.success && response.data != null) {
                val movies = response.data.map { dto ->
                    Movie(
                        id = dto.id,
                        tmdbId = dto.tmdbId,
                        title = dto.title,
                        originalTitle = dto.originalTitle,
                        overview = dto.overview,
                        posterPath = dto.posterPath,
                        backdropPath = dto.backdropPath,
                        voteAverage = dto.voteAverage,
                        voteCount = dto.voteCount,
                        releaseYear = dto.releaseYear,
                        runtime = dto.runtime
                    )
                }
                Result.success(movies)
            } else {
                Result.failure(Exception(response.message ?: "Arama sonucu bulunamadı."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun discoverMovies(request: DiscoverRequest): Result<List<Movie>> {
        return try {
            val response = api.discoverMovies(request)
            if (response.success && response.data != null) {
                val movies = response.data.map { dto ->
                    Movie(
                        id = dto.id,
                        tmdbId = dto.tmdbId,
                        title = dto.title,
                        originalTitle = dto.originalTitle,
                        overview = dto.overview,
                        posterPath = dto.posterPath,
                        backdropPath = dto.backdropPath,
                        voteAverage = dto.voteAverage,
                        voteCount = dto.voteCount,
                        releaseYear = dto.releaseYear,
                        runtime = dto.runtime
                    )
                }
                Result.success(movies)
            } else {
                Result.failure(Exception(response.message ?: "Filtreleme hatası."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
