package com.example.movieplatform.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val tmdbId: Int,
    val title: String,
    val originalTitle: String?,
    val overview: String?,
    val releaseYear: Int?,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double?,
    val appVoteAverage: Double?,
    val category: String, // "trending", "popular", "top_rated", "favorite", "watchlist", "watched"
    val cachedAt: Long = System.currentTimeMillis()
)
