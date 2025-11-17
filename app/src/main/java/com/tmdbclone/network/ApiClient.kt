package com.tmdbclone.network

import com.tmdbclone.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "https://api.themoviedb.org/3/"
    // API Key is now accessed securely from BuildConfig
    val API_KEY: String = BuildConfig.TMDB_API_KEY
    const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"


    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val tmdbApi: TmdbApi = retrofit.create(TmdbApi::class.java)
}
