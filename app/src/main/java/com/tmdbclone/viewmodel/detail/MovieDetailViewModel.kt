package com.tmdbclone.viewmodel.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tmdbclone.data.model.MovieDetail
import com.tmdbclone.data.network.RetrofitInstance
import dev.convex.android.ConvexClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserReview(
    val author: String,
    val comment: String,
    val rating: Long
)

data class MovieDetailUiState(
    val movieDetail: MovieDetail? = null,
    val isFavorite: Boolean = false,
    val isInWatchlist: Boolean = false,
    val reviews: List<UserReview> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class MovieDetailViewModel(
    private val movieId: Int,
    private val convex: ConvexClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailUiState())
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    init {
        fetchMovieData()
    }

    private fun fetchMovieData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Fetch details, favorite status, and watchlist status in parallel
                val detailJob = launch { fetchMovieDetails() }
                val favStatusJob = launch { fetchFavoriteStatus() }
                val watchlistStatusJob = launch { fetchWatchlistStatus() }
                val reviewsJob = launch { fetchReviews() }

                detailJob.join()
                favStatusJob.join()
                watchlistStatusJob.join()
                reviewsJob.join()

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun fetchMovieDetails() {
        val detail = RetrofitInstance.api.getMovieDetails(movieId)
        _uiState.update { it.copy(movieDetail = detail) }
    }

    private suspend fun fetchFavoriteStatus() {
        val isFav = convex.query("favorites:isFavorite", mapOf("movieId" to movieId)) as Boolean
        _uiState.update { it.copy(isFavorite = isFav) }
    }

    private suspend fun fetchWatchlistStatus() {
        val inWatchlist = convex.query("watchlist:isInWatchlist", mapOf("movieId" to movieId)) as Boolean
        _uiState.update { it.copy(isInWatchlist = inWatchlist) }
    }

    private suspend fun fetchReviews() {
        val result = convex.query("reviews:getMovieReviews", mapOf("movieId" to movieId))
        @Suppress("UNCHECKED_CAST")
        val reviews = (result as? List<Map<String, Any>>)?.map { map ->
            UserReview(
                author = map["author"] as String,
                comment = map["comment"] as String,
                rating = map["rating"] as Long
            )
        } ?: emptyList()
        _uiState.update { it.copy(reviews = reviews) }
    }

    fun addOrUpdateReview(rating: Int, comment: String) {
        viewModelScope.launch {
            try {
                convex.mutation(
                    "reviews:addOrUpdateReview",
                    mapOf("movieId" to movieId, "rating" to rating, "comment" to comment)
                )
                fetchReviews() // Refresh reviews after adding/updating
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to submit review: ${e.message}") }
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val currentState = _uiState.value.isFavorite
            _uiState.update { it.copy(isFavorite = !currentState) } // Optimistic update
            try {
                val params = mapOf("movieId" to movieId, "title" to _uiState.value.movieDetail!!.title, "posterUrl" to _uiState.value.movieDetail!!.posterPath!!)
                if (!currentState) {
                    convex.mutation("favorites:addFavorite", params)
                } else {
                    convex.mutation("favorites:removeFavorite", mapOf("movieId" to movieId))
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isFavorite = currentState, error = e.message) } // Revert on error
            }
        }
    }

    fun toggleWatchlist() {
        viewModelScope.launch {
            val currentState = _uiState.value.isInWatchlist
            _uiState.update { it.copy(isInWatchlist = !currentState) } // Optimistic update
            try {
                val params = mapOf("movieId" to movieId, "title" to _uiState.value.movieDetail!!.title, "posterUrl" to _uiState.value.movieDetail!!.posterPath!!)
                if (!currentState) {
                    convex.mutation("watchlist:addToWatchlist", params)
                } else {
                    convex.mutation("watchlist:removeFromWatchlist", mapOf("movieId" to movieId))
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isInWatchlist = currentState, error = e.message) } // Revert on error
            }
        }
    }
}
