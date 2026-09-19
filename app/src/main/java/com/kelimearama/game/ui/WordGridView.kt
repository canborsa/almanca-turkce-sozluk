package com.kelimearama.game.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.kelimearama.game.model.GridCell
import kotlin.math.atan2
import kotlin.math.roundToInt

class WordGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    interface OnWordSelectedListener {
        fun onWordSelected(selectedString: String, startRow: Int, startCol: Int, endRow: Int, endCol: Int)
    }

    private var listener: OnWordSelectedListener? = null
    private var grid: Array<Array<GridCell>>? = null
    private var gridSize = 0
    private var cellSize = 0f

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#212121")
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val cellBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E8EAF6")
        style = Paint.Style.FILL
    }

    private val selectedLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#80FFCA28")
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val highlightPaints = listOf(
        "#804CAF50", "#802196F3", "#80FF9800", "#809C27B0", "#80E91E63", "#8000BCD4"
    ).map { hex ->
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor(hex)
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
    }

    // Highlighting permanently found word lines
    data class HighlightLine(
        val startRow: Int, val startCol: Int,
        val endRow: Int, val endCol: Int,
        val colorIndex: Int
    )

    private val permanentHighlights = mutableListOf<HighlightLine>()

    private var touchStartRow = -1
    private var touchStartCol = -1
    private var touchCurrentRow = -1
    private var touchCurrentCol = -1
    private var isSelecting = false

    fun setOnWordSelectedListener(listener: OnWordSelectedListener) {
        this.listener = listener
    }

    fun setGrid(newGrid: Array<Array<GridCell>>) {
        this.grid = newGrid
        this.gridSize = newGrid.size
        this.permanentHighlights.clear()
        requestLayout()
        invalidate()
    }

    fun addHighlight(startRow: Int, startCol: Int, endRow: Int, endCol: Int, colorIndex: Int) {
        permanentHighlights.add(HighlightLine(startRow, startCol, endRow, endCol, colorIndex))
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val size = Math.min(width, height)
        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (gridSize > 0) {
            cellSize = w.toFloat() / gridSize
            textPaint.textSize = cellSize * 0.5f
            selectedLinePaint.strokeWidth = cellSize * 0.7f
            highlightPaints.forEach { it.strokeWidth = cellSize * 0.7f }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val currentGrid = grid ?: return
        if (gridSize == 0 || cellSize == 0f) return

        val padding = 4f
        val cornerRadius = 12f

        // Draw background cells
        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                val left = c * cellSize + padding
                val top = r * cellSize + padding
                val right = (c + 1) * cellSize - padding
                val bottom = (r + 1) * cellSize - padding

                canvas.drawRoundRect(RectF(left, top, right, bottom), cornerRadius, cornerRadius, cellBgPaint)
            }
        }

        // Draw permanent highlight lines
        for (hl in permanentHighlights) {
            val startX = hl.startCol * cellSize + cellSize / 2f
            val startY = hl.startRow * cellSize + cellSize / 2f
            val endX = hl.endCol * cellSize + cellSize / 2f
            val endY = hl.endRow * cellSize + cellSize / 2f
            val paint = highlightPaints[hl.colorIndex % highlightPaints.size]
            canvas.drawLine(startX, startY, endX, endY, paint)
        }

        // Draw current selection line
        if (isSelecting && touchStartRow != -1 && touchCurrentRow != -1) {
            val startX = touchStartCol * cellSize + cellSize / 2f
            val startY = touchStartRow * cellSize + cellSize / 2f
            val endX = touchCurrentCol * cellSize + cellSize / 2f
            val endY = touchCurrentRow * cellSize + cellSize / 2f
            canvas.drawLine(startX, startY, endX, endY, selectedLinePaint)
        }

        // Draw text characters
        val fontMetrics = textPaint.fontMetrics
        val baselineOffset = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom

        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                val cell = currentGrid[r][c]
                val centerX = c * cellSize + cellSize / 2f
                val centerY = r * cellSize + cellSize / 2f + baselineOffset
                canvas.drawText(cell.letter.toString(), centerX, centerY, textPaint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (grid == null || gridSize == 0) return false

        val col = (event.x / cellSize).toInt().coerceIn(0, gridSize - 1)
        val row = (event.y / cellSize).toInt().coerceIn(0, gridSize - 1)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isSelecting = true
                touchStartRow = row
                touchStartCol = col
                touchCurrentRow = row
                touchCurrentCol = col
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (isSelecting) {
                    val constrained = constrainToDirection(touchStartRow, touchStartCol, row, col)
                    touchCurrentRow = constrained.first
                    touchCurrentCol = constrained.second
                    invalidate()
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (isSelecting) {
                    isSelecting = false
                    val constrained = constrainToDirection(touchStartRow, touchStartCol, row, col)
                    val endR = constrained.first
                    val endC = constrained.second

                    val selectedWord = getSelectedString(touchStartRow, touchStartCol, endR, endC)
                    if (selectedWord.isNotEmpty()) {
                        listener?.onWordSelected(selectedWord, touchStartRow, touchStartCol, endR, endC)
                    }

                    touchStartRow = -1
                    touchStartCol = -1
                    touchCurrentRow = -1
                    touchCurrentCol = -1
                    invalidate()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun constrainToDirection(startR: Int, startC: Int, curR: Int, curC: Int): Pair<Int, Int> {
        val dR = curR - startR
        val dC = curC - startC
        if (dR == 0 && dC == 0) return Pair(startR, startC)

        // Snap to nearest 45 degree direction
        val angle = Math.toDegrees(atan2(dR.toDouble(), dC.toDouble()))
        val octant = ((angle + 360 + 22.5) % 360 / 45).toInt()

        val steps = Math.max(Math.abs(dR), Math.abs(dC))

        return when (octant) {
            0 -> Pair(startR, startC + steps)                 // Right
            1 -> Pair(startR + steps, startC + steps)         // Down-Right
            2 -> Pair(startR + steps, startC)                 // Down
            3 -> Pair(startR + steps, startC - steps)         // Down-Left
            4 -> Pair(startR, startC - steps)                 // Left
            5 -> Pair(startR - steps, startC - steps)         // Up-Left
            6 -> Pair(startR - steps, startC)                 // Up
            7 -> Pair(startR - steps, startC + steps)         // Up-Right
            else -> Pair(curR, curC)
        }.let { (r, c) ->
            Pair(r.coerceIn(0, gridSize - 1), c.coerceIn(0, gridSize - 1))
        }
    }

    private fun getSelectedString(startR: Int, startC: Int, endR: Int, endC: Int): String {
        val currentGrid = grid ?: return ""
        val dR = Integer.signum(endR - startR)
        val dC = Integer.signum(endC - startC)

        if (dR == 0 && dC == 0) return currentGrid[startR][startC].letter.toString()

        val steps = Math.max(Math.abs(endR - startR), Math.abs(endC - startC))
        val sb = StringBuilder()

        var r = startR
        var c = startC
        for (i in 0..steps) {
            if (r in 0 until gridSize && c in 0 until gridSize) {
                sb.append(currentGrid[r][c].letter)
            }
            r += dR
            c += dC
        }
        return sb.toString()
    }
}
