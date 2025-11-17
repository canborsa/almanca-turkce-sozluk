package com.tmdbclone.screens.watchlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tmdbclone.auth.AuthViewModel
import com.tmdbclone.network.Movie
import com.tmdbclone.screens.search.SearchResultItem

@Composable
fun WatchlistScreen(
    watchlistViewModel: WatchlistViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    onMovieClick: (Int) -> Unit
) {
    val watchlistUiState = watchlistViewModel.uiState
    val authUiState = authViewModel.uiState

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when {
            authUiState.isAuthLoading || watchlistUiState.isLoading -> {
                CircularProgressIndicator()
            }
            !authUiState.isAuthenticated -> {
                Text("Sign in to see your watchlist.")
            }
            watchlistUiState.errorMessage != null -> {
                Text(text = watchlistUiState.errorMessage, color = MaterialTheme.colorScheme.error)
            }
            watchlistUiState.watchlistMovies.isEmpty() -> {
                Text("You haven't added any movies to your watchlist yet.")
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(watchlistUiState.watchlistMovies) { watchlistMovie ->
                        val movie = Movie(
                            id = watchlistMovie.movieId.toInt(),
                            title = watchlistMovie.title,
                            posterPath = watchlistMovie.posterPath,
                            overview = "",
                            releaseDate = "",
                            voteAverage = 0.0
                        )
                        SearchResultItem(movie = movie, onClick = { onMovieClick(movie.id) })
                    }
                }
            }
        }
    }
}