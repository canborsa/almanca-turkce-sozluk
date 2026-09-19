package com.kelimearama.game.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kelimearama.game.R
import com.kelimearama.game.data.WordRepository
import com.kelimearama.game.model.Category

class CategorySelectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_select)

        val rvCategories = findViewById<RecyclerView>(R.id.rvCategories)
        rvCategories.layoutManager = LinearLayoutManager(this)

        val categories = WordRepository.getCategories()
        rvCategories.adapter = CategoryAdapter(categories) { category ->
            val intent = Intent(this, LevelSelectActivity::class.java)
            intent.putExtra("CATEGORY_ID", category.id)
            startActivity(intent)
        }

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
    }

    class CategoryAdapter(
        private val categories: List<Category>,
        private val onItemClick: (Category) -> Unit
    ) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tvCategoryName)
            val tvDesc: TextView = view.findViewById(R.id.tvCategoryDesc)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_category, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val cat = categories[position]
            holder.tvName.text = cat.name
            holder.tvDesc.text = cat.description
            holder.itemView.setOnClickListener { onItemClick(cat) }
        }

        override fun getItemCount() = categories.size
    }
}
