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
import com.example.healthtracker.databinding.FragmentAddUserBinding
import com.example.healthtracker.ui.viewmodel.AddUserViewModel
import com.example.healthtracker.ui.viewmodel.ViewModelFactory

class AddUserFragment : Fragment() {

    private var _binding: FragmentAddUserBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AddUserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        val repository = HealthRepository(database.userDao(), database.healthRecordDao())
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(AddUserViewModel::class.java)

        binding.saveButton.setOnClickListener {
            val firstName = binding.firstNameEditText.text.toString().trim()
            val lastName = binding.lastNameEditText.text.toString().trim()
            val ageStr = binding.ageEditText.text.toString().trim()

            if (firstName.isEmpty()) {
                binding.firstNameEditText.error = "Ad gerekli"
                return@setOnClickListener
            }
            if (lastName.isEmpty()) {
                binding.lastNameEditText.error = "Soyad gerekli"
                return@setOnClickListener
            }
            if (ageStr.isEmpty()) {
                binding.ageEditText.error = "Yaş gerekli"
                return@setOnClickListener
            }

            val age = ageStr.toInt()
            viewModel.addUser(firstName, lastName, age)
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
