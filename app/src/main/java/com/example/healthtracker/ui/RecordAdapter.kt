package com.example.healthtracker.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtracker.data.database.HealthRecord
import com.example.healthtracker.databinding.ItemRecordBinding
import com.example.healthtracker.ui.viewmodel.RecordListViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordAdapter(private val viewModel: RecordListViewModel, private val age: Int) :
    ListAdapter<HealthRecord, RecordAdapter.RecordViewHolder>(RecordDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val binding = ItemRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = getItem(position)
        holder.bind(record)
    }

    inner class RecordViewHolder(private val binding: ItemRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(record: HealthRecord) {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            binding.dateTextView.text = sdf.format(Date(record.timestamp))
            binding.bloodSugarTextView.text = "Kan Şekeri: ${record.bloodSugar}"
            binding.bloodPressureTextView.text =
                "Tansiyon: ${record.systolicPressure}/${record.diastolicPressure}"

            if (viewModel.isBloodSugarOutOfRange(age, record.bloodSugar) ||
                viewModel.isBloodPressureOutOfRange(age, record.systolicPressure, record.diastolicPressure)
            ) {
                itemView.setBackgroundColor(Color.RED)
            } else {
                itemView.setBackgroundColor(Color.WHITE)
            }

            binding.bloodSugarTextView.setTextColor(Color.parseColor("#FF69B4"))
            binding.bloodPressureTextView.setTextColor(Color.BLUE)
        }
    }
}

class RecordDiffCallback : DiffUtil.ItemCallback<HealthRecord>() {
    override fun areItemsTheSame(oldItem: HealthRecord, newItem: HealthRecord): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: HealthRecord, newItem: HealthRecord): Boolean {
        return oldItem == newItem
    }
}
