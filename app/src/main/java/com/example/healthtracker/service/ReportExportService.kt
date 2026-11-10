package com.example.healthtracker.service

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.healthtracker.data.entity.MealEntry
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object ReportExportService {

    /**
     * Generates a clean Daily Food Diary CSV file for a given date.
     */
    fun generateDailyFoodDiaryCSV(context: Context, date: String, meals: List<MealEntry>): File {
        val fileName = "VitaLog_FoodDiary_$date.csv"
        val file = File(context.cacheDir, fileName)
        FileWriter(file).use { writer ->
            writer.append("Meal,Food,Portion,Calories (kcal),Protein (g),Carbs (g),Fat (g),Sugar (g)\n")
            for (meal in meals) {
                writer.append("\"${meal.mealType}\",")
                writer.append("\"${meal.foodName}\",")
                writer.append("\"${meal.portionMultiplier} ${meal.portionUnit}\",")
                writer.append("${meal.calories},")
                writer.append("${meal.protein},")
                writer.append("${meal.carbs},")
                writer.append("${meal.fat},")
                writer.append("${meal.sugar}\n")
            }
            // Totals
            val totalCal = meals.sumOf { it.calories.toDouble() }
            val totalProt = meals.sumOf { it.protein.toDouble() }
            val totalCarb = meals.sumOf { it.carbs.toDouble() }
            val totalFat = meals.sumOf { it.fat.toDouble() }
            val totalSugar = meals.sumOf { it.sugar.toDouble() }
            writer.append("TOTALS,,,\"${String.format(Locale.US, "%.1f", totalCal)}\",\"${String.format(Locale.US, "%.1f", totalProt)}\",\"${String.format(Locale.US, "%.1f", totalCarb)}\",\"${String.format(Locale.US, "%.1f", totalFat)}\",\"${String.format(Locale.US, "%.1f", totalSugar)}\"\n")
        }
        return file
    }

    /**
     * Generates a professionally formatted Daily Food Diary PDF document.
     */
    fun generateDailyFoodDiaryPDF(context: Context, date: String, meals: List<MealEntry>): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Header Background
        paint.color = Color.parseColor("#6658E8")
        canvas.drawRect(0f, 0f, 595f, 90f, paint)

        // Title
        paint.color = Color.WHITE
        paint.textSize = 22f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("VitaLog — DAILY FOOD DIARY", 30f, 45f, paint)

        // Subtitle / Date
        paint.textSize = 12f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Date: $date   |   Generated with VitaLog", 30f, 70f, paint)

        // Table Header
        var yPos = 130f
        paint.color = Color.parseColor("#F0F2F6")
        canvas.drawRect(30f, yPos - 20f, 565f, yPos + 10f, paint)

        paint.color = Color.parseColor("#1C1E23")
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        canvas.drawText("Meal", 35f, yPos, paint)
        canvas.drawText("Food Item", 115f, yPos, paint)
        canvas.drawText("Portion", 270f, yPos, paint)
        canvas.drawText("Calories", 345f, yPos, paint)
        canvas.drawText("Protein", 415f, yPos, paint)
        canvas.drawText("Carbs", 470f, yPos, paint)
        canvas.drawText("Fat", 525f, yPos, paint)

        yPos += 25f
        paint.typeface = Typeface.DEFAULT

        var totalCal = 0f
        var totalProt = 0f
        var totalCarb = 0f
        var totalFat = 0f
        var totalSugar = 0f

        val linePaint = Paint()
        linePaint.color = Color.parseColor("#E0E2E8")
        linePaint.strokeWidth = 1f

        for (meal in meals) {
            totalCal += meal.calories
            totalProt += meal.protein
            totalCarb += meal.carbs
            totalFat += meal.fat
            totalSugar += meal.sugar

            paint.color = Color.parseColor("#2D313A")
            canvas.drawText(meal.mealType, 35f, yPos, paint)

            val shortName = if (meal.foodName.length > 22) meal.foodName.substring(0, 20) + ".." else meal.foodName
            canvas.drawText(shortName, 115f, yPos, paint)
            canvas.drawText("${meal.portionMultiplier} ${meal.portionUnit}", 270f, yPos, paint)
            canvas.drawText("${meal.calories.toInt()} kcal", 345f, yPos, paint)
            canvas.drawText("${meal.protein.toInt()}g", 415f, yPos, paint)
            canvas.drawText("${meal.carbs.toInt()}g", 470f, yPos, paint)
            canvas.drawText("${meal.fat.toInt()}g", 525f, yPos, paint)

            canvas.drawLine(30f, yPos + 8f, 565f, yPos + 8f, linePaint)
            yPos += 24f
            if (yPos > 750f) break
        }

        // Summary Totals Box
        yPos += 15f
        paint.color = Color.parseColor("#EAE8FD")
        canvas.drawRoundRect(30f, yPos, 565f, yPos + 50f, 8f, 8f, paint)

        paint.color = Color.parseColor("#6658E8")
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("DAILY TOTALS", 45f, yPos + 22f, paint)

        paint.color = Color.parseColor("#1C1E23")
        paint.textSize = 11f
        paint.typeface = Typeface.DEFAULT
        val totalsStr = "Calories: ${totalCal.toInt()} kcal   |   Protein: ${totalProt.toInt()}g   |   Carbs: ${totalCarb.toInt()}g   |   Fat: ${totalFat.toInt()}g   |   Sugar: ${totalSugar.toInt()}g"
        canvas.drawText(totalsStr, 45f, yPos + 40f, paint)

        // Footer
        paint.color = Color.parseColor("#A1A7B5")
        paint.textSize = 10f
        canvas.drawText("VitaLog — Small Steps. Big Changes. | Health and Lifestyle Companion", 30f, 810f, paint)

        pdfDocument.finishPage(page)

        val file = File(context.cacheDir, "VitaLog_FoodDiary_$date.pdf")
        FileOutputStream(file).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()
        return file
    }

    /**
     * Generates a 9:16 high-resolution Achievement Story Card Bitmap.
     * Perfect for WhatsApp Status, Stories, and social sharing.
     */
    fun createStoryAchievementCard(
        title: String,
        subtitle: String,
        daysStreak: Int,
        theme: String = "colorful" // colorful, minimal, celebration, dark
    ): Bitmap {
        val width = 1080
        val height = 1920
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val isDark = theme == "dark"
        val bgColor = if (isDark) Color.parseColor("#12141A") else Color.parseColor("#F8F9FD")
        canvas.drawColor(bgColor)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Decorative background top glow
        val gradient = LinearGradient(
            0f, 0f, 1080f, 800f,
            intArrayOf(Color.parseColor("#6658E8"), Color.parseColor("#20C9B0")),
            null, Shader.TileMode.CLAMP
        )
        paint.shader = gradient
        canvas.drawRoundRect(60f, 80f, 1020f, 1840f, 48f, 48f, paint)
        paint.shader = null

        // Inner Card Container
        paint.color = if (isDark) Color.parseColor("#1C1E26") else Color.WHITE
        canvas.drawRoundRect(90f, 200f, 990f, 1800f, 40f, 40f, paint)

        // App Logo / Tagline Header
        paint.color = Color.parseColor("#6658E8")
        paint.textSize = 46f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("VitaLog", 540f, 320f, paint)

        paint.color = Color.parseColor("#6E7687")
        paint.textSize = 26f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Small Steps. Big Changes.", 540f, 370f, paint)

        // Decorative Badge Circle
        val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        circlePaint.color = Color.parseColor("#EAE8FD")
        canvas.drawCircle(540f, 620f, 150f, circlePaint)

        // Trophy / Star emoji or text inside circle
        paint.color = Color.parseColor("#6658E8")
        paint.textSize = 90f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("🏆", 540f, 650f, paint)

        // Streak Count
        paint.color = Color.parseColor("#FFAA45")
        paint.textSize = 100f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("$daysStreak DAYS", 540f, 920f, paint)

        // Achievement Title
        paint.color = if (isDark) Color.WHITE else Color.parseColor("#1C1E23")
        paint.textSize = 52f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(title, 540f, 1020f, paint)

        // Subtitle / Encouragement Message
        paint.color = Color.parseColor("#6E7687")
        paint.textSize = 34f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText(subtitle, 540f, 1100f, paint)

        // Divider
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        linePaint.color = Color.parseColor("#ECEEF2")
        linePaint.strokeWidth = 3f
        canvas.drawLine(200f, 1220f, 880f, 1220f, linePaint)

        // Motivational Quote
        paint.color = Color.parseColor("#45B978")
        paint.textSize = 36f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Consistency is the Key to Wellness", 540f, 1340f, paint)

        // Date
        val dateStr = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
        paint.color = Color.parseColor("#A1A7B5")
        paint.textSize = 28f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Achieved on $dateStr", 540f, 1420f, paint)

        // Footer Brand Pill
        val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        pillPaint.color = Color.parseColor("#20C9B0")
        canvas.drawRoundRect(320f, 1600f, 760f, 1690f, 45f, 45f, pillPaint)

        paint.color = Color.WHITE
        paint.textSize = 32f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Track with VitaLog", 540f, 1658f, paint)

        return bitmap
    }

    /**
     * Saves bitmap to cache and returns the File.
     */
    fun saveStoryCardToFile(context: Context, bitmap: Bitmap): File {
        val file = File(context.cacheDir, "VitaLog_Story_Card.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file
    }
}
