package com.assemcorp.cuttingapp.logic

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.compose.ui.graphics.toArgb
import com.assemcorp.cuttingapp.data.Material
import com.assemcorp.cuttingapp.ui.partColors
import java.io.File
import java.io.FileOutputStream

fun generatePdf(
    context: Context,
    results: List<RollResult>,
    customerName: String,
    material: Material,
    customWidth: Double?,
    customHeight: Double?
): File {
    val document = PdfDocument()
    val pageHeight = 842
    val pageWidth = 595
    var pageNumber = 1
    var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
    var page = document.startPage(pageInfo)
    var canvas = page.canvas
    val paint = Paint()

    var yPosition = 20f

    fun checkPageBounds(newHeight: Float) {
        if (yPosition + newHeight > pageHeight) {
            document.finishPage(page)
            pageNumber++
            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = document.startPage(pageInfo)
            canvas = page.canvas
            yPosition = 20f
        }
    }

    paint.textSize = 18f
    paint.isFakeBoldText = true
    canvas.drawText("Assemcorp Kesim Planı", 10f, yPosition, paint)
    yPosition += 40f

    paint.textSize = 12f
    paint.isFakeBoldText = false
    canvas.drawText("Müşteri: $customerName", 10f, yPosition, paint)
    yPosition += 20f
    canvas.drawText("Malzeme: ${material.name}", 10f, yPosition, paint)
    yPosition += 40f

    val totalParts = results.sumOf { it.placedParts.size }
    val totalRolls = results.size
    canvas.drawText("Toplam Parça: $totalParts", 10f, yPosition, paint)
    yPosition += 20f
    canvas.drawText("Kullanılan Rulo: $totalRolls", 10f, yPosition, paint)
    yPosition += 40f

    results.forEach { rollResult ->
        checkPageBounds(40f)
        canvas.drawText("Rulo #${rollResult.rollNumber}", 10f, yPosition, paint)
        yPosition += 20f

        val rollWidth = if (material.isCustom) customWidth ?: 0.0 else material.width
        val rollHeight = if (material.isCustom) customHeight ?: 0.0 else material.height

        val scale = 0.5f
        val rollRectWidth = (rollWidth * scale).toFloat()
        val rollRectHeight = (rollHeight * scale).toFloat()

        checkPageBounds(rollRectHeight + 20f)

        // Draw roll outline
        paint.style = Paint.Style.STROKE
        paint.color = Color.BLACK
        canvas.drawRect(10f, yPosition, 10f + rollRectWidth, yPosition + rollRectHeight, paint)
        paint.style = Paint.Style.FILL

        rollResult.placedParts.forEach { placedPart ->
            paint.color = partColors[placedPart.originalPart.id % partColors.size].toArgb()
            val partRect = android.graphics.RectF(
                (10 + placedPart.rect.x * scale).toFloat(),
                (yPosition + placedPart.rect.y * scale).toFloat(),
                (10 + (placedPart.rect.x + placedPart.rect.width) * scale).toFloat(),
                (yPosition + (placedPart.rect.y + placedPart.rect.height) * scale).toFloat()
            )
            canvas.drawRect(partRect, paint)
        }
        yPosition += rollRectHeight + 20f
    }


    document.finishPage(page)
    val file = File(context.getExternalFilesDir(null), "assemcorp_kesim_plani.pdf")
    document.writeTo(FileOutputStream(file))
    document.close()
    return file
}
