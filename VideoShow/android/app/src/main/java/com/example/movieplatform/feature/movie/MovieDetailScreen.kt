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

@Composable
fun MovieDetailScreen(
    movieId: Int,
    onBackClick: () -> Unit
) {
    var isFavorite by remember { mutableStateOf(true) }
    var inWatchlist by remember { mutableStateOf(true) }
    var isWatched by remember { mutableStateOf(true) }
    var userRating by remember { mutableStateOf(4.5f) }

    val backdropUrl = "https://image.tmdb.org/t/p/w780/xJHokMbljv3AA1S3i3z239339.jpg"
    val posterUrl = "https://image.tmdb.org/t/p/w342/gEU2QniE6E77NI6lCU6Mxl4vO2i.jpg"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        Box(modifier = Modifier.height(240.dp).fillMaxWidth()) {
            AsyncImage(
                model = backdropUrl,
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
                model = posterUrl,
                contentDescription = null,
                modifier = Modifier
                    .width(110.dp)
                    .height(160.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text("Yıldızlararası", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Interstellar (2014) • 169 dk", color = Color.Gray, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("TMDB: 8.4", color = Color.LightGray, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("⭐ Uygulama: 4.5", color = PrimaryGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { isFavorite = !isFavorite },
                        modifier = Modifier.background(DarkSurface, RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favori", tint = if (isFavorite) Color.Red else Color.Gray)
                    }

                    IconButton(
                        onClick = { inWatchlist = !inWatchlist },
                        modifier = Modifier.background(DarkSurface, RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Default.Bookmark, contentDescription = "Watchlist", tint = if (inWatchlist) PrimaryGold else Color.Gray)
                    }

                    IconButton(
                        onClick = { isWatched = !isWatched },
                        modifier = Modifier.background(DarkSurface, RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "İzledim", tint = if (isWatched) Color.Green else Color.Gray)
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text("Özet", color = PrimaryGold, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "İnsanlığın son günlerinde bir grup uzay kaşifi solucan deliğini kullanarak yeni bir yaşanabilir gezegen arar.",
                color = Color.White,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("Puan Ver", color = PrimaryGold, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                for (i in 1..5) {
                    IconButton(onClick = { userRating = i.toFloat() }) {
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
