package com.example.movieplatform.feature.movie

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(movieId: Int) {
    var showSpoiler by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Film Detayı") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Yıldızlararası (2014)",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Uygulama Puanı: ⭐ 4.5 / 5.0 (12.481 oy)")
            Spacer(modifier = Modifier.height(16.dp))

            Text("Yorumlar:", style = MaterialTheme.typography.titleMedium)
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Kullanıcı: Ahmet Kaya")
                    if (showSpoiler) {
                        Text("SPOILER İÇERİK: Filmin sonunda Cooper zaman ve uzayın ötesine geçer...")
                    } else {
                        Button(onClick = { showSpoiler = true }) {
                            Text("Spoileri Göster")
                        }
                    }
                }
            }
        }
    }
}
