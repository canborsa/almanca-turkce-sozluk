package com.tmdbclone.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tmdbclone.network.ApiClient
import com.tmdbclone.network.Movie

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = viewModel(),
    onMovieClick: (Int) -> Unit
) {
    val uiState = searchViewModel.uiState

    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar
        OutlinedTextField(
            value = uiState.query,
            onValueChange = { searchViewModel.onQueryChange(it) },
            label = { Text("Search Movies") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true
        )

        // Content
        when {
            uiState.isLoading -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            }
            uiState.errorMessage != null -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(text = uiState.errorMessage, color = MaterialTheme.colorScheme.error)
                }
            }
            uiState.isInitial -> {
                 Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(text = "Start typing to search for movies", fontStyle = FontStyle.Italic)
                }
            }
            uiState.searchResults.isEmpty() && !uiState.isLoading && uiState.query.isNotEmpty() -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(text = "No results found for '${uiState.query}'")
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.searchResults) { movie ->
                        SearchResultItem(movie = movie, onClick = { onMovieClick(movie.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(movie: Movie, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = "${ApiClient.IMAGE_BASE_URL}${movie.posterPath}",
                contentDescription = movie.title,
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = movie.title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = movie.releaseDate, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
