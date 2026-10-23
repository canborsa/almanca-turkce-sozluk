package com.example.movieplatform.feature.movie

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.movieplatform.core.ui.theme.DarkBackground
import com.example.movieplatform.core.ui.theme.DarkSurface
import com.example.movieplatform.core.ui.theme.PrimaryGold
import com.example.movieplatform.domain.model.Movie

@Composable
fun MovieDetailScreen(
    movieId: Int,
    viewModel: MovieDetailViewModel? = null,
    onBackClick: () -> Unit
) {
    LaunchedEffect(movieId) {
        viewModel?.loadMovie(movieId)
    }

    val state = viewModel?.uiState?.collectAsState()?.value ?: MovieDetailUiState.Success(
        Movie(movieId, 157336, "tt0816692", "Yıldızlararası", "Interstellar", "Uzay yolculuğu hikayesi", "Geleceğimiz uzayda", "2014-11-05", 2014, 169, "/gEU2QniE6E77NI6lCU6Mxl4vO2i.jpg", "/xJHokMbljv3AA1S3i3z239339.jpg", 8.4, 4.5, 4.5, true, true, true, emptyList(), emptyList(), emptyList())
    )

    when (state) {
        is MovieDetailUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize().background(DarkBackground), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryGold)
            }
        }
        is MovieDetailUiState.Error -> {
            Column(modifier = Modifier.fillMaxSize().background(DarkBackground).padding(16.dp)) {
                Button(onClick = onBackClick) { Text("← Geri") }
                Text(state.message, color = Color.Red, fontSize = 16.sp, modifier = Modifier.padding(top = 16.dp))
            }
        }
        is MovieDetailUiState.Success -> {
            val movie = state.movie
            var isFav by remember { mutableStateOf(movie.isFavorite) }
            var userRating by remember { mutableStateOf((movie.userRating ?: 4.5).toFloat()) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBackground)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 80.dp)
            ) {
                Box(modifier = Modifier.height(240.dp).fillMaxWidth()) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w780${movie.backdropPath}",
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Button(
                        onClick = onBackClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.6f)),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("← Geri", color = Color.White)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w342${movie.posterPath}",
                        contentDescription = null,
                        modifier = Modifier
                            .width(110.dp)
                            .height(160.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(movie.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("${movie.originalTitle ?: ""} (${movie.releaseYear ?: ""}) • ${movie.runtime ?: 0} dk", color = Color.Gray, fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("TMDB: ${movie.voteAverage}", color = Color.LightGray, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("⭐ Uygulama: ${movie.appVoteAverage}", color = PrimaryGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = {
                                    isFav = !isFav
                                    viewModel?.toggleFavorite(movie.id, !isFav)
                                },
                                modifier = Modifier.background(DarkSurface, RoundedCornerShape(8.dp))
                            ) {
                                Icon(Icons.Default.Favorite, contentDescription = "Favori", tint = if (isFav) Color.Red else Color.Gray)
                            }

                            IconButton(
                                onClick = { },
                                modifier = Modifier.background(DarkSurface, RoundedCornerShape(8.dp))
                            ) {
                                Icon(Icons.Default.Bookmark, contentDescription = "Watchlist", tint = PrimaryGold)
                            }

                            IconButton(
                                onClick = { },
                                modifier = Modifier.background(DarkSurface, RoundedCornerShape(8.dp))
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "İzledim", tint = Color.Green)
                            }
                        }
                    }
                }

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("Özet", color = PrimaryGold, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        movie.overview ?: "Özet bulunmuyor.",
                        color = Color.White,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("Puan Ver", color = PrimaryGold, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                        for (i in 1..5) {
                            IconButton(onClick = {
                                userRating = i.toFloat()
                                viewModel?.addRating(movie.id, i.toDouble())
                            }) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = "$i Yıldız",
                                    tint = if (i <= userRating) PrimaryGold else Color.DarkGray
                                )
                            }
                        }
                        Text("($userRating / 5.0)", color = Color.White, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }
    }
}
