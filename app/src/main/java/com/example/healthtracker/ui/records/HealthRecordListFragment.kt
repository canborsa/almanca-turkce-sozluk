package com.example.healthtracker.ui.records

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthtracker.HealthTrackerApplication
import com.example.healthtracker.data.HealthRecord
import com.example.healthtracker.databinding.FragmentHealthRecordListBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class HealthRecordListFragment : Fragment() {

    private var _binding: FragmentHealthRecordListBinding? = null
    private val binding get() = _binding!!

    private val args: HealthRecordListFragmentArgs by navArgs()

    private val healthRecordViewModel: HealthRecordViewModel by viewModels {
        HealthRecordViewModelFactory((activity?.application as HealthTrackerApplication).healthRecordRepository)
    }

    private lateinit var records: List<HealthRecord>

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                createAndSharePdf()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permission denied. Cannot create PDF.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealthRecordListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = HealthRecordListAdapter()
        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(context)

        healthRecordViewModel.getRecordsForUser(args.userId).observe(viewLifecycleOwner) { records ->
            records?.let {
                this.records = it
                adapter.submitList(it)
            }
        }

        binding.fabAdd.setOnClickListener {
            val action =
                HealthRecordListFragmentDirections.actionHealthRecordListFragmentToAddHealthRecordFragment(
                    args.userId
                )
            findNavController().navigate(action)
        }

        binding.fabShare.setOnClickListener {
            checkPermissionAndCreatePdf()
        }
    }

    private fun checkPermissionAndCreatePdf() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                createAndSharePdf()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                // Explain to the user why the permission is needed
                Toast.makeText(
                    requireContext(),
                    "Storage permission is required to create a PDF file.",
                    Toast.LENGTH_LONG
                ).show()
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun createAndSharePdf() {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = android.graphics.Paint()
        paint.textSize = 12f

        var yPosition = 40f
        for (record in records) {
            val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(record.date))
            val value = if (record.type == "BLOOD_PRESSURE") {
                "${record.value1}/${record.value2}"
            } else {
                record.value1
            }
            canvas.drawText("${record.type}: $value - $date", 20f, yPosition, paint)
            yPosition += 20
        }

        pdfDocument.finishPage(page)

        val file = File(requireContext().getExternalFilesDir(null), "HealthRecords.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(requireContext(), "PDF created successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error creating PDF: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
        pdfDocument.close()

        sharePdf(file)
    }

    private fun sharePdf(file: File) {
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "application/pdf"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(shareIntent, "Share PDF via")
        val resInfoList =
            requireContext().packageManager.queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            requireContext().grantUriPermission(
                packageName,
                uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        startActivity(chooser)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
