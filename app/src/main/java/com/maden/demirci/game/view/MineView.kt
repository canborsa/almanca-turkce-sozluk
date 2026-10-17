package com.maden.demirci.game.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.maden.demirci.game.data.GameManager
import kotlin.math.cos
import kotlin.math.sin

class MineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var onMineClickListener: (() -> Unit)? = null

    private val rockPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val rockBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 8f
        color = Color.parseColor("#3E2723")
    }
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1C1613") }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 42f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }
    private val particlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val pickaxePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 12f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val minerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

    private var rockScale = 1.0f
    private var shakeOffsetX = 0f
    private var shakeOffsetY = 0f
    private var pickaxeAngle = -45f

    private class Particle(
        var x: Float,
        var y: Float,
        var vx: Float,
        var vy: Float,
        var color: Int,
        var radius: Float,
        var alpha: Int = 255
    )

    private val particles = mutableListOf<Particle>()

    init {
        // Animation ticker for particles and shake
        postInvalidateOnAnimation()
    }

    fun triggerHitAnimation(isCrit: Boolean, layer: Int) {
        // Rock scale bounce
        val scaleAnim = ValueAnimator.ofFloat(0.88f, 1.0f)
        scaleAnim.duration = 150
        scaleAnim.addUpdateListener {
            rockScale = it.animatedValue as Float
            invalidate()
        }
        scaleAnim.start()

        // Pickaxe swing animation
        val swingAnim = ValueAnimator.ofFloat(30f, -45f)
        swingAnim.duration = 180
        swingAnim.addUpdateListener {
            pickaxeAngle = it.animatedValue as Float
            invalidate()
        }
        swingAnim.start()

        // Screen shake
        val shakeAnim = ValueAnimator.ofFloat(15f, 0f)
        shakeAnim.duration = 150
        shakeAnim.addUpdateListener {
            val amount = it.animatedValue as Float
            shakeOffsetX = ((Math.random() - 0.5) * amount * 2).toFloat()
            shakeOffsetY = ((Math.random() - 0.5) * amount * 2).toFloat()
            invalidate()
        }
        shakeAnim.start()

        // Particles
        val particleColor = when (layer) {
            2 -> Color.parseColor("#E0E0E0") // Silver
            3 -> Color.parseColor("#FFD700") // Gold
            else -> Color.parseColor("#CD7F32") // Copper
        }

        val centerX = width / 2f
        val centerY = height / 2f + 20f
        val count = if (isCrit) 25 else 12

        for (i in 0 until count) {
            val angle = Math.random() * Math.PI * 2
            val speed = 8f + Math.random().toFloat() * 16f
            val vx = (cos(angle) * speed).toFloat()
            val vy = (sin(angle) * speed - 5f).toFloat()
            val pColor = if (Math.random() < 0.3) Color.YELLOW else particleColor
            particles.add(Particle(centerX, centerY, vx, vy, pColor, 6f + Math.random().toFloat() * 8f))
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            onMineClickListener?.invoke()
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0 || h <= 0) return

        canvas.save()
        canvas.translate(shakeOffsetX, shakeOffsetY)

        // 1. Background (Cave texture)
        canvas.drawRect(0f, 0f, w, h, bgPaint)

        // Draw cave background arch / lines
        val caveArchPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#2A211D")
            style = Paint.Style.STROKE
            strokeWidth = 20f
        }
        canvas.drawOval(-50f, 50f, w + 50f, h + 100f, caveArchPaint)

        val manager = GameManager.getInstance()
        val layer = manager.getCurrentLayer()

        // 2. Depth Badge
        val layerName = when (layer) {
            1 -> "Katman 1: Bakır Damarı"
            2 -> "Katman 2: Gümüş Damarı"
            else -> "Katman 3: Saf Altın Damarı"
        }
        textPaint.color = Color.WHITE
        textPaint.textSize = 38f
        canvas.drawText("Derinlik: ${manager.depthMeters}m - $layerName", w / 2f, 60f, textPaint)

        // 3. Central Ore Rock
        val centerX = w / 2f
        val centerY = h / 2f + 30f
        val rockRadius = 120f * rockScale

        rockPaint.color = when (layer) {
            2 -> Color.parseColor("#9E9E9E") // Silver rock
            3 -> Color.parseColor("#FFC107") // Gold rock
            else -> Color.parseColor("#A1887F") // Copper rock
        }

        val rockPath = Path()
        rockPath.moveTo(centerX - rockRadius, centerY)
        rockPath.lineTo(centerX - rockRadius * 0.6f, centerY - rockRadius * 0.9f)
        rockPath.lineTo(centerX + rockRadius * 0.5f, centerY - rockRadius * 0.8f)
        rockPath.lineTo(centerX + rockRadius, centerY)
        rockPath.lineTo(centerX + rockRadius * 0.8f, centerY + rockRadius * 0.8f)
        rockPath.lineTo(centerX - rockRadius * 0.7f, centerY + rockRadius * 0.9f)
        rockPath.close()

        canvas.drawPath(rockPath, rockPaint)
        canvas.drawPath(rockPath, rockBorderPaint)

        // Shiny veins on the rock
        val veinPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = when (layer) {
                2 -> Color.WHITE
                3 -> Color.parseColor("#FFF59D")
                else -> Color.parseColor("#FFAB91")
            }
            style = Paint.Style.FILL
        }
        canvas.drawCircle(centerX - rockRadius * 0.3f, centerY - rockRadius * 0.2f, 16f * rockScale, veinPaint)
        canvas.drawCircle(centerX + rockRadius * 0.2f, centerY + rockRadius * 0.3f, 22f * rockScale, veinPaint)
        canvas.drawCircle(centerX, centerY - rockRadius * 0.4f, 12f * rockScale, veinPaint)

        // 4. Animated Pickaxe
        canvas.save()
        canvas.translate(centerX + 60f, centerY - 80f)
        canvas.rotate(pickaxeAngle)

        // Handle
        pickaxePaint.color = Color.parseColor("#795548")
        pickaxePaint.strokeWidth = 14f
        canvas.drawLine(0f, 0f, -80f, 80f, pickaxePaint)

        // Blade
        pickaxePaint.color = Color.parseColor("#CFD8DC")
        pickaxePaint.strokeWidth = 18f
        canvas.drawLine(-30f, -20f, 30f, 20f, pickaxePaint)
        canvas.restore()

        // 5. Active Miners around the rock
        for (miner in manager.miners) {
            if (miner.level > 0) {
                val angle = (miner.id * 70 - 140).toDouble()
                val rad = Math.toRadians(angle)
                val mx = centerX + cos(rad).toFloat() * 200f
                val my = centerY + sin(rad).toFloat() * 120f + 60f

                // Dwarf Miner sprite drawing
                minerPaint.color = Color.parseColor("#3F51B5") // Clothes
                canvas.drawCircle(mx, my, 22f, minerPaint)

                minerPaint.color = Color.parseColor("#FFCC80") // Face
                canvas.drawCircle(mx, my - 18f, 14f, minerPaint)

                // Helmet (Upgraded if level > 3)
                minerPaint.color = if (miner.level >= 4) Color.parseColor("#FFD700") else Color.parseColor("#795548")
                canvas.drawRect(mx - 16f, my - 36f, mx + 16f, my - 26f, minerPaint)

                // Miner level text
                textPaint.textSize = 22f
                textPaint.color = Color.YELLOW
                canvas.drawText("Lv.${miner.level}", mx, my + 40f, textPaint)
            }
        }

        // 6. Particle Updates & Draw
        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val p = iterator.next()
            p.x += p.vx
            p.y += p.vy
            p.vy += 0.8f // Gravity
            p.alpha -= 10
            if (p.alpha <= 0) {
                iterator.remove()
            } else {
                particlePaint.color = p.color
                particlePaint.alpha = p.alpha
                canvas.drawCircle(p.x, p.y, p.radius, particlePaint)
            }
        }

        canvas.restore()

        if (particles.isNotEmpty()) {
            postInvalidateOnAnimation()
        }
    }
}
