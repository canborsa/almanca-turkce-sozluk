package com.example.movieplatform.data.remote.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T?,
    @SerializedName("message") val message: String?,
    @SerializedName("pagination") val pagination: ApiPagination?
)

data class ApiPagination(
    @SerializedName("page") val page: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("total_pages") val totalPages: Int
)

data class ApiMovie(
    @SerializedName("id") val id: Int,
    @SerializedName("tmdb_id") val tmdbId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("original_title") val originalTitle: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("tagline") val tagline: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("release_year") val releaseYear: Int?,
    @SerializedName("runtime") val runtime: Int?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("app_vote_average") val appVoteAverage: Float?,
    @SerializedName("app_vote_count") val appVoteCount: Int?,
    @SerializedName("tmdb_vote_average") val tmdbVoteAverage: Float?,
    @SerializedName("genres") val genres: List<ApiGenre>?,
    @SerializedName("cast") val cast: List<ApiCastPerson>?
)

data class ApiGenre(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

data class ApiCastPerson(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("character_name") val characterName: String?,
    @SerializedName("profile_path") val profilePath: String?
)

data class SearchResponse(
    @SerializedName("movies") val movies: List<ApiMovie>,
    @SerializedName("people") val people: List<ApiCastPerson>
)
