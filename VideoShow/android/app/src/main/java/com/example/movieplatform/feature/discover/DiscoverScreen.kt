package com.example.movieplatform.feature.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.movieplatform.core.ui.theme.DarkSurface
import com.example.movieplatform.core.ui.theme.PrimaryGold
import com.example.movieplatform.domain.model.Movie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    onMovieClick: (Int) -> Unit
) {
    var minRating by remember { mutableStateOf(7.5f) }
    var minVotes by remember { mutableStateOf(10000f) }
    var selectedGenre by remember { mutableStateOf("Bilim Kurgu") }
    var showFilterSheet by remember { mutableStateOf(false) }

    val movies = remember {
        listOf(
            Movie(1, 157336, "tt0816692", "Yıldızlararası", "Interstellar", "Uzay yolculuğu", "Geleceğimiz uzayda", "2014-11-05", 2014, 169, "/gEU2QniE6E77NI6lCU6Mxl4vO2i.jpg", "/xJHokMbljv3AA1S3i3z239339.jpg", 8.4, 4.5, 4.5, true, true, true, emptyList(), emptyList(), emptyList())
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gelişmiş Keşfet",
                color = PrimaryGold,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = { showFilterSheet = true },
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface)
            ) {
                Text("🎯 Filtreler", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Selected filters badge
        Text(
            text = "Aktif Filtreler: Tür ($selectedGenre), Min Puan ($minRating), Min Oy (${minVotes.toInt()})",
            color = Color.Gray,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(movies) { movie ->
                Column(
                    modifier = Modifier
                        .clickable { onMovieClick(movie.id) }
                        .background(DarkSurface, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w342${movie.posterPath}",
                        contentDescription = movie.title,
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(movie.title, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1)
                    Row {
                        Text("⭐ ${movie.voteAverage}", color = PrimaryGold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("${movie.releaseYear}", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            containerColor = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text("Gelişmiş Filtreleme", color = PrimaryGold, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Minimum Puan: ${String.format("%.1f", minRating)}", color = Color.White)
                Slider(
                    value = minRating,
                    onValueChange = { minRating = it },
                    valueRange = 0f..10f,
                    colors = SliderDefaults.colors(thumbColor = PrimaryGold, activeTrackColor = PrimaryGold)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Minimum Oy Sayısı: ${minVotes.toInt()}", color = Color.White)
                Slider(
                    value = minVotes,
                    onValueChange = { minVotes = it },
                    valueRange = 0f..50000f,
                    colors = SliderDefaults.colors(thumbColor = PrimaryGold, activeTrackColor = PrimaryGold)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { showFilterSheet = false },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold)
                ) {
                    Text("Sonuçları Göster", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
