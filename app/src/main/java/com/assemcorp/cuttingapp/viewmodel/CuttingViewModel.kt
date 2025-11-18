package com.assemcorp.cuttingapp.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.assemcorp.cuttingapp.data.Material
import com.assemcorp.cuttingapp.data.Part
import com.assemcorp.cuttingapp.logic.CuttingAlgorithm
import com.assemcorp.cuttingapp.logic.PartToPlace
import com.assemcorp.cuttingapp.logic.RollResult
import com.assemcorp.cuttingapp.repository.PartRepository

class CuttingViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PartRepository(application)
    val parts = mutableStateListOf<Part>()
    private var partIdCounter = 0

    val materials = listOf(
        Material("80cm x 10 Metre", 80.0, 1000.0),
        Material("100cm x 10 Metre", 100.0, 1000.0),
        Material("120cm x 10 Metre", 120.0, 1000.0),
        Material("Serbest Ölçü", 0.0, 0.0, isCustom = true) // Special value for custom size
    )
    var selectedMaterial by mutableStateOf(materials.first())
    var customWidth by mutableStateOf("")
    var customHeight by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)

    var currentUnit by mutableStateOf("cm")
    var currentLanguage by mutableStateOf("tr")
    var calculationResult by mutableStateOf<List<RollResult>?>(null)

    init {
        val loadedParts = repository.loadParts()
        parts.addAll(loadedParts)
        partIdCounter = (loadedParts.maxOfOrNull { it.id } ?: -1) + 1
    }

    fun addPart(width: Double, height: Double, quantity: Int) {
        val widthInCm = if (currentUnit == "in") width * 2.54 else width
        val heightInCm = if (currentUnit == "in") height * 2.54 else height
        parts.add(Part(partIdCounter++, widthInCm, heightInCm, quantity))
        saveParts()
    }

    fun removePart(part: Part) {
        parts.remove(part)
        saveParts()
    }

    fun setUnit(unit: String) {
        currentUnit = unit
    }

    fun setLanguage(language: String) {
        currentLanguage = language
    }

    fun calculateCuttingPlan() {
        errorMessage = null
        val rollWidth = if (selectedMaterial.isCustom) customWidth.toDoubleOrNull() ?: 0.0 else selectedMaterial.width
        val rollHeight = if (selectedMaterial.isCustom) customHeight.toDoubleOrNull() ?: 0.0 else selectedMaterial.height

        if (rollWidth <= 0 || rollHeight <= 0) {
            errorMessage = "Lütfen geçerli rulo boyutları girin."
            return
        }

        var uniqueIdCounter = 0
        val allPartsToPlace = parts.flatMap { part ->
            List(part.quantity) { PartToPlace(part, uniqueIdCounter++) }
        }.toMutableList()

        val results = mutableListOf<RollResult>()
        var rollNumber = 1

        while (allPartsToPlace.any { !it.isPlaced }) {
            val algorithm = CuttingAlgorithm()
            val placedOnThisRoll = algorithm.placePartsOnRoll(rollWidth, rollHeight, allPartsToPlace.filter { !it.isPlaced }.toMutableList())

            if (placedOnThisRoll.isEmpty()) {
                break
            }

            placedOnThisRoll.forEach { placedPart ->
                allPartsToPlace.first { it.uniqueId == placedPart.uniqueId }.isPlaced = true
            }

            results.add(RollResult(rollNumber++, placedOnThisRoll))
        }
        calculationResult = results
    }

    private fun saveParts() {
        repository.saveParts(parts.toList())
    }
}
