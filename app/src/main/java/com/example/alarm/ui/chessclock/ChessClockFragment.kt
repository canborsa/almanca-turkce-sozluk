package com.example.alarm.ui.chessclock

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.alarm.databinding.FragmentChessClockBinding
import java.util.concurrent.TimeUnit

class ChessClockFragment : Fragment() {

    private var _binding: FragmentChessClockBinding? = null
    private val binding get() = _binding!!

    private var player1Timer: CountDownTimer? = null
    private var player2Timer: CountDownTimer? = null

    private var player1TimeLeftInMillis: Long = 600000 // 10 minutes default
    private var player2TimeLeftInMillis: Long = 600000 // 10 minutes default
    private var initialTimeInMillis: Long = 600000

    private var isPlayer1Running = false
    private var isPlayer2Running = false
    private var isGameRunning = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChessClockBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updatePlayer1TimeText()
        updatePlayer2TimeText()

        binding.tvPlayer1Time.setOnClickListener {
            if (isPlayer1Running) {
                pausePlayer1Timer()
                startPlayer2Timer()
            }
        }

        binding.tvPlayer2Time.setOnClickListener {
            if (isPlayer2Running) {
                pausePlayer2Timer()
                startPlayer1Timer()
            }
        }

        binding.btnStartPauseChess.setOnClickListener {
            if (isGameRunning) {
                pauseGame()
            } else {
                startGame()
            }
        }

        binding.btnResetChess.setOnClickListener {
            resetGame()
        }
    }

    private fun startGame() {
        val minutes = binding.etMinutesChess.text.toString().toLongOrNull() ?: (initialTimeInMillis / 60000)
        val seconds = binding.etSecondsChess.text.toString().toLongOrNull() ?: 0
        initialTimeInMillis = (minutes * 60 + seconds) * 1000

        if (initialTimeInMillis == 0L) {
            Toast.makeText(requireContext(), "Lütfen geçerli bir süre girin.", Toast.LENGTH_SHORT).show()
            return
        }

        player1TimeLeftInMillis = initialTimeInMillis
        player2TimeLeftInMillis = initialTimeInMillis
        updatePlayer1TimeText()
        updatePlayer2TimeText()


        isGameRunning = true
        binding.btnStartPauseChess.text = "Durdur"
        binding.llTimeInput.visibility = View.GONE

        // Player 2 starts (or who is not on the move)
        isPlayer2Running = true
        startPlayer2Timer() // Let's say player 2 starts (or you can make it random)
    }

    private fun pauseGame() {
        isGameRunning = false
        binding.btnStartPauseChess.text = "Devam Et"
        pausePlayer1Timer()
        pausePlayer2Timer()
    }

    private fun resetGame(){
         isGameRunning = false
         binding.btnStartPauseChess.text = "Başlat"
         binding.llTimeInput.visibility = View.VISIBLE

         pausePlayer1Timer()
         pausePlayer2Timer()

         initialTimeInMillis =  600000
         player1TimeLeftInMillis = initialTimeInMillis
         player2TimeLeftInMillis = initialTimeInMillis
         updatePlayer1TimeText()
         updatePlayer2TimeText()
    }

    private fun startPlayer1Timer() {
        if (!isGameRunning) return
        isPlayer1Running = true
        player1Timer = object : CountDownTimer(player1TimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                player1TimeLeftInMillis = millisUntilFinished
                updatePlayer1TimeText()
            }

            override fun onFinish() {
                player1TimeLeftInMillis = 0
                updatePlayer1TimeText()
                endGame("Oyuncu 2 Kazandı!")
            }
        }.start()
    }

    private fun startPlayer2Timer() {
        if (!isGameRunning) return
        isPlayer2Running = true
        player2Timer = object : CountDownTimer(player2TimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                player2TimeLeftInMillis = millisUntilFinished
                updatePlayer2TimeText()
            }

            override fun onFinish() {
                player2TimeLeftInMillis = 0
                updatePlayer2TimeText()
                endGame("Oyuncu 1 Kazandı!")
            }
        }.start()
    }

    private fun pausePlayer1Timer() {
        player1Timer?.cancel()
        isPlayer1Running = false
    }

    private fun pausePlayer2Timer() {
        player2Timer?.cancel()
        isPlayer2Running = false
    }

    private fun updatePlayer1TimeText() {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(player1TimeLeftInMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(player1TimeLeftInMillis) - TimeUnit.MINUTES.toSeconds(minutes)
        binding.tvPlayer1Time.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun updatePlayer2TimeText() {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(player2TimeLeftInMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(player2TimeLeftInMillis) - TimeUnit.MINUTES.toSeconds(minutes)
        binding.tvPlayer2Time.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun endGame(message: String) {
        pauseGame()
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player1Timer?.cancel()
        player2Timer?.cancel()
        _binding = null
    }
}
