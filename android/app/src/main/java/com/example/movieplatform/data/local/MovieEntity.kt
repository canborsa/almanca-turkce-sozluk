package com.example.movieplatform.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val tmdbId: Int,
    val title: String,
    val originalTitle: String,
    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val releaseYear: Int?
)
