package com.example.movieplatform.feature.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movieplatform.core.ui.theme.DarkBackground
import com.example.movieplatform.core.ui.theme.PrimaryGold

@Composable
fun LibraryScreen(onMovieClick: (Int) -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Favoriler", "Watchlist", "İzlediklerim", "Listeler")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        Text("Kütüphanem", color = PrimaryGold, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        TabRow(selectedTabIndex = selectedTab, containerColor = DarkBackground, contentColor = PrimaryGold) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, color = if (selectedTab == index) PrimaryGold else Color.Gray) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Yıldızlararası (2014) - ⭐ 4.5", color = Color.White, modifier = Modifier.padding(vertical = 8.dp))
    }
}
