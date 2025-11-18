package com.assemcorp.cuttingapp.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.assemcorp.cuttingapp.logic.RollResult

val partColors = listOf(
    Color(0xFFE6194B), Color(0xFF3CB44B), Color(0xFFFFE119), Color(0xFF4363D8),
    Color(0xFFF58231), Color(0xFF911EB4), Color(0xFF42D4F4), Color(0xFFF032E6),
    Color(0xFFBFEF45), Color(0xFFFABED4)
)

@Composable
fun RollDrawing(rollResult: RollResult, rollWidth: Double, rollHeight: Double) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio((rollWidth / rollHeight).toFloat())
    ) {
        val scaleX = size.width / rollWidth
        val scaleY = size.height / rollHeight

        // Draw the roll outline
        drawRect(
            color = Color.Black,
            style = Stroke(width = 2f),
            topLeft = Offset.Zero,
            size = size
        )

        // Draw the placed parts
        rollResult.placedParts.forEach { placedPart ->
            val color = partColors[placedPart.originalPart.id % partColors.size]
            drawRect(
                color = color,
                topLeft = Offset(
                    (placedPart.rect.x * scaleX).toFloat(),
                    (placedPart.rect.y * scaleY).toFloat()
                ),
                size = Size(
                    (placedPart.rect.width * scaleX).toFloat(),
                    (placedPart.rect.height * scaleY).toFloat()
                )
            )
            // Draw a border around the part
            drawRect(
                color = Color.Black,
                style = Stroke(width = 1f),
                 topLeft = Offset(
                    (placedPart.rect.x * scaleX).toFloat(),
                    (placedPart.rect.y * scaleY).toFloat()
                ),
                size = Size(
                    (placedPart.rect.width * scaleX).toFloat(),
                    (placedPart.rect.height * scaleY).toFloat()
                )
            )
        }
    }
}
