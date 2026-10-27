package com.example.movieplatform.domain.model

data class Movie(
    val id: Int,
    val tmdbId: Int,
    val title: String,
    val originalTitle: String?,
    val overview: String?,
    val releaseYear: Int?,
    val posterPath: String?,
    val backdropPath: String?,
    val appVoteAverage: Float?,
    val tmdbVoteAverage: Float?
)
