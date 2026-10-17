package com.example.healthtracker.ui.user

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.healthtracker.HealthTrackerApplication
import com.example.healthtracker.data.User
import com.example.healthtracker.databinding.FragmentAddUserBinding

class AddUserFragment : Fragment() {

    private var _binding: FragmentAddUserBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((activity?.application as HealthTrackerApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSave.setOnClickListener {
            insertDataToDatabase()
        }
    }

    private fun insertDataToDatabase() {
        val firstName = binding.editTextFirstName.text.toString()
        val lastName = binding.editTextLastName.text.toString()
        val age = binding.editTextAge.text.toString()

        if (inputCheck(firstName, lastName, age)) {
            val user = User(0, firstName, lastName, Integer.parseInt(age))
            userViewModel.insert(user)
            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_LONG).show()
            findNavController().navigateUp()
        } else {
            Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_LONG).show()
        }
    }

    private fun inputCheck(firstName: String, lastName: String, age: String): Boolean {
        return !(TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(age))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
