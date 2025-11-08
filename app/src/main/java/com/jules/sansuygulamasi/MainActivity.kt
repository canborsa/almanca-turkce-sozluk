package com.jules.sansuygulamasi

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.jules.sansuygulamasi.data.Advices
import com.jules.sansuygulamasi.databinding.ActivityMainBinding
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val cards by lazy { listOf(binding.card1, binding.card2, binding.card3, binding.card4) }
    private var isAnimating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cards.forEach { card ->
            card.setOnClickListener {
                if (!isAnimating) {
                    flipCard(it as ImageView)
                }
            }
        }

        binding.adviceText.setOnClickListener {
            if (!isAnimating) {
                resetGame()
            }
        }
    }

    private fun flipCard(card: ImageView) {
        isAnimating = true

        val oa1 = ObjectAnimator.ofFloat(card, "rotationY", 0f, 90f)
        oa1.interpolator = DecelerateInterpolator()
        oa1.duration = 200

        oa1.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                // Hide all cards after the selected one has turned halfway
                cards.forEach { it.visibility = View.INVISIBLE }
                showAdviceAndConfetti()
            }
        })

        oa1.start()
    }

    private fun showAdviceAndConfetti() {
        binding.adviceText.text = Advices.getRandomAdvice()
        binding.adviceText.visibility = View.VISIBLE

        val party = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
            position = Position.Relative(0.5, 0.3)
        )
        binding.konfettiView.start(party)
        isAnimating = false // Animation is complete
    }

    private fun resetGame() {
        binding.adviceText.visibility = View.GONE
        cards.forEach { card ->
            card.visibility = View.VISIBLE
            card.rotationY = 0f
        }
    }
}
