package com.assemcorp.cuttingapp.logic

import com.assemcorp.cuttingapp.data.Part

// Represents any rectangle with position and dimensions
data class Rectangle(val x: Double, val y: Double, val width: Double, val height: Double)

// Represents a part that has been placed on a roll
data class PlacedPart(val originalPart: Part, val rect: Rectangle, val uniqueId: Int)

// Holds the results for a single roll
data class RollResult(
    val rollNumber: Int,
    val placedParts: List<PlacedPart>
)

class CuttingAlgorithm {

    /**
     * Tries to place a list of parts onto a single roll using a best-fit heuristic.
     * This is a translation of the original JavaScript algorithm.
     */
    fun placePartsOnRoll(
        rollWidth: Double,
        rollHeight: Double,
        partsToPlace: MutableList<PartToPlace>
    ): List<PlacedPart> {
        val placedParts = mutableListOf<PlacedPart>()
        val freeAreas = mutableListOf(Rectangle(0.0, 0.0, rollWidth, rollHeight))

        // Sort parts by area, descending. A common heuristic for packing.
        partsToPlace.sortByDescending { it.part.width * it.part.height }

        for (part in partsToPlace) {
            if (part.isPlaced) continue

            var bestFit: BestFit? = null

            for (i in freeAreas.indices.reversed()) {
                val area = freeAreas[i]

                // Try original orientation
                if (part.part.width <= area.width && part.part.height <= area.height) {
                    val fitness = (area.width * area.height) - (part.part.width * part.part.height)
                    if (bestFit == null || fitness < bestFit.fitness) {
                        bestFit = BestFit(
                            fitness = fitness,
                            areaIndex = i,
                            partWidth = part.part.width,
                            partHeight = part.part.height
                        )
                    }
                }

                // Try rotated orientation (if not a square)
                if (part.part.width != part.part.height) {
                    if (part.part.height <= area.width && part.part.width <= area.height) {
                        val fitness = (area.width * area.height) - (part.part.height * part.part.width)
                         if (bestFit == null || fitness < bestFit.fitness) {
                            bestFit = BestFit(
                                fitness = fitness,
                                areaIndex = i,
                                partWidth = part.part.height,
                                partHeight = part.part.width
                            )
                        }
                    }
                }
            }

            if (bestFit != null) {
                val chosenArea = freeAreas.removeAt(bestFit.areaIndex)

                placedParts.add(
                    PlacedPart(
                        originalPart = part.part,
                        rect = Rectangle(
                            x = chosenArea.x,
                            y = chosenArea.y,
                            width = bestFit.partWidth,
                            height = bestFit.partHeight
                        ),
                        uniqueId = part.uniqueId
                    )
                )
                part.isPlaced = true

                // Split the remaining area into two new free areas
                // Area to the right
                val rightArea = Rectangle(
                    x = chosenArea.x + bestFit.partWidth,
                    y = chosenArea.y,
                    width = chosenArea.width - bestFit.partWidth,
                    height = bestFit.partHeight
                )
                // Area below
                val bottomArea = Rectangle(
                    x = chosenArea.x,
                    y = chosenArea.y + bestFit.partHeight,
                    width = chosenArea.width,
                    height = chosenArea.height - bestFit.partHeight
                )

                if (rightArea.width > 0 && rightArea.height > 0) freeAreas.add(rightArea)
                if (bottomArea.width > 0 && bottomArea.height > 0) freeAreas.add(bottomArea)
            }
        }
        return placedParts
    }

    // Helper data class to find the best placement spot
    private data class BestFit(val fitness: Double, val areaIndex: Int, val partWidth: Double, val partHeight: Double)
}

// A wrapper for the Part data class to track its placement status
data class PartToPlace(val part: Part, val uniqueId: Int, var isPlaced: Boolean = false)
