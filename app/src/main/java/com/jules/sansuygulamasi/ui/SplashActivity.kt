package com.jules.sansuygulamasi.ui

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jules.sansuygulamasi.MainActivity
import com.jules.sansuygulamasi.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val splashText = findViewById<TextView>(R.id.splash_text)
        val text = "hayatının akışını belirleyecek kartlar açılıyor."
        splashText.text = ""

        val animator = ValueAnimator.ofInt(0, text.length)
        animator.duration = 2000 // 2 saniye
        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            splashText.text = text.substring(0, animatedValue)
        }
        animator.start()

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000) // 3 saniye sonra ana ekrana geç
    }
}
