package com.example.movieplatform.data.local

import androidx.room.*

@Dao
interface MovieDao {
    @Query("SELECT * FROM cached_movies WHERE category = :category ORDER BY cachedAt DESC")
    suspend fun getMoviesByCategory(category: String): List<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Query("DELETE FROM cached_movies WHERE category = :category")
    suspend fun clearCategory(category: String)

    @Query("SELECT * FROM cached_movies WHERE id = :movieId LIMIT 1")
    suspend fun getMovieById(movieId: Int): MovieEntity?
}
