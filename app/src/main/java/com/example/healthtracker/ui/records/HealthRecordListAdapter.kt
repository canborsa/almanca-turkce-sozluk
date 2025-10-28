package com.example.healthtracker.ui.records

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtracker.R
import com.example.healthtracker.data.HealthRecord
import java.text.SimpleDateFormat
import java.util.*

class HealthRecordListAdapter : ListAdapter<HealthRecord, HealthRecordListAdapter.HealthRecordViewHolder>(HealthRecordsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HealthRecordViewHolder {
        return HealthRecordViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: HealthRecordViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class HealthRecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val typeItemView: TextView = itemView.findViewById(R.id.textViewType)
        private val valueItemView: TextView = itemView.findViewById(R.id.textViewValue)
        private val dateItemView: TextView = itemView.findViewById(R.id.textViewDate)

        fun bind(record: HealthRecord) {
            typeItemView.text = record.type
            if (record.type == "BLOOD_PRESSURE") {
                valueItemView.text = "${record.value1}/${record.value2}"
                typeItemView.setTextColor(Color.BLUE)
            } else {
                valueItemView.text = record.value1
                typeItemView.setTextColor(Color.RED)
            }
            dateItemView.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(record.date))
        }

        companion object {
            fun create(parent: ViewGroup): HealthRecordViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.health_record_item, parent, false)
                return HealthRecordViewHolder(view)
            }
        }
    }

    class HealthRecordsComparator : DiffUtil.ItemCallback<HealthRecord>() {
        override fun areItemsTheSame(oldItem: HealthRecord, newItem: HealthRecord): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: HealthRecord, newItem: HealthRecord): Boolean {
            return oldItem.id == newItem.id
        }
    }
}
