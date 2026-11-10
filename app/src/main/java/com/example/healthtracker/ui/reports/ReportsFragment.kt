package com.example.healthtracker.ui.reports

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.healthtracker.VitaLogApplication
import com.example.healthtracker.databinding.FragmentReportsBinding
import com.example.healthtracker.service.ReportExportService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var storyBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val app = requireActivity().application as VitaLogApplication
        val repo = app.repository
        val today = dateFormat.format(Date())

        // Generate 9:16 Story Card preview
        lifecycleScope.launch(Dispatchers.IO) {
            val challenges = repo.getAllChallengesDirect()
            val sugar = challenges.firstOrNull { it.type == "sugar_free" }
            val streak = sugar?.currentStreak ?: 5

            val bitmap = ReportExportService.createStoryAchievementCard(
                title = "Sugar-Free Milestone",
                subtitle = "Eliminating added sugars one day at a time!",
                daysStreak = streak,
                theme = "colorful"
            )
            storyBitmap = bitmap
            withContext(Dispatchers.Main) {
                binding.ivStoryPreview.setImageBitmap(bitmap)
            }
        }

        // Daily PDF Export
        binding.btnExportDailyPdf.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val meals = repo.getMealsForDateDirect(today)
                val pdfFile = ReportExportService.generateDailyFoodDiaryPDF(requireContext(), today, meals)
                withContext(Dispatchers.Main) {
                    shareFile(pdfFile, "application/pdf", "VitaLog Daily Food Diary ($today)")
                }
            }
        }

        // Daily CSV Export
        binding.btnExportDailyCsv.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val meals = repo.getMealsForDateDirect(today)
                val csvFile = ReportExportService.generateDailyFoodDiaryCSV(requireContext(), today, meals)
                withContext(Dispatchers.Main) {
                    shareFile(csvFile, "text/csv", "VitaLog Daily Food Diary CSV ($today)")
                }
            }
        }

        // WhatsApp Share Food Diary
        binding.btnShareDailyWhatsApp.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val meals = repo.getMealsForDateDirect(today)
                val pdfFile = ReportExportService.generateDailyFoodDiaryPDF(requireContext(), today, meals)
                withContext(Dispatchers.Main) {
                    shareToWhatsApp(pdfFile, "application/pdf", "Here is my daily food diary with VitaLog! 🥑")
                }
            }
        }

        // Share 9:16 Story Card
        binding.btnShareStoryCard.setOnClickListener {
            storyBitmap?.let { bmp ->
                val imageFile = ReportExportService.saveStoryCardToFile(requireContext(), bmp)
                shareToWhatsApp(imageFile, "image/png", "Completed my healthy habit milestone with VitaLog! Small steps, big changes! 💪")
            } ?: Toast.makeText(requireContext(), "Generating story card...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareFile(file: File, mimeType: String, title: String) {
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Share via"))
    }

    private fun shareToWhatsApp(file: File, mimeType: String, messageText: String) {
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, messageText)
            setPackage("com.whatsapp")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback to native chooser if WhatsApp is not directly installed
            shareFile(file, mimeType, messageText)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
