package com.tmdbclone.screens.favorites

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
import com.tmdbclone.screens.search.SearchResultItem
import com.tmdbclone.network.Movie

@Composable
fun FavoritesScreen(
    favoritesViewModel: FavoritesViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    onMovieClick: (Int) -> Unit
) {
    val favoritesUiState = favoritesViewModel.uiState
    val authUiState = authViewModel.uiState

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when {
            authUiState.isAuthLoading || favoritesUiState.isLoading -> {
                CircularProgressIndicator()
            }
            !authUiState.isAuthenticated -> {
                Text("Sign in to see your favorite movies.")
            }
            favoritesUiState.errorMessage != null -> {
                Text(text = favoritesUiState.errorMessage, color = MaterialTheme.colorScheme.error)
            }
            favoritesUiState.favoriteMovies.isEmpty() -> {
                Text("You haven't added any movies to your favorites yet.")
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(favoritesUiState.favoriteMovies) { favorite ->
                        // We can reuse SearchResultItem by converting FavoriteMovie to a partial Movie object
                        val movie = Movie(
                            id = favorite.movieId.toInt(),
                            title = favorite.title,
                            posterPath = favorite.posterPath,
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