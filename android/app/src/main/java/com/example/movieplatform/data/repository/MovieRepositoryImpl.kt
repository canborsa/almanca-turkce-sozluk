package com.example.movieplatform.data.repository

import com.example.movieplatform.data.local.dao.MovieDao
import com.example.movieplatform.data.local.entity.MovieEntity
import com.example.movieplatform.data.remote.MovieApi
import com.example.movieplatform.domain.model.Movie
import com.example.movieplatform.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieRepositoryImpl(
    private val api: MovieApi,
    private val dao: MovieDao
) : MovieRepository {

    override fun getMovies(): Flow<List<Movie>> {
        return dao.getAllMovies().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun fetchMovies(): Result<List<Movie>> {
        return try {
            val response = api.getMovies()
            if (response.success && response.data != null) {
                val movies = response.data.map { apiMovie ->
                    Movie(
                        id = apiMovie.id,
                        tmdbId = apiMovie.tmdbId,
                        title = apiMovie.title,
                        originalTitle = apiMovie.originalTitle,
                        overview = apiMovie.overview,
                        releaseYear = apiMovie.releaseYear,
                        posterPath = apiMovie.posterPath,
                        backdropPath = apiMovie.backdropPath,
                        appVoteAverage = apiMovie.appVoteAverage,
                        tmdbVoteAverage = apiMovie.tmdbVoteAverage
                    )
                }
                dao.insertMovies(movies.map { it.toEntity() })
                Result.success(movies)
            } else {
                Result.failure(Exception(response.message ?: "Film listesi yüklenemedi"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun discoverMovies(
        genres: String?,
        yearMin: Int?,
        yearMax: Int?,
        ratingMin: Float?,
        votesMin: Int?,
        durationMin: Int?,
        durationMax: Int?,
        sortBy: String?
    ): Result<List<Movie>> {
        return try {
            val response = api.discover(genres, yearMin, yearMax, ratingMin, votesMin, durationMin, durationMax, sortBy)
            if (response.success && response.data != null) {
                val movies = response.data.map { apiMovie ->
                    Movie(
                        id = apiMovie.id,
                        tmdbId = apiMovie.tmdbId,
                        title = apiMovie.title,
                        originalTitle = apiMovie.originalTitle,
                        overview = apiMovie.overview,
                        releaseYear = apiMovie.releaseYear,
                        posterPath = apiMovie.posterPath,
                        backdropPath = apiMovie.backdropPath,
                        appVoteAverage = apiMovie.appVoteAverage,
                        tmdbVoteAverage = apiMovie.tmdbVoteAverage
                    )
                }
                Result.success(movies)
            } else {
                Result.failure(Exception(response.message ?: "Keşfet araması başarısız"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun Movie.toEntity() = MovieEntity(
        id = id,
        tmdbId = tmdbId,
        title = title,
        originalTitle = originalTitle,
        overview = overview,
        releaseYear = releaseYear,
        posterPath = posterPath,
        backdropPath = backdropPath,
        appVoteAverage = appVoteAverage,
        tmdbVoteAverage = tmdbVoteAverage
    )

    private fun MovieEntity.toDomainModel() = Movie(
        id = id,
        tmdbId = tmdbId,
        title = title,
        originalTitle = originalTitle,
        overview = overview,
        releaseYear = releaseYear,
        posterPath = posterPath,
        backdropPath = backdropPath,
        appVoteAverage = appVoteAverage,
        tmdbVoteAverage = tmdbVoteAverage
    )
}
