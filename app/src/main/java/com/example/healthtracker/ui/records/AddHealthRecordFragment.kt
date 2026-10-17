package com.example.healthtracker.ui.records

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.healthtracker.HealthTrackerApplication
import com.example.healthtracker.data.HealthRecord
import com.example.healthtracker.databinding.FragmentAddHealthRecordBinding

class AddHealthRecordFragment : Fragment() {

    private var _binding: FragmentAddHealthRecordBinding? = null
    private val binding get() = _binding!!

    private val args: AddHealthRecordFragmentArgs by navArgs()

    private val healthRecordViewModel: HealthRecordViewModel by viewModels {
        HealthRecordViewModelFactory((activity?.application as HealthTrackerApplication).healthRecordRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddHealthRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSave.setOnClickListener {
            insertDataToDatabase()
        }
    }

    private fun insertDataToDatabase() {
        val systolic = binding.editTextSystolic.text.toString()
        val diastolic = binding.editTextDiastolic.text.toString()
        val sugar = binding.editTextSugar.text.toString()

        if (inputCheck(systolic, diastolic, sugar)) {
            if (systolic.isNotEmpty() && diastolic.isNotEmpty()) {
                val bloodPressureRecord = HealthRecord(
                    0,
                    args.userId,
                    "BLOOD_PRESSURE",
                    systolic,
                    diastolic,
                    System.currentTimeMillis()
                )
                healthRecordViewModel.insert(bloodPressureRecord)
            }
            if (sugar.isNotEmpty()) {
                val sugarRecord = HealthRecord(
                    0,
                    args.userId,
                    "SUGAR",
                    sugar,
                    null,
                    System.currentTimeMillis()
                )
                healthRecordViewModel.insert(sugarRecord)
            }
            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_LONG).show()
            findNavController().navigateUp()
        } else {
            Toast.makeText(requireContext(), "Please fill out all fields for a record type.", Toast.LENGTH_LONG).show()
        }
    }

    private fun inputCheck(systolic: String, diastolic: String, sugar: String): Boolean {
        val isBloodPressureValid = (TextUtils.isEmpty(systolic) && TextUtils.isEmpty(diastolic)) ||
                (!TextUtils.isEmpty(systolic) && !TextUtils.isEmpty(diastolic))
        val isSugarValid = !TextUtils.isEmpty(sugar)
        return isBloodPressureValid && (isSugarValid || (!TextUtils.isEmpty(systolic) && !TextUtils.isEmpty(diastolic)))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
