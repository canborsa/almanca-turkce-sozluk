package com.example.movieplatform.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MovieApiService {

    @GET("movies/list.php")
    suspend fun getMovies(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): ApiResponse<List<MovieDto>>

    @GET("movies/detail.php")
    suspend fun getMovieDetail(
        @Query("id") id: Int
    ): ApiResponse<MovieDto>

    @GET("movies/search.php")
    suspend fun searchMovies(
        @Query("q") query: String,
        @Query("page") page: Int = 1
    ): ApiResponse<List<MovieDto>>

    @POST("discovery/discover.php")
    suspend fun discoverMovies(
        @Body request: DiscoverRequest
    ): ApiResponse<List<MovieDto>>
}
