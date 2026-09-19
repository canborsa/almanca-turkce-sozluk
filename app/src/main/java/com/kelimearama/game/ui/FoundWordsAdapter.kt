package com.kelimearama.game.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kelimearama.game.R
import com.kelimearama.game.model.PlacedWord

class FoundWordsAdapter(private val words: List<PlacedWord>) :
    RecyclerView.Adapter<FoundWordsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvWord: TextView = view.findViewById(R.id.tvWordItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_word, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = words[position]
        holder.tvWord.text = item.word

        if (item.isFound) {
            holder.tvWord.paintFlags = holder.tvWord.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvWord.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.accent_green))
        } else {
            holder.tvWord.paintFlags = holder.tvWord.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.tvWord.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.text_dark))
        }
    }

    override fun getItemCount(): Int = words.size
}
