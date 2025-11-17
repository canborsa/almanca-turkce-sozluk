package com.tmdbclone.ui.screens.watchlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.tmdbclone.TMDBCloneApp
import com.tmdbclone.viewmodel.favorites.ConvexMovie
import com.tmdbclone.viewmodel.watchlist.WatchlistViewModel

@Composable
fun WatchlistScreen(
    navController: NavController
) {
    val app = LocalContext.current.applicationContext as TMDBCloneApp
    val viewModel: WatchlistViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return WatchlistViewModel(app.convex) as T
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.fetchWatchlist()
    }

    val uiState by viewModel.uiState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
                }
            }
            uiState.watchlistMovies.isEmpty() -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("No movies in your watchlist yet.")
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 128.dp),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.watchlistMovies) { movie ->
                        WatchlistMovieCard(movie = movie, onClick = {
                            navController.navigate("movie_detail/${movie.movieId}")
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun WatchlistMovieCard(movie: ConvexMovie, onClick: () -> Unit) {
    Card(modifier = Modifier.clickable(onClick = onClick)) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w500${movie.posterUrl}",
            contentDescription = movie.title,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
    }
}
