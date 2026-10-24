package com.example.movieplatform.data.repository

import com.example.movieplatform.core.network.ApiService
import com.example.movieplatform.data.local.MovieDao
import com.example.movieplatform.data.local.MovieEntity
import com.example.movieplatform.data.model.MovieDto
import com.example.movieplatform.domain.model.CastPerson
import com.example.movieplatform.domain.model.CrewPerson
import com.example.movieplatform.domain.model.Genre
import com.example.movieplatform.domain.model.Movie
import com.example.movieplatform.domain.repository.MovieRepository

class MovieRepositoryImpl(
    private val apiService: ApiService,
    private val movieDao: MovieDao
) : MovieRepository {

    override suspend fun getTrendingMovies(): Result<List<Movie>> {
        return try {
            val response = apiService.getTrendingMovies()
            if (response.isSuccessful && response.body()?.success == true) {
                val dtos = response.body()?.data ?: emptyList()
                val movies = dtos.map { it.toDomain() }

                // Offline caching
                movieDao.clearCategory("trending")
                movieDao.insertMovies(dtos.map { it.toEntity("trending") })
                Result.success(movies)
            } else {
                getOfflineMovies("trending")
            }
        } catch (e: Exception) {
            getOfflineMovies("trending")
        }
    }

    override suspend fun getPopularMovies(): Result<List<Movie>> {
        return try {
            val response = apiService.getPopularMovies()
            if (response.isSuccessful && response.body()?.success == true) {
                val dtos = response.body()?.data ?: emptyList()
                Result.success(dtos.map { it.toDomain() })
            } else {
                getOfflineMovies("popular")
            }
        } catch (e: Exception) {
            getOfflineMovies("popular")
        }
    }

    override suspend fun getTopRatedMovies(): Result<List<Movie>> {
        return try {
            val response = apiService.getTopRatedMovies()
            if (response.isSuccessful && response.body()?.success == true) {
                val dtos = response.body()?.data ?: emptyList()
                Result.success(dtos.map { it.toDomain() })
            } else {
                getOfflineMovies("top_rated")
            }
        } catch (e: Exception) {
            getOfflineMovies("top_rated")
        }
    }

    override suspend fun discoverMovies(filters: Map<String, String>): Result<List<Movie>> {
        return try {
            val response = apiService.discoverMovies(filters)
            if (response.isSuccessful && response.body()?.success == true) {
                val dtos = response.body()?.data ?: emptyList()
                Result.success(dtos.map { it.toDomain() })
            } else {
                Result.failure(Exception(response.body()?.message ?: "Film keşfi başarısız."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMovieDetail(id: Int): Result<Movie> {
        return try {
            val response = apiService.getMovieDetail(id)
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.toDomain())
            } else {
                Result.failure(Exception("Film bulunamadı"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addRating(movieId: Int, rating: Double): Result<Unit> {
        return try {
            val response = apiService.addRating(mapOf("movie_id" to movieId, "rating" to rating))
            if (response.isSuccessful && response.body()?.success == true) Result.success(Unit)
            else Result.failure(Exception("Puan eklenemedi"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleFavorite(movieId: Int, isFavorite: Boolean): Result<Unit> {
        return try {
            val response = if (isFavorite) apiService.addFavorite(mapOf("movie_id" to movieId))
            else apiService.removeFavorite(mapOf("movie_id" to movieId))
            if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception("Hata"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleWatchlist(movieId: Int, inWatchlist: Boolean): Result<Unit> {
        return try {
            val response = if (inWatchlist) apiService.addWatchlist(mapOf("movie_id" to movieId))
            else apiService.removeWatchlist(mapOf("movie_id" to movieId))
            if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception("Hata"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleWatched(movieId: Int, isWatched: Boolean): Result<Unit> {
        return try {
            val response = if (isWatched) apiService.addWatched(mapOf("movie_id" to movieId))
            else apiService.removeWatched(mapOf("movie_id" to movieId))
            if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception("Hata"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getOfflineMovies(category: String): Result<List<Movie>> {
        val cached = movieDao.getMoviesByCategory(category)
        return if (cached.isNotEmpty()) {
            Result.success(cached.map { it.toDomain() })
        } else {
            Result.failure(Exception("İnternet bağlantısı yok ve yerel veri bulunamadı."))
        }
    }

    private fun MovieDto.toDomain() = Movie(
        id = id,
        tmdbId = tmdbId,
        imdbId = imdbId,
        title = title,
        originalTitle = originalTitle,
        overview = overview,
        tagline = tagline,
        releaseDate = releaseDate,
        releaseYear = releaseYear,
        runtime = runtime,
        posterPath = posterPath,
        backdropPath = backdropPath,
        voteAverage = voteAverage ?: 0.0,
        appVoteAverage = appVoteAverage ?: 0.0,
        userRating = userRating,
        isFavorite = isFavorite ?: false,
        inWatchlist = inWatchlist ?: false,
        isWatched = isWatched ?: false,
        genres = genres?.map { Genre(it.id, it.name) } ?: emptyList(),
        cast = cast?.map { CastPerson(it.id, it.name, it.profilePath, it.characterName) } ?: emptyList(),
        directors = directors?.map { CrewPerson(it.id, it.name, it.profilePath, it.job) } ?: emptyList()
    )

    private fun MovieDto.toEntity(category: String) = MovieEntity(
        id = id,
        tmdbId = tmdbId,
        title = title,
        originalTitle = originalTitle,
        overview = overview,
        releaseYear = releaseYear,
        posterPath = posterPath,
        backdropPath = backdropPath,
        voteAverage = voteAverage,
        appVoteAverage = appVoteAverage,
        category = category
    )

    private fun MovieEntity.toDomain() = Movie(
        id = id,
        tmdbId = tmdbId,
        imdbId = null,
        title = title,
        originalTitle = originalTitle,
        overview = overview,
        tagline = null,
        releaseDate = null,
        releaseYear = releaseYear,
        runtime = null,
        posterPath = posterPath,
        backdropPath = backdropPath,
        voteAverage = voteAverage ?: 0.0,
        appVoteAverage = appVoteAverage ?: 0.0,
        userRating = null,
        isFavorite = false,
        inWatchlist = false,
        isWatched = false,
        genres = emptyList(),
        cast = emptyList(),
        directors = emptyList()
    )
}
