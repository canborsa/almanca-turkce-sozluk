package com.example.movieplatform.core.network

import com.example.movieplatform.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("auth/register.php")
    suspend fun register(@Body body: Map<String, String>): Response<ApiResponse<AuthResponseDto>>

    @POST("auth/login.php")
    suspend fun login(@Body body: Map<String, String>): Response<ApiResponse<AuthResponseDto>>

    @GET("auth/me.php")
    suspend fun getMe(): Response<ApiResponse<Map<String, Any>>>

    // Movies
    @GET("movies/list.php")
    suspend fun discoverMovies(
        @QueryMap filters: Map<String, String>
    ): Response<ApiResponse<List<MovieDto>>>

    @GET("movies/detail.php")
    suspend fun getMovieDetail(@Query("id") id: Int): Response<ApiResponse<MovieDto>>

    @GET("movies/trending.php")
    suspend fun getTrendingMovies(@Query("page") page: Int = 1): Response<ApiResponse<List<MovieDto>>>

    @GET("movies/popular.php")
    suspend fun getPopularMovies(@Query("page") page: Int = 1): Response<ApiResponse<List<MovieDto>>>

    @GET("movies/top_rated.php")
    suspend fun getTopRatedMovies(@Query("page") page: Int = 1): Response<ApiResponse<List<MovieDto>>>

    @GET("movies/search.php")
    suspend fun search(@Query("q") query: String): Response<ApiResponse<Map<String, Any>>>

    @GET("movies/recommendations.php")
    suspend fun getRecommendations(): Response<ApiResponse<List<MovieDto>>>

    @GET("movies/similar.php")
    suspend fun getSimilarMovies(@Query("id") movieId: Int): Response<ApiResponse<List<MovieDto>>>

    // People
    @GET("people/detail.php")
    suspend fun getPersonDetail(@Query("id") personId: Int): Response<ApiResponse<PersonDto>>

    // User Actions
    @POST("ratings/add.php")
    suspend fun addRating(@Body body: Map<String, Any>): Response<ApiResponse<Map<String, Any>>>

    @POST("reviews/create.php")
    suspend fun createReview(@Body body: Map<String, Any>): Response<ApiResponse<ReviewDto>>

    @GET("reviews/list.php")
    suspend fun getReviews(
        @Query("movie_id") movieId: Int,
        @Query("page") page: Int = 1,
        @Query("sort") sort: String = "newest"
    ): Response<ApiResponse<List<ReviewDto>>>

    @POST("favorites/add.php")
    suspend fun addFavorite(@Body body: Map<String, Int>): Response<ApiResponse<Any>>

    @POST("favorites/remove.php")
    suspend fun removeFavorite(@Body body: Map<String, Int>): Response<ApiResponse<Any>>

    @GET("favorites/list.php")
    suspend fun getFavorites(@Query("page") page: Int = 1): Response<ApiResponse<List<MovieDto>>>

    @POST("watchlist/add.php")
    suspend fun addWatchlist(@Body body: Map<String, Int>): Response<ApiResponse<Any>>

    @POST("watchlist/remove.php")
    suspend fun removeWatchlist(@Body body: Map<String, Int>): Response<ApiResponse<Any>>

    @GET("watchlist/list.php")
    suspend fun getWatchlist(@Query("page") page: Int = 1): Response<ApiResponse<List<MovieDto>>>

    @POST("watched/add.php")
    suspend fun addWatched(@Body body: Map<String, Any>): Response<ApiResponse<Any>>

    @POST("watched/remove.php")
    suspend fun removeWatched(@Body body: Map<String, Int>): Response<ApiResponse<Any>>

    @GET("watched/list.php")
    suspend fun getWatchedList(@Query("page") page: Int = 1): Response<ApiResponse<List<MovieDto>>>

    @GET("discovery/filters.php")
    suspend fun getDiscoveryFilters(): Response<ApiResponse<Map<String, Any>>>
}
