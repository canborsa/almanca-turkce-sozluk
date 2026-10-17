package com.example.healthtracker.ui

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.example.healthtracker.data.database.HealthRecord
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfGenerator {

    fun generatePdf(context: Context, records: List<HealthRecord>): File? {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = android.graphics.Paint()
        paint.textSize = 12f
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        var yPosition = 20f
        for (record in records) {
            canvas.drawText("Tarih: ${sdf.format(Date(record.timestamp))}", 10f, yPosition, paint)
            yPosition += 20f
            canvas.drawText("Kan Şekeri: ${record.bloodSugar}", 10f, yPosition, paint)
            yPosition += 20f
            canvas.drawText("Tansiyon: ${record.systolicPressure}/${record.diastolicPressure}", 10f, yPosition, paint)
            yPosition += 40f
        }

        document.finishPage(page)

        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "health_records.pdf")
        try {
            document.writeTo(FileOutputStream(filePath))
        } catch (e: java.io.IOException) {
            e.printStackTrace()
            return null
        }
        document.close()
        return filePath
    }
}
