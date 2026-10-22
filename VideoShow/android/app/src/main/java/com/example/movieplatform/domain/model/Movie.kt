package com.example.movieplatform.domain.model

data class Movie(
    val id: Int,
    val tmdbId: Int,
    val imdbId: String?,
    val title: String,
    val originalTitle: String?,
    val overview: String?,
    val tagline: String?,
    val releaseDate: String?,
    val releaseYear: Int?,
    val runtime: Int?,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val appVoteAverage: Double,
    val userRating: Double?,
    val isFavorite: Boolean,
    val inWatchlist: Boolean,
    val isWatched: Boolean,
    val genres: List<Genre>,
    val cast: List<CastPerson>,
    val directors: List<CrewPerson>
)

data class Genre(val id: Int, val name: String)
data class CastPerson(val id: Int, val name: String, val profilePath: String?, val characterName: String?)
data class CrewPerson(val id: Int, val name: String, val profilePath: String?, val job: String?)
