package com.tmdbclone.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tmdbclone.network.ApiClient
import com.tmdbclone.network.Movie

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    onMovieClick: (Int) -> Unit
) {
    val uiState = homeViewModel.uiState

    Surface(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        } else if (uiState.errorMessage != null) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(text = uiState.errorMessage, color = MaterialTheme.colorScheme.error)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item { MovieSection(title = "Trending", movies = uiState.trendingMovies, onMovieClick = onMovieClick) }
                item { MovieSection(title = "Popular", movies = uiState.popularMovies, onMovieClick = onMovieClick) }
                item { MovieSection(title = "Upcoming", movies = uiState.upcomingMovies, onMovieClick = onMovieClick) }
            }
        }
    }
}

@Composable
fun MovieSection(title: String, movies: List<Movie>, onMovieClick: (Int) -> Unit) {
    if (movies.isEmpty()) return // Don't show section if there are no movies

    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(movies) { movie ->
                MovieItem(movie = movie, onClick = { onMovieClick(movie.id) })
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = "${ApiClient.IMAGE_BASE_URL}${movie.posterPath}",
                contentDescription = movie.title,
                modifier = Modifier
                    .height(225.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Text(
                text = movie.title,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(8.dp),
                maxLines = 2
            )
        }
    }
}
