package com.example.movieplatform.data.model

import com.google.gson.annotations.SerializedName

data class MovieDto(
    @SerializedName("id") val id: Int,
    @SerializedName("tmdb_id") val tmdbId: Int,
    @SerializedName("imdb_id") val imdbId: String?,
    @SerializedName("title") val title: String,
    @SerializedName("original_title") val originalTitle: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("tagline") val tagline: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("release_year") val releaseYear: Int?,
    @SerializedName("runtime") val runtime: Int?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("original_language") val originalLanguage: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("vote_count") val voteCount: Int?,
    @SerializedName("popularity") val popularity: Double?,
    @SerializedName("app_vote_average") val appVoteAverage: Double?,
    @SerializedName("app_vote_count") val appVoteCount: Int?,
    @SerializedName("weighted_rating") val weightedRating: Double?,
    @SerializedName("user_rating") val userRating: Double?,
    @SerializedName("is_favorite") val isFavorite: Boolean?,
    @SerializedName("in_watchlist") val inWatchlist: Boolean?,
    @SerializedName("is_watched") val isWatched: Boolean?,
    @SerializedName("genres") val genres: List<GenreDto>?,
    @SerializedName("cast") val cast: List<CastDto>?,
    @SerializedName("directors") val directors: List<CrewDto>?
)

data class GenreDto(
    @SerializedName("id") val id: Int,
    @SerializedName("tmdb_id") val tmdbId: Int?,
    @SerializedName("name") val name: String
)

data class CastDto(
    @SerializedName("id") val id: Int,
    @SerializedName("tmdb_id") val tmdbId: Int?,
    @SerializedName("name") val name: String,
    @SerializedName("profile_path") val profilePath: String?,
    @SerializedName("character_name") val characterName: String?,
    @SerializedName("cast_order") val castOrder: Int?
)

data class CrewDto(
    @SerializedName("id") val id: Int,
    @SerializedName("tmdb_id") val tmdbId: Int?,
    @SerializedName("name") val name: String,
    @SerializedName("profile_path") val profilePath: String?,
    @SerializedName("department") val department: String?,
    @SerializedName("job") val job: String?
)

data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String?,
    @SerializedName("display_name") val displayName: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("bio") val bio: String?,
    @SerializedName("role") val role: String?
)

data class AuthResponseDto(
    @SerializedName("user") val user: UserDto,
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Long
)

data class ReviewDto(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("movie_id") val movieId: Int,
    @SerializedName("username") val username: String?,
    @SerializedName("display_name") val displayName: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("body") val body: String,
    @SerializedName("rating") val rating: Double?,
    @SerializedName("spoiler") val spoiler: Int?,
    @SerializedName("like_count") val likeCount: Int?,
    @SerializedName("created_at") val createdAt: String?
)

data class PersonDto(
    @SerializedName("id") val id: Int,
    @SerializedName("tmdb_id") val tmdbId: Int?,
    @SerializedName("name") val name: String,
    @SerializedName("biography") val biography: String?,
    @SerializedName("birthday") val birthday: String?,
    @SerializedName("place_of_birth") val placeOfBirth: String?,
    @SerializedName("profile_path") val profilePath: String?,
    @SerializedName("popularity") val popularity: Double?,
    @SerializedName("cast_movies") val castMovies: List<MovieDto>?,
    @SerializedName("crew_movies") val crewMovies: List<MovieDto>?
)
