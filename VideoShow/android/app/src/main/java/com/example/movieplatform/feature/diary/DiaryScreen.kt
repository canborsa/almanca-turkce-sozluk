package com.example.movieplatform.feature.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movieplatform.core.ui.theme.DarkBackground
import com.example.movieplatform.core.ui.theme.PrimaryGold

@Composable
fun DiaryScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        Text("Film Günlüğüm", color = PrimaryGold, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        Text("2026 İstatistikleri", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("İzlenen Film: 18 • Ortalama Puan: 4.1 ⭐ • Süre: 32 saat", color = Color.Gray, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(16.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Interstellar", color = Color.White, fontWeight = FontWeight.Bold)
                Text("İzleme Tarihi: 22 Ekim 2026 • Puan: 4.5 ⭐", color = PrimaryGold, fontSize = 12.sp)
                Text("Not: Sinema tarihinin en iyi bilim kurgularından.", color = Color.LightGray, fontSize = 13.sp)
            }
        }
    }
}
