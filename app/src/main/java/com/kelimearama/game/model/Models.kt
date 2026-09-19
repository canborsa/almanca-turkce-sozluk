package com.kelimearama.game.model

import java.io.Serializable

data class Category(
    val id: String,
    val name: String,
    val iconName: String,
    val description: String,
    val wordList: List<String>
) : Serializable

data class Level(
    val levelNumber: Int,
    val gridSize: Int,
    val wordCount: Int
) : Serializable

data class GridCell(
    val row: Int,
    val col: Int,
    val letter: Char,
    var isSelected: Boolean = false,
    var isFound: Boolean = false,
    var highlightColor: Int = 0
)

data class PlacedWord(
    val word: String,
    val startRow: Int,
    val startCol: Int,
    val endRow: Int,
    val endCol: Int,
    var isFound: Boolean = false,
    var color: Int = 0
)

enum class Direction(val dRow: Int, val dCol: Int) {
    HORIZONTAL(0, 1),
    VERTICAL(1, 0),
    DIAGONAL_DOWN(1, 1),
    DIAGONAL_UP(-1, 1),
    HORIZONTAL_REVERSE(0, -1),
    VERTICAL_REVERSE(-1, 0)
}
