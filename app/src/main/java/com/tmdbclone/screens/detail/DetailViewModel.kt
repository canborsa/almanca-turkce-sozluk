package com.tmdbclone.screens.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tmdbclone.TMDBCloneApp
import com.tmdbclone.convex.ConvexClient
import com.tmdbclone.network.ApiClient
import com.tmdbclone.network.Movie
import dev.convex.convex.Value
import kotlinx.coroutines.launch

// ... (Review data class remains the same)
data class Review(
    val _id: String,
    val author: String,
    val comment: String,
    val rating: Double,
    val userId: String
)

data class DetailUiState(
    val movie: Movie? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isFavorite: Boolean = false,
    val onWatchlist: Boolean = false,
    val reviews: List<Review> = emptyList(),
    val userReview: Review? = null
)

class DetailViewModel : ViewModel() {

    var uiState by mutableStateOf(DetailUiState())
        private set

    private val convex = ConvexClient.client
    private val sessionManager = TMDBCloneApp.sessionManager
    private val currentUserId: String?
        get() = sessionManager.getUserId()

    fun fetchMovieDetails(movieId: Int) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val movieDetails = ApiClient.tmdbApi.getMovieDetails(movieId, ApiClient.API_KEY)
                fetchMovieReviews(movieId)

                if (currentUserId != null) {
                    checkFavoriteStatus(movieId)
                    checkWatchlistStatus(movieId)
                }

                uiState = uiState.copy(movie = movieDetails, isLoading = false)
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = "Failed to load details: ${e.message}")
            }
        }
    }

    private suspend fun fetchMovieReviews(movieId: Int) {
        val reviewValues = convex.query("reviews:getMovieReviews", mapOf("movieId" to Value.from(movieId.toDouble()))).get()
        val reviews = reviewValues.toList().mapNotNull { reviewValue ->
             val reviewMap = reviewValue.toMap()
             Review(
                _id = (reviewMap["_id"] as Value.String).string,
                author = (reviewMap["author"] as Value.String).string,
                comment = (reviewMap["comment"] as Value.String).string,
                rating = (reviewMap["rating"] as Value.Float64).number,
                userId = (reviewMap["userId"] as Value.String).string
            )
        }
        uiState = uiState.copy(
            reviews = reviews,
            userReview = reviews.find { it.userId == currentUserId }
        )
    }

    fun addOrUpdateReview(comment: String, rating: Int) {
        val movie = uiState.movie ?: return
        val userId = currentUserId ?: return
        viewModelScope.launch {
            val args = mapOf(
                "userId" to Value.fromString(userId),
                "movieId" to Value.from(movie.id.toDouble()),
                "comment" to Value.from(comment),
                "rating" to Value.from(rating.toDouble())
            )
            convex.mutation("reviews:addOrUpdateReview", args)
            fetchMovieReviews(movie.id)
        }
    }

    private suspend fun checkFavoriteStatus(movieId: Int) {
        val userId = currentUserId ?: return
        val result = convex.query("favorites:isFavorite", mapOf("userId" to Value.fromString(userId), "movieId" to Value.from(movieId.toDouble()))).get()
        uiState = uiState.copy(isFavorite = result.toBoolean())
    }

    private suspend fun checkWatchlistStatus(movieId: Int) {
        val userId = currentUserId ?: return
        val result = convex.query("watchlist:isOnWatchlist", mapOf("userId" to Value.fromString(userId), "movieId" to Value.from(movieId.toDouble()))).get()
        uiState = uiState.copy(onWatchlist = result.toBoolean())
    }

    fun toggleFavorite() {
        val movie = uiState.movie ?: return
        val userId = currentUserId ?: return
        viewModelScope.launch {
            val args = mapOf("userId" to Value.fromString(userId), "movieId" to Value.from(movie.id.toDouble()), "title" to Value.from(movie.title), "posterPath" to Value.from(movie.posterPath ?: ""))
            val mutation = if (uiState.isFavorite) "favorites:removeFavorite" else "favorites:addFavorite"
            convex.mutation(mutation, args)
            checkFavoriteStatus(movie.id)
        }
    }

    fun toggleWatchlist() {
        val movie = uiState.movie ?: return
        val userId = currentUserId ?: return
        viewModelScope.launch {
             val args = mapOf("userId" to Value.fromString(userId), "movieId" to Value.from(movie.id.toDouble()), "title" to Value.from(movie.title), "posterPath" to Value.from(movie.posterPath ?: ""))
             val mutation = if (uiState.onWatchlist) "watchlist:removeWatchlist" else "watchlist:addWatchlist"
             convex.mutation(mutation, args)
             checkWatchlistStatus(movie.id)
        }
    }
}
