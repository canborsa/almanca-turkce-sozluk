package com.example.healthtracker.ui.settings

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.healthtracker.VitaLogApplication
import com.example.healthtracker.data.entity.UserProfile
import com.example.healthtracker.databinding.FragmentProfileSettingsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileSettingsFragment : Fragment() {

    private var _binding: FragmentProfileSettingsBinding? = null
    private val binding get() = _binding!!

    private var currentProfile: UserProfile? = null

    private val languages = listOf(
        "English" to "en",
        "Türkçe (Turkish)" to "tr",
        "Español (Spanish)" to "es",
        "Deutsch (German)" to "de",
        "Français (French)" to "fr",
        "Português (Portuguese)" to "pt"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val app = requireActivity().application as VitaLogApplication
        val repo = app.repository

        // Language Spinner Setup
        val langNames = languages.map { it.first }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, langNames)
        binding.spinnerLanguage.adapter = adapter

        repo.userProfile.observe(viewLifecycleOwner) { profile ->
            currentProfile = profile
            profile?.let {
                binding.etProfileName.setText(it.name)
                binding.etProfileWeight.setText(it.currentWeightKg.toString())
                binding.etTargetWeight.setText(it.targetWeightKg.toString())

                binding.switchCalories.isChecked = it.moduleCalories
                binding.switchWater.isChecked = it.moduleWater
                binding.switchHabits.isChecked = it.moduleHabits
                binding.switchFasting.isChecked = it.moduleFasting
                binding.switchCoach.isChecked = it.moduleCoach

                val index = languages.indexOfFirst { pair -> pair.second == it.language }
                if (index != -1) {
                    binding.spinnerLanguage.setSelection(index)
                }
            }
        }

        // Apply Language
        binding.btnApplyLanguage.setOnClickListener {
            val selectedIndex = binding.spinnerLanguage.selectedItemPosition
            val selectedLangCode = languages[selectedIndex].second
            currentProfile?.let {
                val updated = it.copy(language = selectedLangCode)
                lifecycleScope.launch(Dispatchers.IO) {
                    repo.saveProfile(updated)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Language changed to ${languages[selectedIndex].first}!", Toast.LENGTH_SHORT).show()
                        requireActivity().recreate()
                    }
                }
            }
        }

        // Save Profile & Modules
        binding.btnSaveProfile.setOnClickListener {
            val name = binding.etProfileName.text.toString().ifBlank { "VitaLog User" }
            val weight = binding.etProfileWeight.text.toString().toFloatOrNull() ?: 75f
            val targetWeight = binding.etTargetWeight.text.toString().toFloatOrNull() ?: 70f

            val updated = (currentProfile ?: UserProfile()).copy(
                name = name,
                currentWeightKg = weight,
                targetWeightKg = targetWeight,
                moduleCalories = binding.switchCalories.isChecked,
                moduleWater = binding.switchWater.isChecked,
                moduleHabits = binding.switchHabits.isChecked,
                moduleFasting = binding.switchFasting.isChecked,
                moduleCoach = binding.switchCoach.isChecked
            )

            lifecycleScope.launch(Dispatchers.IO) {
                repo.saveProfile(updated)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Profile and module settings saved!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Module Switch Instant Toggles
        val moduleListener = View.OnClickListener {
            currentProfile?.let {
                val updated = it.copy(
                    moduleCalories = binding.switchCalories.isChecked,
                    moduleWater = binding.switchWater.isChecked,
                    moduleHabits = binding.switchHabits.isChecked,
                    moduleFasting = binding.switchFasting.isChecked,
                    moduleCoach = binding.switchCoach.isChecked
                )
                lifecycleScope.launch(Dispatchers.IO) { repo.saveProfile(updated) }
            }
        }
        binding.switchCalories.setOnClickListener(moduleListener)
        binding.switchWater.setOnClickListener(moduleListener)
        binding.switchHabits.setOnClickListener(moduleListener)
        binding.switchFasting.setOnClickListener(moduleListener)
        binding.switchCoach.setOnClickListener(moduleListener)

        // Clear All Historical Data
        binding.btnClearAllData.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Clear All Data")
                .setMessage("Are you sure you want to reset all historical meals, water, and habits? This cannot be undone.")
                .setPositiveButton("Clear Everything") { _, _ ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        repo.clearAllData()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Historical data cleared.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
