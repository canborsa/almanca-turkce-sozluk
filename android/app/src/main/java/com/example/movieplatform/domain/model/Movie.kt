package com.example.movieplatform.domain.model

data class Movie(
    val id: Int,
    val tmdbId: Int,
    val title: String,
    val originalTitle: String,
    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val releaseYear: Int?,
    val runtime: Int?
)
