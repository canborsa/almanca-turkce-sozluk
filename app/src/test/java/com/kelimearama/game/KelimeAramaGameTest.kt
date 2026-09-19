package com.kelimearama.game

import com.kelimearama.game.data.WordRepository
import com.kelimearama.game.engine.WordGridGenerator
import org.junit.Assert.*
import org.junit.Test

class KelimeAramaGameTest {

    @Test
    fun testWordRepositoryCategoriesAndWords() {
        val categories = WordRepository.getCategories()
        assertTrue(categories.isNotEmpty())

        for (cat in categories) {
            assertNotNull(cat.id)
            assertNotNull(cat.name)
            assertTrue(cat.wordList.isNotEmpty())

            // Check Turkish uppercase letters in words
            for (word in cat.wordList) {
                assertEquals(word, word.uppercase())
                assertFalse(word.contains(" "))
            }
        }
    }

    @Test
    fun testGridGeneratorPlacement() {
        val generator = WordGridGenerator()
        val category = WordRepository.getCategoryById("animals")

        val result = generator.generateGrid(gridSize = 10, candidateWords = category.wordList, maxWords = 8)

        assertEquals(10, result.grid.size)
        assertEquals(10, result.grid[0].size)
        assertTrue(result.placedWords.isNotEmpty())

        // Ensure placed words exist in grid at specified coordinates
        for (placed in result.placedWords) {
            val word = placed.word
            val dR = Integer.signum(placed.endRow - placed.startRow)
            val dC = Integer.signum(placed.endCol - placed.startCol)

            var r = placed.startRow
            var c = placed.startCol
            for (char in word) {
                assertEquals(char, result.grid[r][c].letter)
                r += dR
                c += dC
            }
        }
    }
}
