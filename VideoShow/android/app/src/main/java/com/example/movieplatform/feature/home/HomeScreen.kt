package com.example.movieplatform.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.movieplatform.core.ui.theme.PrimaryGold
import com.example.movieplatform.domain.model.Movie

@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit
) {
    // Sample movies for UI presentation
    val trendingMovies = remember {
        listOf(
            Movie(1, 157336, "tt0816692", "Yıldızlararası", "Interstellar", "Uzay yolculuğu", "Geleceğimiz uzayda", "2014-11-05", 2014, 169, "/gEU2QniE6E77NI6lCU6Mxl4vO2i.jpg", "/xJHokMbljv3AA1S3i3z239339.jpg", 8.4, 4.5, 4.5, true, true, true, emptyList(), emptyList(), emptyList()),
            Movie(2, 27205, "tt1375666", "Başlangıç", "Inception", "Rüya içinde rüya", "Zihniniz suç mahallinizdir", "2010-07-16", 2010, 148, "/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg", "/s3T19R22fA28cuA8298492.jpg", 8.3, 4.4, null, false, false, false, emptyList(), emptyList(), emptyList())
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(bottom = 80.dp)
    ) {
        item {
            Text(
                text = "VideoShow",
                color = PrimaryGold,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }

        item { MovieSection("🔥 Şu Anda Popüler", trendingMovies, onMovieClick) }
        item { MovieSection("⭐ En Yüksek Puanlı", trendingMovies, onMovieClick) }
        item { MovieSection("👥 Kullanıcılarımızın Favorileri", trendingMovies, onMovieClick) }
        item { MovieSection("🎯 Sana Özel", trendingMovies, onMovieClick) }
    }
}

@Composable
fun MovieSection(
    title: String,
    movies: List<Movie>,
    onMovieClick: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(movies) { movie ->
                MovieCard(movie = movie, onClick = { onMovieClick(movie.id) })
            }
        }
    }
}

@Composable
fun MovieCard(movie: Movie, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(130.dp)
            .clickable { onClick() }
    ) {
        val imageUrl = "https://image.tmdb.org/t/p/w342${movie.posterPath}"
        AsyncImage(
            model = imageUrl,
            contentDescription = movie.title,
            modifier = Modifier
                .height(180.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.DarkGray),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = movie.title,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "⭐ ${movie.voteAverage}", color = PrimaryGold, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "${movie.releaseYear ?: ""}", color = Color.Gray, fontSize = 12.sp)
        }
    }
}
