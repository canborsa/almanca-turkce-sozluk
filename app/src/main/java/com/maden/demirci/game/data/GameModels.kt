package com.maden.demirci.game.data

enum class OreType(val displayName: String, val coinValue: Double) {
    COPPER("Bakır", 1.0),
    SILVER("Gümüş", 10.0),
    GOLD("Ham Altın", 100.0)
}

data class WorkerState(
    val id: Int,
    val name: String,
    val title: String,
    var level: Int = 0, // 0 = Unlocked/Not bought, 1..5 = Active level
    val unlockRequirementText: String,
    val baseCost: Double
) {
    fun getUpgradeCost(): Double {
        val current = if (level == 0) 0 else level
        return baseCost * Math.pow(1.18, current.toDouble())
    }

    // Speed multiplier or rate based on level
    fun getSpeedMultiplier(): Double {
        return when (level) {
            1 -> 1.0
            2 -> 1.5
            3 -> 2.2
            4 -> 3.2
            5 -> 5.0
            else -> 0.0
        }
    }

    fun getDoubleChance(): Double {
        return when (level) {
            2 -> 0.10
            3 -> 0.25
            else -> 0.0
        }
    }

    fun getCritChance(): Double {
        return when (level) {
            4 -> 0.15
            else -> 0.0
        }
    }

    fun isMaxLevel(): Boolean = level >= 5
}

data class CarrierState(
    val id: Int,
    val name: String,
    var level: Int = 0,
    val baseCost: Double
) {
    fun getUpgradeCost(): Double {
        val current = if (level == 0) 0 else level
        return baseCost * Math.pow(1.18, current.toDouble())
    }

    fun getCapacity(): Long {
        if (id == 1) {
            return when (level) {
                1 -> 5L
                2 -> 12L
                3 -> 25L
                4 -> 50L
                5 -> 120L
                else -> 0L
            }
        } else {
            return when (level) {
                1 -> 150L
                2 -> 300L
                3 -> 600L
                4 -> 1200L
                5 -> 3000L
                else -> 0L
            }
        }
    }

    fun getIntervalSeconds(): Double {
        if (id == 1) {
            return when (level) {
                1 -> 3.0
                2 -> 2.5
                3 -> 2.0
                4 -> 1.5
                5 -> 1.0
                else -> 3.0
            }
        } else {
            return when (level) {
                1 -> 2.0
                2 -> 1.8
                3 -> 1.5
                4 -> 1.2
                5 -> 1.0
                else -> 2.0
            }
        }
    }

    fun isMaxLevel(): Boolean = level >= 5
}
