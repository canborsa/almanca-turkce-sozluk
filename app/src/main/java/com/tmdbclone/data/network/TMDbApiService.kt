package com.tmdbclone.data.network

import com.tmdbclone.data.model.MovieDetail
import com.tmdbclone.data.model.MovieListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDbApiService {

    @GET("trending/movie/week")
    suspend fun getTrendingMovies(): MovieListResponse

    @GET("movie/popular")
    suspend fun getPopularMovies(): MovieListResponse

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(): MovieListResponse

    @GET("search/movie")
    suspend fun searchMovies(@Query("query") query: String): MovieListResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(@Path("movie_id") movieId: Int): MovieDetail
}
