package com.example.healthtracker.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.healthtracker.VitaLogApplication
import com.example.healthtracker.data.entity.FastingSession
import com.example.healthtracker.data.entity.HabitChallenge
import com.example.healthtracker.data.entity.UserProfile
import com.example.healthtracker.databinding.FragmentDashboardBinding
import com.example.healthtracker.service.AICoachEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    private var currentProfile: UserProfile? = null
    private var sugarChallenge: HabitChallenge? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val app = requireActivity().application as VitaLogApplication
        val repo = app.repository
        val today = dateFormat.format(Date())

        binding.tvDate.text = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault()).format(Date())

        // User Profile observer
        repo.userProfile.observe(viewLifecycleOwner) { profile ->
            currentProfile = profile
            profile?.let {
                binding.tvGreeting.text = "Hello, ${it.name}!"
                binding.tvCaloriesTarget.text = "/ ${it.dailyCalorieTarget} kcal"
                binding.cardCalories.visibility = if (it.moduleCalories) View.VISIBLE else View.GONE
                binding.cardHydration.visibility = if (it.moduleWater) View.VISIBLE else View.GONE
                binding.cardHabits.visibility = if (it.moduleHabits) View.VISIBLE else View.GONE
                binding.cardFasting.visibility = if (it.moduleFasting) View.VISIBLE else View.GONE
                binding.cardAiCoach.visibility = if (it.moduleCoach) View.VISIBLE else View.GONE
            }
        }

        // Today's Meals observer
        repo.getMealsForDate(today).observe(viewLifecycleOwner) { meals ->
            val totalCal = meals.sumOf { it.calories.toDouble() }.toInt()
            val totalProt = meals.sumOf { it.protein.toDouble() }.toInt()
            val totalCarb = meals.sumOf { it.carbs.toDouble() }.toInt()
            val totalFat = meals.sumOf { it.fat.toDouble() }.toInt()

            binding.tvCaloriesConsumed.text = totalCal.toString()
            val targetCal = currentProfile?.dailyCalorieTarget ?: 2000
            val pct = ((totalCal.toFloat() / targetCal) * 100).toInt().coerceIn(0, 100)
            binding.progressCalories.progress = pct
            binding.tvMacrosSummary.text = "Protein: ${totalProt}g | Carbs: ${totalCarb}g | Fat: ${totalFat}g"
        }

        // Today's Water observer
        repo.getWaterEntriesForDate(today).observe(viewLifecycleOwner) { entries ->
            val totalWater = entries.sumOf { it.amountMl }
            val targetWater = currentProfile?.dailyWaterTargetMl ?: 2500
            binding.tvWaterAmount.text = "$totalWater / $targetWater ml"
            val pct = ((totalWater.toFloat() / targetWater) * 100).toInt().coerceIn(0, 100)
            binding.progressWater.progress = pct
        }

        // Challenges observer
        repo.activeChallenges.observe(viewLifecycleOwner) { challenges ->
            sugarChallenge = challenges.firstOrNull { it.type == "sugar_free" }
            sugarChallenge?.let {
                binding.tvSugarFreeStreak.text = "Sugar-Free: ${it.currentStreak} Days Streak"
            }
        }

        // Fasting observer
        repo.latestFastingSession.observe(viewLifecycleOwner) { session ->
            if (session != null && !session.isCompleted) {
                val elapsedHours = ((System.currentTimeMillis() - session.startTimeMillis) / (1000 * 60 * 60)).toInt()
                val remaining = (session.targetDurationHours - elapsedHours).coerceAtLeast(0)
                binding.tvFastingStatus.text = "Fasting in progress: $elapsedHours hrs elapsed ($remaining hrs remaining)"
                binding.btnToggleFasting.text = "End Fast"
            } else {
                binding.tvFastingStatus.text = "Eating window / No active fast"
                binding.btnToggleFasting.text = "Start 16:8 Fast"
            }
        }

        // Setup AI Coach Tip
        lifecycleScope.launch {
            val meals = repo.getMealsForDateDirect(today)
            val water = repo.getWaterEntriesForDateDirect(today)
            val tip = AICoachEngine.generateDailyTip(
                currentProfile, meals, water, sugarChallenge?.currentStreak ?: 0, currentProfile?.language ?: "en"
            )
            binding.tvCoachTip.text = tip
        }

        // Hydration Quick-Add Listeners
        binding.btnAddWater200.setOnClickListener { logWater(200) }
        binding.btnAddWater330.setOnClickListener { logWater(330) }
        binding.btnAddWater500.setOnClickListener { logWater(500) }

        // Habit day logger
        binding.btnCompleteTodayHabit.setOnClickListener {
            sugarChallenge?.let { challenge ->
                lifecycleScope.launch(Dispatchers.IO) {
                    val newStreak = challenge.currentStreak + 1
                    val best = maxOf(newStreak, challenge.bestStreak)
                    repo.updateChallenge(challenge.copy(currentStreak = newStreak, bestStreak = best))
                    repo.logHabitDay(challenge.id, today, "completed")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Sugar-free day logged! Streak: $newStreak days 🌱", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Fasting toggle
        binding.btnToggleFasting.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val active = repo.getLatestFastingSessionDirect()
                if (active != null && !active.isCompleted) {
                    repo.saveFastingSession(active.copy(endTimeMillis = System.currentTimeMillis(), isCompleted = true))
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Fasting session ended!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    repo.saveFastingSession(
                        FastingSession(scheduleType = "16:8", targetDurationHours = 16, startTimeMillis = System.currentTimeMillis())
                    )
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "16:8 Fast started! Stay hydrated 💧", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun logWater(amountMl: Int) {
        val app = requireActivity().application as VitaLogApplication
        val today = dateFormat.format(Date())
        val nowTime = timeFormat.format(Date())
        lifecycleScope.launch(Dispatchers.IO) {
            app.repository.logWater(amountMl, today, nowTime)
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "+$amountMl ml logged! 💧", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
