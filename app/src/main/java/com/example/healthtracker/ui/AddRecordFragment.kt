package com.example.healthtracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.healthtracker.data.HealthRepository
import com.example.healthtracker.data.database.AppDatabase
import com.example.healthtracker.databinding.FragmentAddRecordBinding
import com.example.healthtracker.ui.viewmodel.AddRecordViewModel
import com.example.healthtracker.ui.viewmodel.ViewModelFactory

class AddRecordFragment : Fragment() {

    private var _binding: FragmentAddRecordBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AddRecordViewModel
    private var userId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            userId = AddRecordFragmentArgs.fromBundle(it).userId
        }

        val database = AppDatabase.getDatabase(requireContext())
        val repository = HealthRepository(database.userDao(), database.healthRecordDao())
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(AddRecordViewModel::class.java)

        binding.saveButton.setOnClickListener {
            val bloodSugarStr = binding.bloodSugarEditText.text.toString().trim()
            val systolicStr = binding.systolicEditText.text.toString().trim()
            val diastolicStr = binding.diastolicEditText.text.toString().trim()

            if (bloodSugarStr.isEmpty()) {
                binding.bloodSugarEditText.error = "Kan şekeri gerekli"
                return@setOnClickListener
            }
            if (systolicStr.isEmpty()) {
                binding.systolicEditText.error = "Büyük tansiyon gerekli"
                return@setOnClickListener
            }
            if (diastolicStr.isEmpty()) {
                binding.diastolicEditText.error = "Küçük tansiyon gerekli"
                return@setOnClickListener
            }

            val bloodSugar = bloodSugarStr.toInt()
            val systolic = systolicStr.toInt()
            val diastolic = diastolicStr.toInt()

            viewModel.addHealthRecord(userId, bloodSugar, systolic, diastolic)
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
