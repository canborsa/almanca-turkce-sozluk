package com.example.healthtracker.ui.meals

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtracker.data.entity.MealEntry
import com.example.healthtracker.databinding.ItemMealEntryBinding

class MealAdapter(
    private var meals: List<MealEntry>,
    private val onItemClick: (MealEntry) -> Unit
) : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    fun updateData(newMeals: List<MealEntry>) {
        meals = newMeals
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = ItemMealEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        holder.bind(meals[position])
    }

    override fun getItemCount(): Int = meals.size

    inner class MealViewHolder(private val binding: ItemMealEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(meal: MealEntry) {
            binding.tvMealCategory.text = meal.mealType
            binding.tvMealTime.text = meal.time
            binding.tvFoodName.text = meal.foodName
            binding.tvPortionAndMacros.text = "${meal.portionMultiplier} ${meal.portionUnit} | P: ${meal.protein.toInt()}g • C: ${meal.carbs.toInt()}g • F: ${meal.fat.toInt()}g"
            binding.tvMealCalories.text = "${meal.calories.toInt()} kcal"

            binding.root.setOnClickListener { onItemClick(meal) }
        }
    }
}
