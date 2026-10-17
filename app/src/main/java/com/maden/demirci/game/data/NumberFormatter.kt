package com.maden.demirci.game.data

import java.util.Locale

object NumberFormatter {
    fun format(value: Double): String {
        if (value < 0) return "0"
        return when {
            value >= 1_000_000_000_000.0 -> String.format(Locale.US, "%.2fT", value / 1_000_000_000_000.0)
            value >= 1_000_000_000.0 -> String.format(Locale.US, "%.2fB", value / 1_000_000_000.0)
            value >= 1_000_000.0 -> String.format(Locale.US, "%.2fM", value / 1_000_000.0)
            value >= 1_000.0 -> String.format(Locale.US, "%.2fK", value / 1_000.0)
            else -> String.format(Locale.US, "%.0f", value)
        }
    }

    fun formatLong(value: Long): String {
        return format(value.toDouble())
    }
}
