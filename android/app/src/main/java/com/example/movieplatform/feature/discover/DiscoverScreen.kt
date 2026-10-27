package com.example.movieplatform.feature.discover

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen() {
    var minRating by remember { mutableFloatStateOf(7.5f) }
    var yearMin by remember { mutableFloatStateOf(2000f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gelişmiş Film Keşfi") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Minimum Puan: ${String.format("%.1f", minRating)}")
            Slider(
                value = minRating,
                onValueChange = { minRating = it },
                valueRange = 0f..10f
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Başlangıç Yılı: ${yearMin.toInt()}")
            Slider(
                value = yearMin,
                onValueChange = { yearMin = it },
                valueRange = 1950f..2026f
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Trigger discover API */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Filmleri Filtrele")
            }
        }
    }
}
