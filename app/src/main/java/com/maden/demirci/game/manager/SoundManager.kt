package com.maden.demirci.game.manager

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.SoundPool
import kotlin.concurrent.thread
import kotlin.math.sin

class SoundManager(context: Context) {
    private var soundPool: SoundPool? = null
    private var soundPickaxeId: Int = 0
    private var soundAnvilId: Int = 0
    private var soundCoinId: Int = 0

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(8)
            .setAudioAttributes(audioAttributes)
            .build()
    }

    fun playPickaxeHit() {
        playTone(320, 80, 0.9f)
    }

    fun playAnvilHit() {
        // Metallic clink with random pitch (0.95 - 1.05)
        val pitch = (0.95 + Math.random() * 0.10).toFloat()
        playTone((880 * pitch).toInt(), 120, 1.0f)
    }

    fun playCoinSound() {
        playTone(1250, 100, 0.8f)
    }

    private fun playTone(frequencyHz: Int, durationMs: Int, volume: Float) {
        thread {
            try {
                val sampleRate = 22050
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val sample = DoubleArray(numSamples)
                val generatedSnd = ByteArray(2 * numSamples)

                for (i in 0 until numSamples) {
                    sample[i] = sin(2.0 * Math.PI * i.toDouble() / (sampleRate.toDouble() / frequencyHz.toDouble()))
                }

                var idx = 0
                for (dVal in sample) {
                    // Fade out
                    val fadeFactor = (numSamples - idx / 2).toDouble() / numSamples.toDouble()
                    val valShort = (dVal * 32767 * volume * fadeFactor).toInt().toShort()
                    generatedSnd[idx++] = (valShort.toInt() and 0x00ff).toByte()
                    generatedSnd[idx++] = (valShort.toInt() and 0xff00 ushr 8).toByte()
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(generatedSnd.size)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(generatedSnd, 0, generatedSnd.size)
                audioTrack.play()
                Thread.sleep(durationMs.toLong() + 50)
                audioTrack.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}
