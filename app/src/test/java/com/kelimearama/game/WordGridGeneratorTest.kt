package com.kelimearama.game

import com.kelimearama.game.data.WordRepository
import com.kelimearama.game.engine.WordGridGenerator
import org.junit.Assert.*
import org.junit.Test

class WordGridGeneratorTest {

    @Test
    fun testGridGeneration() {
        val generator = WordGridGenerator()
        val category = WordRepository.getCategories().first()
        val result = generator.generateGrid(gridSize = 10, candidateWords = category.wordList, maxWords = 6)

        assertEquals(10, result.grid.size)
        assertEquals(10, result.grid[0].size)
        assertTrue(result.placedWords.isNotEmpty())
        assertTrue(result.placedWords.size <= 6)

        // Verify no empty spaces in grid
        for (r in 0 until 10) {
            for (c in 0 until 10) {
                assertNotEquals(' ', result.grid[r][c].letter)
            }
        }
    }
}
