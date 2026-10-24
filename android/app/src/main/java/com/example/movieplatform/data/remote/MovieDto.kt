package com.example.movieplatform.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    @SerialName("error_code") val errorCode: String? = null,
    val pagination: PaginationData? = null
)

@Serializable
data class PaginationData(
    val page: Int,
    @SerialName("per_page") val perPage: Int,
    val total: Int,
    @SerialName("total_pages") val totalPages: Int
)

@Serializable
data class MovieDto(
    val id: Int,
    @SerialName("tmdb_id") val tmdbId: Int,
    @SerialName("imdb_id") val imdbId: String? = null,
    val title: String,
    @SerialName("original_title") val originalTitle: String,
    val overview: String? = null,
    val tagline: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("release_year") val releaseYear: Int? = null,
    val runtime: Int? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("vote_count") val voteCount: Int = 0,
    val popularity: Double = 0.0,
    @SerialName("user_rating_average") val userRatingAverage: Double = 0.0,
    @SerialName("user_rating_count") val userRatingCount: Int = 0,
    @SerialName("weighted_rating") val weightedRating: Double = 0.0
)

@Serializable
data class DiscoverRequest(
    val genres: List<Int>? = null,
    @SerialName("year_start") val yearStart: Int? = null,
    @SerialName("year_end") val yearEnd: Int? = null,
    @SerialName("min_rating") val minRating: Double? = null,
    @SerialName("min_votes") val minVotes: Int? = null,
    @SerialName("min_runtime") val minRuntime: Int? = null,
    @SerialName("max_runtime") val maxRuntime: Int? = null,
    @SerialName("sort_by") val sortBy: String? = null,
    val page: Int = 1,
    @SerialName("per_page") val perPage: Int = 20
)
