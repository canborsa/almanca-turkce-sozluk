package com.example.alarm.ui.stopwatch

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.alarm.databinding.FragmentStopwatchBinding

class StopwatchFragment : Fragment() {

    private var _binding: FragmentStopwatchBinding? = null
    private val binding get() = _binding!!

    private var isRunning = false
    private var handler = Handler(Looper.getMainLooper())
    private var startTime = 0L
    private var timeInMillis = 0L
    private var timeSwapBuff = 0L
    private var updateTime = 0L
    private var lapCount = 1

    private val runnable = object : Runnable {
        override fun run() {
            timeInMillis = SystemClock.uptimeMillis() - startTime
            updateTime = timeSwapBuff + timeInMillis
            val secs = (updateTime / 1000).toInt()
            val mins = secs / 60
            val milliseconds = (updateTime % 1000).toInt()
            binding.tvStopwatch.text = String.format("%02d:%02d:%03d", mins, secs % 60, milliseconds)
            handler.postDelayed(this, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStopwatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStartStop.setOnClickListener {
            if (isRunning) {
                stopStopwatch()
            } else {
                startStopwatch()
            }
        }

        binding.btnLapReset.setOnClickListener {
            if (isRunning) {
                recordLap()
            } else {
                resetStopwatch()
            }
        }

        binding.btnLapReset.isEnabled = false // Initially disable lap/reset
    }

    private fun startStopwatch() {
        isRunning = true
        startTime = SystemClock.uptimeMillis()
        handler.postDelayed(runnable, 0)
        binding.btnStartStop.text = "Durdur"
        binding.btnLapReset.text = "Tur"
        binding.btnLapReset.isEnabled = true
    }

    private fun stopStopwatch() {
        isRunning = false
        timeSwapBuff += timeInMillis
        handler.removeCallbacks(runnable)
        binding.btnStartStop.text = "Devam Et"
        binding.btnLapReset.text = "Sıfırla"
    }

    private fun resetStopwatch() {
        isRunning = false
        startTime = 0L
        timeInMillis = 0L
        timeSwapBuff = 0L
        updateTime = 0L
        lapCount = 1
        binding.tvStopwatch.text = "00:00:000"
        binding.llLaps.removeAllViews()
        binding.btnStartStop.text = "Başlat"
        binding.btnLapReset.text = "Tur"
        binding.btnLapReset.isEnabled = false
    }

    private fun recordLap() {
        val lapTime = binding.tvStopwatch.text.toString()
        val lapTextView = TextView(requireContext()).apply {
            text = "Tur $lapCount: $lapTime"
            textSize = 18f
            setPadding(8, 8, 8, 8)
        }
        binding.llLaps.addView(lapTextView, 0) // Add to the top
        lapCount++
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(runnable)
        _binding = null
    }
}
