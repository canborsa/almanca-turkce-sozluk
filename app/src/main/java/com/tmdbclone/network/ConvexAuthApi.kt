package com.tmdbclone.network

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ConvexAuthApi {
    @POST("handleSignUp")
    suspend fun signUp(@Body body: JsonObject): Response<JsonObject>

    @POST("handleLogIn")
    suspend fun logIn(@Body body: JsonObject): Response<JsonObject>
}
