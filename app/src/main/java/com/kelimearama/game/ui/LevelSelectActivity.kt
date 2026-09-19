package com.kelimearama.game.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kelimearama.game.R
import com.kelimearama.game.data.WordRepository
import com.kelimearama.game.model.Category

class LevelSelectActivity : AppCompatActivity() {

    private lateinit var category: Category

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_select)

        val catId = intent.getStringExtra("CATEGORY_ID") ?: "animals"
        category = WordRepository.getCategoryById(catId)

        findViewById<TextView>(R.id.tvTitle).text = "${category.name} - Seviyeler"

        val rvLevels = findViewById<RecyclerView>(R.id.rvLevels)
        rvLevels.layoutManager = GridLayoutManager(this, 3)

        val levels = (1..20).toList()
        rvLevels.adapter = LevelAdapter(levels) { levelNum ->
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("CATEGORY_ID", category.id)
            intent.putExtra("LEVEL_NUMBER", levelNum)
            startActivity(intent)
        }

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
    }

    class LevelAdapter(
        private val levels: List<Int>,
        private val onItemClick: (Int) -> Unit
    ) : RecyclerView.Adapter<LevelAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvLevel: TextView = view.findViewById(R.id.tvLevelNum)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_level, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val lvl = levels[position]
            holder.tvLevel.text = "Level $lvl"
            holder.itemView.setOnClickListener { onItemClick(lvl) }
        }

        override fun getItemCount() = levels.size
    }
}
