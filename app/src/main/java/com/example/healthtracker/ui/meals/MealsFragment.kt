package com.example.healthtracker.ui.meals

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthtracker.VitaLogApplication
import com.example.healthtracker.data.entity.FoodItem
import com.example.healthtracker.data.entity.MealEntry
import com.example.healthtracker.databinding.FragmentMealsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MealsFragment : Fragment() {

    private var _binding: FragmentMealsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mealAdapter: MealAdapter
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val app = requireActivity().application as VitaLogApplication
        val repo = app.repository
        val today = dateFormat.format(Date())

        mealAdapter = MealAdapter(emptyList()) { meal ->
            // Delete confirmation
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Meal")
                .setMessage("Are you sure you want to remove ${meal.foodName}?")
                .setPositiveButton("Delete") { _, _ ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        repo.deleteMeal(meal)
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.rvMeals.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMeals.adapter = mealAdapter

        repo.getMealsForDate(today).observe(viewLifecycleOwner) { meals ->
            mealAdapter.updateData(meals)
            val cal = meals.sumOf { it.calories.toDouble() }.toInt()
            val prot = meals.sumOf { it.protein.toDouble() }.toInt()
            val carb = meals.sumOf { it.carbs.toDouble() }.toInt()
            val fat = meals.sumOf { it.fat.toDouble() }.toInt()

            binding.tvTotalCalories.text = "$cal kcal"
            binding.tvTotalProtein.text = "${prot}g"
            binding.tvTotalCarbs.text = "${carb}g"
            binding.tvTotalFat.text = "${fat}g"
        }

        binding.btnQuickAddFood.setOnClickListener {
            showAddFoodDialog()
        }
    }

    private fun showAddFoodDialog() {
        val app = requireActivity().application as VitaLogApplication
        lifecycleScope.launch(Dispatchers.IO) {
            val allFoods = app.repository.searchFoods("")
            withContext(Dispatchers.Main) {
                val foodNames = allFoods.map { "${it.name} (${it.calories.toInt()} kcal)" }.toTypedArray()

                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Select Food to Add")
                builder.setItems(foodNames) { _, which ->
                    val selectedFood = allFoods[which]
                    promptPortionAndMealType(selectedFood)
                }
                builder.setNeutralButton("+ Custom Food") { _, _ ->
                    promptCustomFood()
                }
                builder.setNegativeButton("Cancel", null)
                builder.show()
            }
        }
    }

    private fun promptPortionAndMealType(food: FoodItem) {
        val app = requireActivity().application as VitaLogApplication
        val mealTypes = arrayOf("Breakfast", "Lunch", "Dinner", "Snack")

        val layout = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val spinner = Spinner(requireContext())
        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, mealTypes)
        layout.addView(spinner)

        val inputPortion = EditText(requireContext()).apply {
            hint = "Portion multiplier (e.g. 1.0 or 1.5)"
            setText("1.0")
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        layout.addView(inputPortion)

        AlertDialog.Builder(requireContext())
            .setTitle("Add ${food.name}")
            .setView(layout)
            .setPositiveButton("Log") { _, _ ->
                val mult = inputPortion.text.toString().toFloatOrNull() ?: 1.0f
                val type = spinner.selectedItem.toString()
                val today = dateFormat.format(Date())
                val now = timeFormat.format(Date())

                val entry = MealEntry(
                    date = today,
                    time = now,
                    mealType = type,
                    foodName = food.name,
                    portionMultiplier = mult,
                    portionUnit = food.servingDescription,
                    calories = food.calories * mult,
                    protein = food.protein * mult,
                    carbs = food.carbs * mult,
                    fat = food.fat * mult,
                    sugar = food.sugar * mult,
                    fiber = food.fiber * mult,
                    sodiumMg = food.sodiumMg * mult
                )

                lifecycleScope.launch(Dispatchers.IO) {
                    app.repository.insertMeal(entry)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Logged ${food.name} to $type!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun promptCustomFood() {
        val app = requireActivity().application as VitaLogApplication
        val layout = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val nameInput = EditText(requireContext()).apply { hint = "Food Name" }
        val calInput = EditText(requireContext()).apply {
            hint = "Calories (kcal)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        val protInput = EditText(requireContext()).apply {
            hint = "Protein (g)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        val carbInput = EditText(requireContext()).apply {
            hint = "Carbs (g)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        val fatInput = EditText(requireContext()).apply {
            hint = "Fat (g)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        layout.addView(nameInput)
        layout.addView(calInput)
        layout.addView(protInput)
        layout.addView(carbInput)
        layout.addView(fatInput)

        AlertDialog.Builder(requireContext())
            .setTitle("Create Custom Food")
            .setView(layout)
            .setPositiveButton("Save & Log") { _, _ ->
                val name = nameInput.text.toString().ifBlank { "Custom Food" }
                val cal = calInput.text.toString().toFloatOrNull() ?: 100f
                val prot = protInput.text.toString().toFloatOrNull() ?: 5f
                val carb = carbInput.text.toString().toFloatOrNull() ?: 10f
                val fat = fatInput.text.toString().toFloatOrNull() ?: 2f

                val customItem = FoodItem(
                    name = name,
                    calories = cal,
                    protein = prot,
                    carbs = carb,
                    fat = fat,
                    isCustom = true
                )

                lifecycleScope.launch(Dispatchers.IO) {
                    app.repository.insertFood(customItem)
                    withContext(Dispatchers.Main) {
                        promptPortionAndMealType(customItem)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
