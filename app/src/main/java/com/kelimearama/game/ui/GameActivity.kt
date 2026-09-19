package com.kelimearama.game.ui

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.widget.Chronometer
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.kelimearama.game.R
import com.kelimearama.game.data.WordRepository
import com.kelimearama.game.engine.WordGridGenerator
import com.kelimearama.game.model.Category
import com.kelimearama.game.model.PlacedWord

class GameActivity : AppCompatActivity(), WordGridView.OnWordSelectedListener {

    private lateinit var category: Category
    private var levelNumber: Int = 1
    private var gridSize: Int = 8
    private var maxWords: Int = 5

    private lateinit var gridView: WordGridView
    private lateinit var wordsRecyclerView: RecyclerView
    private lateinit var tvFoundCount: TextView
    private lateinit var tvTitle: TextView
    private lateinit var chronometer: Chronometer
    private lateinit var btnHint: MaterialButton
    private lateinit var btnRocket: MaterialButton

    private lateinit var wordAdapter: FoundWordsAdapter
    private var placedWords = mutableListOf<PlacedWord>()
    private var foundCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val catId = intent.getStringExtra("CATEGORY_ID") ?: "animals"
        category = WordRepository.getCategoryById(catId)
        levelNumber = intent.getIntExtra("LEVEL_NUMBER", 1)

        // Calculate grid size and word count based on level
        gridSize = Math.min(8 + (levelNumber - 1) / 2, 12)
        maxWords = Math.min(4 + levelNumber, 10)

        initViews()
        setupGame()
    }

    private fun initViews() {
        gridView = findViewById(R.id.wordGridView)
        wordsRecyclerView = findViewById(R.id.rvWords)
        tvFoundCount = findViewById(R.id.tvFoundCount)
        tvTitle = findViewById(R.id.tvGameTitle)
        chronometer = findViewById(R.id.chronometer)
        btnHint = findViewById(R.id.btnHint)
        btnRocket = findViewById(R.id.btnRocket)

        tvTitle.text = "${category.name} - Seviye $levelNumber"

        gridView.setOnWordSelectedListener(this)

        btnHint.setOnClickListener { useHint() }
        btnRocket.setOnClickListener { useRocket() }

        findViewById<View>(R.id.btnBack).setOnClickListener { onBackPressed() }
    }

    private fun setupGame() {
        val generator = WordGridGenerator()
        val result = generator.generateGrid(gridSize, category.wordList, maxWords)

        placedWords = result.placedWords.toMutableList()
        foundCount = 0

        gridView.setGrid(result.grid)

        wordAdapter = FoundWordsAdapter(placedWords)
        wordsRecyclerView.layoutManager = GridLayoutManager(this, 3)
        wordsRecyclerView.adapter = wordAdapter

        updateFoundCountText()

        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
    }

    private fun updateFoundCountText() {
        tvFoundCount.text = "Bulunan: $foundCount / ${placedWords.size}"
    }

    override fun onWordSelected(selectedString: String, startRow: Int, startCol: Int, endRow: Int, endCol: Int) {
        val reversedString = selectedString.reversed()

        val match = placedWords.find {
            !it.isFound && (
                (it.word == selectedString && it.startRow == startRow && it.startCol == startCol && it.endRow == endRow && it.endCol == endCol) ||
                (it.word == reversedString && it.startRow == endRow && it.startCol == endCol && it.endRow == startRow && it.endCol == startCol) ||
                (it.word == selectedString || it.word == reversedString) && isCoordinatesMatch(it, startRow, startCol, endRow, endCol)
            )
        }

        if (match != null) {
            match.isFound = true
            foundCount++
            gridView.addHighlight(match.startRow, match.startCol, match.endRow, match.endCol, foundCount)
            wordAdapter.notifyDataSetChanged()
            updateFoundCountText()

            if (foundCount == placedWords.size) {
                onLevelCompleted()
            }
        }
    }

    private fun isCoordinatesMatch(word: PlacedWord, r1: Int, c1: Int, r2: Int, c2: Int): Boolean {
        return (word.startRow == r1 && word.startCol == c1 && word.endRow == r2 && word.endCol == c2) ||
               (word.startRow == r2 && word.startCol == c2 && word.endRow == r1 && word.endCol == c1)
    }

    private fun useHint() {
        val unfound = placedWords.filter { !it.isFound }
        if (unfound.isNotEmpty()) {
            val target = unfound.random()
            Toast.makeText(
                this,
                "İpucu: '${target.word}' kelimesinin ilk harfi (${target.startRow + 1}. satır, ${target.startCol + 1}. sütun)",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(this, "Tüm kelimeler zaten bulundu!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun useRocket() {
        val unfound = placedWords.filter { !it.isFound }
        if (unfound.isNotEmpty()) {
            val target = unfound.random()
            onWordSelected(target.word, target.startRow, target.startCol, target.endRow, target.endCol)
            Toast.makeText(this, "Roket: '${target.word}' bulundu!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onLevelCompleted() {
        chronometer.stop()
        val timeElapsed = (SystemClock.elapsedRealtime() - chronometer.base) / 1000

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_level_completed, null)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvCompletedDesc)
        tvMessage.text = "Tebrikler! Seviye $levelNumber tamamlandı.\nSüre: ${timeElapsed} saniye"

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<MaterialButton>(R.id.btnNextLevel).setOnClickListener {
            dialog.dismiss()
            levelNumber++
            intent.putExtra("LEVEL_NUMBER", levelNumber)
            recreate()
        }

        dialogView.findViewById<MaterialButton>(R.id.btnHome).setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }
}
