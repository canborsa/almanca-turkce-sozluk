package com.example.healthtracker.ui.habits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.healthtracker.VitaLogApplication
import com.example.healthtracker.data.entity.HabitChallenge
import com.example.healthtracker.databinding.FragmentHabitsBinding
import com.example.healthtracker.service.GamificationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class HabitsFragment : Fragment() {

    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding!!

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var sugarChallenge: HabitChallenge? = null
    private var flourChallenge: HabitChallenge? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val app = requireActivity().application as VitaLogApplication
        val repo = app.repository
        val gamification = GamificationManager(repo)
        val today = dateFormat.format(Date())

        repo.activeChallenges.observe(viewLifecycleOwner) { challenges ->
            sugarChallenge = challenges.firstOrNull { it.type == "sugar_free" }
            flourChallenge = challenges.firstOrNull { it.type == "flour_free" }

            sugarChallenge?.let {
                binding.tvSugarStreakDetails.text = "Current Streak: ${it.currentStreak} Days | Best: ${it.bestStreak} Days"
            }
            flourChallenge?.let {
                binding.tvFlourStreakDetails.text = "Current Streak: ${it.currentStreak} Days | Best: ${it.bestStreak} Days"
            }
        }

        binding.btnLogSugarFree.setOnClickListener {
            sugarChallenge?.let { challenge ->
                lifecycleScope.launch(Dispatchers.IO) {
                    val newStreak = challenge.currentStreak + 1
                    val best = maxOf(newStreak, challenge.bestStreak)
                    repo.updateChallenge(challenge.copy(currentStreak = newStreak, bestStreak = best))
                    repo.logHabitDay(challenge.id, today, "completed")
                    gamification.checkSugarFreeStreak(newStreak)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Sugar-free day logged! Streak: $newStreak days 🌱", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.btnLogFlourFree.setOnClickListener {
            flourChallenge?.let { challenge ->
                lifecycleScope.launch(Dispatchers.IO) {
                    val newStreak = challenge.currentStreak + 1
                    val best = maxOf(newStreak, challenge.bestStreak)
                    repo.updateChallenge(challenge.copy(currentStreak = newStreak, bestStreak = best))
                    repo.logHabitDay(challenge.id, today, "completed")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Flour reduction logged! Streak: $newStreak days 🥖", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
