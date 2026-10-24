package com.example.movieplatform.feature.person

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
fun PersonDetailScreen(personId: Int, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        Button(onClick = onBackClick) { Text("← Geri") }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Christopher Nolan", color = PrimaryGold, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Yönetmen, Senarist, Yapımcı", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Biyografi:", color = Color.White, fontWeight = FontWeight.Bold)
        Text("İngiliz film yönetmeni, senarist ve yapımcı.", color = Color.LightGray, fontSize = 14.sp)
    }
}
