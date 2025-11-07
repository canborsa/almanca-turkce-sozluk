package com.example.alarm.ui.timer

import android.content.Context
import android.media.RingtoneManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Vibrator
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.alarm.databinding.FragmentTimerBinding
import java.util.concurrent.TimeUnit

class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0
    private var isTimerRunning: Boolean = false
    private var endTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStartPause.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        binding.btnReset.setOnClickListener {
            resetTimer()
        }
    }

    private fun startTimer() {
        val minutes = binding.etMinutes.text.toString().toLongOrNull() ?: 0
        val seconds = binding.etSeconds.text.toString().toLongOrNull() ?: 0

        if(timeLeftInMillis == 0L){
             val totalSeconds = minutes * 60 + seconds
              if (totalSeconds == 0L) {
                Toast.makeText(requireContext(), "Lütfen geçerli bir süre girin.", Toast.LENGTH_SHORT).show()
                return
            }
            timeLeftInMillis = totalSeconds * 1000
        }

        endTime = System.currentTimeMillis() + timeLeftInMillis

        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountdownText()
            }

            override fun onFinish() {
                isTimerRunning = false
                updateButtons()
                // Play sound and vibrate
                val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(1000)

                val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val ringtone = RingtoneManager.getRingtone(requireContext(), notificationUri)
                ringtone.play()
            }
        }.start()

        isTimerRunning = true
        updateButtons()
        binding.etMinutes.visibility = View.INVISIBLE
        binding.etSeconds.visibility = View.INVISIBLE
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        updateButtons()
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        timeLeftInMillis = 0
        isTimerRunning = false
        updateCountdownText()
        updateButtons()
        binding.etMinutes.visibility = View.VISIBLE
        binding.etSeconds.visibility = View.VISIBLE
        binding.etMinutes.text.clear()
        binding.etSeconds.text.clear()
    }

    private fun updateCountdownText() {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeftInMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeftInMillis) - TimeUnit.MINUTES.toSeconds(minutes)
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
        binding.tvCountdown.text = timeFormatted
    }

    private fun updateButtons() {
        if (isTimerRunning) {
            binding.btnStartPause.text = "Duraklat"
            binding.btnReset.visibility = View.INVISIBLE
        } else {
            binding.btnStartPause.text = "Başlat"
            binding.btnReset.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        _binding = null
    }
}