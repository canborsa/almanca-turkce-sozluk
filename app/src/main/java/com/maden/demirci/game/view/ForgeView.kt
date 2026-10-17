package com.maden.demirci.game.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.maden.demirci.game.data.GameManager

class ForgeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var onForgeClickListener: (() -> Unit)? = null

    private val anvilPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#424242") }
    private val anvilBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        color = Color.parseColor("#212121")
    }
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#21130D") }
    private val firePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 38f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }
    private val hammerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 16f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val sparkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

    private var squashY = 1.0f
    private var stretchX = 1.0f
    private var hammerAngle = -50f

    private class Spark(
        var x: Float,
        var y: Float,
        var vx: Float,
        var vy: Float,
        var alpha: Int = 255
    )

    private val sparks = mutableListOf<Spark>()

    init {
        postInvalidateOnAnimation()
    }

    fun triggerHammerHit() {
        // Squash and Stretch on anvil/ingot
        val squashAnim = ValueAnimator.ofFloat(0.7f, 1.0f)
        squashAnim.duration = 160
        squashAnim.addUpdateListener {
            squashY = it.animatedValue as Float
            stretchX = 1.0f + (1.0f - squashY) * 0.5f
            invalidate()
        }
        squashAnim.start()

        // Hammer swing
        val hammerAnim = ValueAnimator.ofFloat(20f, -50f)
        hammerAnim.duration = 180
        hammerAnim.addUpdateListener {
            hammerAngle = it.animatedValue as Float
            invalidate()
        }
        hammerAnim.start()

        // Create sparks
        val centerX = width / 2f
        val centerY = height / 2f + 10f
        for (i in 0 until 18) {
            val vx = ((Math.random() - 0.5) * 20f).toFloat()
            val vy = (-Math.random() * 18f - 4f).toFloat()
            sparks.add(Spark(centerX, centerY, vx, vy))
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            onForgeClickListener?.invoke()
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0 || h <= 0) return

        // 1. Background (Forge Hearth)
        canvas.drawRect(0f, 0f, w, h, bgPaint)

        // Fire glowing background arch
        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#E65100")
            alpha = 100
        }
        canvas.drawCircle(w / 2f, h / 2f + 50f, 220f, glowPaint)

        val manager = GameManager.getInstance()

        // 2. Status Label
        textPaint.color = Color.WHITE
        canvas.drawText("Demirci Atölyesi - Örs & Çekiç", w / 2f, 60f, textPaint)

        val forgeMultiplierText = String.format("Dönüşüm Çarpanı: x%.1f", 1.0 + (manager.forgeLevel - 1) * 0.2)
        textPaint.color = Color.YELLOW
        textPaint.textSize = 32f
        canvas.drawText(forgeMultiplierText, w / 2f, 105f, textPaint)

        val centerX = w / 2f
        val centerY = h / 2f + 80f

        // 3. Anvil Stand and Anvil
        canvas.save()
        // Base / Stand
        val standPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#5D4037") }
        canvas.drawRect(centerX - 80f, centerY + 60f, centerX + 80f, centerY + 160f, standPaint)

        // Anvil body with squash/stretch
        canvas.scale(stretchX, squashY, centerX, centerY + 40f)

        val anvilPath = Path()
        anvilPath.moveTo(centerX - 120f, centerY)
        anvilPath.lineTo(centerX + 140f, centerY) // Horn
        anvilPath.lineTo(centerX + 100f, centerY + 30f)
        anvilPath.lineTo(centerX + 50f, centerY + 40f)
        anvilPath.lineTo(centerX + 60f, centerY + 80f)
        anvilPath.lineTo(centerX - 60f, centerY + 80f)
        anvilPath.lineTo(centerX - 50f, centerY + 40f)
        anvilPath.lineTo(centerX - 120f, centerY + 40f)
        anvilPath.close()

        canvas.drawPath(anvilPath, anvilPaint)
        canvas.drawPath(anvilPath, anvilBorderPaint)

        // Glowing Hot Ingot on top of Anvil
        val ingotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FF3D00") // Red hot glowing
        }
        val ingotRect = RectF(centerX - 40f, centerY - 22f, centerX + 40f, centerY)
        canvas.drawRoundRect(ingotRect, 8f, 8f, ingotPaint)

        val innerGlow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FFD600")
        }
        canvas.drawRoundRect(RectF(centerX - 25f, centerY - 18f, centerX + 25f, centerY - 4f), 4f, 4f, innerGlow)
        canvas.restore()

        // 4. Hammer
        canvas.save()
        canvas.translate(centerX + 50f, centerY - 60f)
        canvas.rotate(hammerAngle)

        // Handle
        hammerPaint.color = Color.parseColor("#8D6E63")
        hammerPaint.strokeWidth = 14f
        canvas.drawLine(0f, 0f, 0f, -90f, hammerPaint)

        // Hammer Head
        val headPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#37474F") }
        canvas.drawRect(-35f, -120f, 35f, -90f, headPaint)
        canvas.restore()

        // 5. Sparks particle effects
        val iterator = sparks.iterator()
        while (iterator.hasNext()) {
            val spark = iterator.next()
            spark.x += spark.vx
            spark.y += spark.vy
            spark.vy += 0.9f // Gravity
            spark.alpha -= 12
            if (spark.alpha <= 0) {
                iterator.remove()
            } else {
                sparkPaint.color = Color.parseColor("#FFD54F")
                sparkPaint.alpha = spark.alpha
                canvas.drawCircle(spark.x, spark.y, 5f, sparkPaint)
            }
        }

        if (sparks.isNotEmpty()) {
            postInvalidateOnAnimation()
        }
    }
}
