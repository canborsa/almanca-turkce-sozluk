package com.tmdbclone.screens.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tmdbclone.auth.AuthViewModel
import com.tmdbclone.network.ApiClient
import com.tmdbclone.network.Movie

@Composable
fun DetailScreen(
    movieId: Int,
    detailViewModel: DetailViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val detailUiState = detailViewModel.uiState
    val authUiState = authViewModel.uiState

    LaunchedEffect(movieId, authUiState.isAuthenticated) {
        detailViewModel.fetchMovieDetails(movieId)
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        if (detailUiState.isLoading) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        } else if (detailUiState.errorMessage != null) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(text = detailUiState.errorMessage, color = MaterialTheme.colorScheme.error)
            }
        } else if (detailUiState.movie != null) {
            MovieDetailContent(
                movie = detailUiState.movie,
                detailUiState = detailUiState,
                isAuthenticated = authUiState.isAuthenticated,
                onToggleFavorite = { detailViewModel.toggleFavorite() },
                onToggleWatchlist = { detailViewModel.toggleWatchlist() },
                onSubmitReview = { comment, rating -> detailViewModel.addOrUpdateReview(comment, rating) }
            )
        }
    }
}

@Composable
fun MovieDetailContent(
    movie: Movie,
    detailUiState: DetailUiState,
    isAuthenticated: Boolean,
    onToggleFavorite: () -> Unit,
    onToggleWatchlist: () -> Unit,
    onSubmitReview: (String, Int) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
        item { BackdropAndHeader(movie, detailUiState, isAuthenticated, onToggleFavorite, onToggleWatchlist) }
        item { Overview(movie.overview) }
        item { ReviewSection(detailUiState, isAuthenticated, onSubmitReview) }
    }
}

@Composable
private fun BackdropAndHeader(
    movie: Movie,
    detailUiState: DetailUiState,
    isAuthenticated: Boolean,
    onToggleFavorite: () -> Unit,
    onToggleWatchlist: () -> Unit
) {
    Column {
        AsyncImage(model = "${ApiClient.IMAGE_BASE_URL}${movie.backdropPath}", contentDescription = null, modifier = Modifier.fillMaxWidth().height(250.dp), contentScale = ContentScale.Crop)
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            AsyncImage(model = "${ApiClient.IMAGE_BASE_URL}${movie.posterPath}", contentDescription = null, modifier = Modifier.width(120.dp).height(180.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = movie.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Release Date: ${movie.releaseDate}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Rating: ${String.format("%.1f", movie.voteAverage)}/10")
            }
        }
        if (isAuthenticated) {
            Row(modifier = Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                IconButton(onClick = onToggleFavorite) { Icon(if (detailUiState.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, "Toggle Favorite", tint = if (detailUiState.isFavorite) Color.Red else Color.Gray) }
                IconButton(onClick = onToggleWatchlist) { Icon(if (detailUiState.onWatchlist) Icons.Filled.List else Icons.Outlined.List, "Toggle Watchlist", tint = if (detailUiState.onWatchlist) MaterialTheme.colorScheme.primary else Color.Gray) }
            }
        } else {
            // Sign in button is removed for now. Will be replaced by navigation to a Login screen.
            Text("Sign in to save movies and write reviews.", modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

// ... (Overview and ReviewSection composables remain the same)
@Composable
private fun Overview(overview: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Overview", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = overview)
    }
}

@Composable
private fun ReviewSection(
    detailUiState: DetailUiState,
    isAuthenticated: Boolean,
    onSubmitReview: (String, Int) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        Text(text = "Reviews", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        if (isAuthenticated) {
            ReviewForm(userReview = detailUiState.userReview, onSubmit = onSubmitReview)
        }

        if (detailUiState.reviews.isEmpty()) {
            Text("No reviews yet.")
        } else {
            detailUiState.reviews.forEach { review ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(review.author, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                           (1..5).forEach { index ->
                                Icon(Icons.Filled.Star, contentDescription = null, tint = if(index <= review.rating) Color.Yellow else Color.Gray)
                           }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(review.comment)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReviewForm(
    userReview: Review?,
    onSubmit: (String, Int) -> Unit
) {
    var currentComment by remember(userReview) { mutableStateOf(userReview?.comment ?: "") }
    var currentRating by remember(userReview) { mutableStateOf((userReview?.rating ?: 0.0).toInt()) }

    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(text = if (userReview != null) "Edit Your Review" else "Add Your Review", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = currentComment,
            onValueChange = { currentComment = it },
            label = { Text("Comment") },
            modifier = Modifier.fillMaxWidth().height(120.dp)
        )
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            (1..5).forEach { rating ->
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Rating $rating",
                    modifier = Modifier.clickable { currentRating = rating }.size(32.dp),
                    tint = if (rating <= currentRating) Color.Yellow else Color.Gray
                )
            }
        }
        Button(onClick = { onSubmit(currentComment, currentRating) }) {
            Text("Submit Review")
        }
    }
}
