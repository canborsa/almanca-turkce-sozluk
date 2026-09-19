package com.kelimearama.game

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.kelimearama.game.data.WordRepository
import com.kelimearama.game.ui.CategorySelectActivity
import com.kelimearama.game.ui.GameActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<MaterialButton>(R.id.btnPlay).setOnClickListener {
            startActivity(Intent(this, CategorySelectActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btnDailyPuzzle).setOnClickListener {
            // Start daily puzzle with a random category
            val randomCategory = WordRepository.getCategories().random()
            val intent = Intent(this, GameActivity::class.java).apply {
                putExtra("CATEGORY_ID", randomCategory.id)
                putExtra("LEVEL_NUMBER", (1..5).random())
                putExtra("IS_DAILY", true)
            }
            Toast.makeText(this, "Günün Bulmacası: ${randomCategory.name}", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
    }
}
