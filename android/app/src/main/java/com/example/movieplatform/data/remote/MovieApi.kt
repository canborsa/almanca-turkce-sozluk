package com.example.movieplatform.data.remote

import com.example.movieplatform.data.remote.model.ApiMovie
import com.example.movieplatform.data.remote.model.ApiResponse
import com.example.movieplatform.data.remote.model.SearchResponse
import retrofit2.http.*

interface MovieApi {
    @GET("movies/list.php")
    suspend fun getMovies(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): ApiResponse<List<ApiMovie>>

    @GET("movies/detail.php")
    suspend fun getMovieDetail(
        @Query("id") id: Int
    ): ApiResponse<ApiMovie>

    @GET("movies/search.php")
    suspend fun search(
        @Query("q") query: String
    ): ApiResponse<SearchResponse>

    @GET("discovery/discover.php")
    suspend fun discover(
        @Query("genres") genres: String? = null,
        @Query("year_min") yearMin: Int? = null,
        @Query("year_max") yearMax: Int? = null,
        @Query("rating_min") ratingMin: Float? = null,
        @Query("votes_min") votesMin: Int? = null,
        @Query("duration_min") durationMin: Int? = null,
        @Query("duration_max") durationMax: Int? = null,
        @Query("sort_by") sortBy: String? = "popularity",
        @Query("page") page: Int = 1
    ): ApiResponse<List<ApiMovie>>
}
