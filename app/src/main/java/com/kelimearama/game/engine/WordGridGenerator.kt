package com.kelimearama.game.engine

import com.kelimearama.game.data.WordRepository
import com.kelimearama.game.model.Direction
import com.kelimearama.game.model.GridCell
import com.kelimearama.game.model.PlacedWord
import kotlin.random.Random

class WordGridGenerator {

    data class GridResult(
        val grid: Array<Array<GridCell>>,
        val placedWords: List<PlacedWord>
    )

    fun generateGrid(gridSize: Int, candidateWords: List<String>, maxWords: Int): GridResult {
        val gridLetters = Array(gridSize) { CharArray(gridSize) { ' ' } }
        val placedWords = mutableListOf<PlacedWord>()

        // Sort words by length descending to place larger words first
        val shuffledWords = candidateWords.distinct()
            .map { it.uppercase().trim().replace(" ", "") }
            .filter { it.length <= gridSize && it.length >= 2 }
            .shuffled()
            .sortedByDescending { it.length }

        val directions = Direction.values()

        var wordsCount = 0
        for (word in shuffledWords) {
            if (wordsCount >= maxWords) break

            var placed = false
            val allowedDirs = directions.toList().shuffled()

            // Try multiple positions and directions
            for (attempt in 0..100) {
                val dir = allowedDirs.random()
                val startRow = Random.nextInt(gridSize)
                val startCol = Random.nextInt(gridSize)

                val endRow = startRow + dir.dRow * (word.length - 1)
                val endCol = startCol + dir.dCol * (word.length - 1)

                if (endRow in 0 until gridSize && endCol in 0 until gridSize) {
                    var canPlace = true
                    for (i in word.indices) {
                        val r = startRow + dir.dRow * i
                        val c = startCol + dir.dCol * i
                        val currentCellChar = gridLetters[r][c]
                        if (currentCellChar != ' ' && currentCellChar != word[i]) {
                            canPlace = false
                            break
                        }
                    }

                    if (canPlace) {
                        for (i in word.indices) {
                            val r = startRow + dir.dRow * i
                            val c = startCol + dir.dCol * i
                            gridLetters[r][c] = word[i]
                        }
                        placedWords.add(
                            PlacedWord(
                                word = word,
                                startRow = startRow,
                                startCol = startCol,
                                endRow = endRow,
                                endCol = endCol
                            )
                        )
                        wordsCount++
                        placed = true
                        break
                    }
                }
            }
        }

        // Fill empty spaces with random Turkish letters
        val turkishLetters = WordRepository.TURKISH_ALPHABET
        val finalGrid = Array(gridSize) { r ->
            Array(gridSize) { c ->
                val char = if (gridLetters[r][c] == ' ') {
                    turkishLetters.random()
                } else {
                    gridLetters[r][c]
                }
                GridCell(row = r, col = c, letter = char)
            }
        }

        return GridResult(finalGrid, placedWords)
    }
}
