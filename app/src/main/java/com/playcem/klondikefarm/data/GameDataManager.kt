package com.playcem.klondikefarm.data

import android.content.Context
import android.content.SharedPreferences

class GameDataManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("klondike_farm_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_NICKNAME = "nickname"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_COINS = "coins"
        private const val KEY_ENERGY = "energy"
        private const val KEY_LEVEL = "level"
        private const val KEY_XP = "xp"
        private const val KEY_WHEAT = "wheat"
        private const val KEY_MILK = "milk"

        // Cow state: 0 = Hungry, 1 = Eating/Producing, 2 = Ready to Milk
        private const val KEY_COW_STATE = "cow_state"
        private const val KEY_COW_FINISH_TIME = "cow_finish_time"

        // Truck state: 0 = Idle, 1 = On Delivery, 2 = Order Ready
        private const val KEY_TRUCK_STATE = "truck_state"
    }

    var nickname: String
        get() = prefs.getString(KEY_NICKNAME, "Farmer") ?: "Farmer"
        set(value) = prefs.edit().putString(KEY_NICKNAME, value).apply()

    var language: String
        get() = prefs.getString(KEY_LANGUAGE, "tr") ?: "tr"
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).apply()

    var coins: Int
        get() = prefs.getInt(KEY_COINS, 100)
        set(value) = prefs.edit().putInt(KEY_COINS, value).apply()

    var energy: Int
        get() = prefs.getInt(KEY_ENERGY, 20)
        set(value) = prefs.edit().putInt(KEY_ENERGY, value).apply()

    var level: Int
        get() = prefs.getInt(KEY_LEVEL, 1)
        set(value) = prefs.edit().putInt(KEY_LEVEL, value).apply()

    var xp: Int
        get() = prefs.getInt(KEY_XP, 0)
        set(value) = prefs.edit().putInt(KEY_XP, value).apply()

    var wheatCount: Int
        get() = prefs.getInt(KEY_WHEAT, 5)
        set(value) = prefs.edit().putInt(KEY_WHEAT, value).apply()

    var milkCount: Int
        get() = prefs.getInt(KEY_MILK, 2)
        set(value) = prefs.edit().putInt(KEY_MILK, value).apply()

    fun getPlotState(index: Int): Int {
        val rawState = prefs.getInt("plot_${index}_state", 0)
        if (rawState == 1) { // Growing
            val finishTime = prefs.getLong("plot_${index}_finish_time", 0L)
            if (System.currentTimeMillis() >= finishTime) {
                setPlotState(index, 2)
                return 2
            }
        }
        return rawState
    }

    fun setPlotState(index: Int, state: Int, finishTime: Long = 0L) {
        prefs.edit()
            .putInt("plot_${index}_state", state)
            .putLong("plot_${index}_finish_time", finishTime)
            .apply()
    }

    fun getPlotFinishTime(index: Int): Long {
        return prefs.getLong("plot_${index}_finish_time", 0L)
    }

    var cowState: Int
        get() {
            val rawState = prefs.getInt(KEY_COW_STATE, 0)
            if (rawState == 1) {
                val finishTime = prefs.getLong(KEY_COW_FINISH_TIME, 0L)
                if (System.currentTimeMillis() >= finishTime) {
                    cowState = 2
                    return 2
                }
            }
            return rawState
        }
        set(value) = prefs.edit().putInt(KEY_COW_STATE, value).apply()

    fun setCowStateWithTimer(state: Int, finishTime: Long = 0L) {
        prefs.edit()
            .putInt(KEY_COW_STATE, state)
            .putLong(KEY_COW_FINISH_TIME, finishTime)
            .apply()
    }

    fun getCowFinishTime(): Long {
        return prefs.getLong(KEY_COW_FINISH_TIME, 0L)
    }

    var truckState: Int
        get() = prefs.getInt(KEY_TRUCK_STATE, 0)
        set(value) = prefs.edit().putInt(KEY_TRUCK_STATE, value).apply()

    fun addXp(amount: Int): Boolean {
        var currentXp = xp + amount
        val neededXp = level * 50
        var leveledUp = false
        if (currentXp >= neededXp) {
            currentXp -= neededXp
            level += 1
            energy += 10 // Bonus energy on level up
            leveledUp = true
        }
        xp = currentXp
        return leveledUp
    }
}
