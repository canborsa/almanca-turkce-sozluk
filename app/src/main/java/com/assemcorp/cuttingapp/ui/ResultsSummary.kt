package com.assemcorp.cuttingapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.assemcorp.cuttingapp.logic.RollResult

@Composable
fun ResultsSummary(results: List<RollResult>) {
    val totalParts = results.sumOf { it.placedParts.size }
    val totalRolls = results.size

    Column {
        Text("Sonuç Özeti", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Toplam Parça: $totalParts")
        Text("Kullanılan Rulo: $totalRolls")
    }
}
