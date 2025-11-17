package com.tmdbclone.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tmdbclone.TMDBCloneApp
import com.tmdbclone.data.model.MovieDetail
import com.tmdbclone.viewmodel.detail.MovieDetailViewModel
import com.tmdbclone.viewmodel.detail.UserReview

@Composable
fun MovieDetailScreen(movieId: Int) {
    val app = LocalContext.current.applicationContext as TMDBCloneApp
    val viewModel: MovieDetailViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MovieDetailViewModel(movieId, app.convex) as T
            }
        }
    )
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
            uiState.movieDetail != null -> {
                LazyColumn {
                    item { MovieDetailHeader(uiState.movieDetail!!) }
                    item { ActionButtons(
                        isFavorite = uiState.isFavorite,
                        isInWatchlist = uiState.isInWatchlist,
                        onToggleFavorite = { viewModel.toggleFavorite() },
                        onToggleWatchlist = { viewModel.toggleWatchlist() }
                    ) }
                    item { MovieInfoSection(uiState.movieDetail!!) }
                    item {
                        ReviewsSection(
                            reviews = uiState.reviews,
                            onAddReview = { rating, comment ->
                                viewModel.addOrUpdateReview(rating, comment)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MovieDetailHeader(movie: MovieDetail) {
    Box(contentAlignment = Alignment.BottomStart) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w780${movie.backdropPath}",
            contentDescription = movie.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentScale = ContentScale.Crop
        )
        Text(
            text = movie.title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun ActionButtons(
    isFavorite: Boolean,
    isInWatchlist: Boolean,
    onToggleFavorite: () -> Unit,
    onToggleWatchlist: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IconButton(onClick = onToggleFavorite) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFavorite) Color.Red else Color.Gray
            )
        }
        IconButton(onClick = onToggleWatchlist) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = "Watchlist",
                tint = if (isInWatchlist) MaterialTheme.colorScheme.primary else Color.Gray
            )
        }
    }
}

@Composable
fun MovieInfoSection(movie: MovieDetail) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = movie.overview, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Rating: ${movie.voteAverage}/10")
        Text(text = "Release Date: ${movie.releaseDate}")
        Text(text = "Duration: ${movie.runtime} min")
        Text(text = "Genres: ${movie.genres.joinToString { it.name }}")
    }
}

@Composable
fun ReviewsSection(reviews: List<UserReview>, onAddReview: (Int, String) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Reviews", style = MaterialTheme.typography.titleLarge)
            Button(onClick = { showDialog = true }) {
                Text("Add Review")
            }
        }

        if (showDialog) {
            ReviewDialog(
                onDismiss = { showDialog = false },
                onSubmit = { rating, comment ->
                    onAddReview(rating, comment)
                    showDialog = false
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (reviews.isEmpty()) {
            Text("No reviews yet.")
        } else {
            reviews.forEach { review ->
                ReviewItem(review)
                Divider()
            }
        }
    }
}

@Composable
fun ReviewItem(review: UserReview) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = review.author, fontWeight = FontWeight.Bold)
        Text(text = "Rating: ${review.rating}/5")
        Text(text = review.comment)
    }
}

@OptIn(ExperimentalMaterial3.AlertDialogApi::class)
@Composable
fun ReviewDialog(onDismiss: () -> Unit, onSubmit: (Int, String) -> Unit) {
    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Your Review") },
        text = {
            Column {
                Text("Your Rating:")
                RatingBar(rating = rating, onRatingChange = { rating = it })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Your comment") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(rating, comment) }) {
                Text("Submit")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RatingBar(rating: Int, onRatingChange: (Int) -> Unit) {
    Row {
        (1..5).forEach { index ->
            Icon(
                imageVector = if (index <= rating) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier.clickable { onRatingChange(index) },
                tint = if (index <= rating) Color.Yellow else Color.Gray
            )
        }
    }
}
