package com.maden.demirci.game.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

class SaveSystem(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("maden_demirci_prefs", Context.MODE_PRIVATE)

    fun saveGame(manager: GameManager) {
        val json = JSONObject()
        json.put("coins", manager.coins)
        json.put("copper", manager.copper)
        json.put("silver", manager.silver)
        json.put("gold", manager.gold)
        json.put("processedCopper", manager.processedCopper)
        json.put("processedSilver", manager.processedSilver)

        json.put("pickaxeLevel", manager.pickaxeLevel)
        json.put("warehouseLevel", manager.warehouseLevel)
        json.put("forgeLevel", manager.forgeLevel)
        json.put("depthMeters", manager.depthMeters)
        json.put("lastSaveTime", System.currentTimeMillis())

        val minersArr = JSONArray()
        for (miner in manager.miners) {
            val obj = JSONObject()
            obj.put("id", miner.id)
            obj.put("level", miner.level)
            minersArr.put(obj)
        }
        json.put("miners", minersArr)

        val carriersArr = JSONArray()
        for (carrier in manager.carriers) {
            val obj = JSONObject()
            obj.put("id", carrier.id)
            obj.put("level", carrier.level)
            carriersArr.put(obj)
        }
        json.put("carriers", carriersArr)

        prefs.edit().putString("save_data", json.toString()).apply()
    }

    fun loadGame(manager: GameManager): Long {
        val jsonStr = prefs.getString("save_data", null) ?: return 0L
        try {
            val json = JSONObject(jsonStr)
            manager.coins = json.optDouble("coins", 0.0)
            manager.copper = json.optLong("copper", 0L)
            manager.silver = json.optLong("silver", 0L)
            manager.gold = json.optLong("gold", 0L)
            manager.processedCopper = json.optLong("processedCopper", 0L)
            manager.processedSilver = json.optLong("processedSilver", 0L)

            manager.pickaxeLevel = json.optInt("pickaxeLevel", 1)
            manager.warehouseLevel = json.optInt("warehouseLevel", 1)
            manager.forgeLevel = json.optInt("forgeLevel", 1)
            manager.depthMeters = json.optInt("depthMeters", 0)

            val minersArr = json.optJSONArray("miners")
            if (minersArr != null) {
                for (i in 0 until minersArr.length()) {
                    val obj = minersArr.getJSONObject(i)
                    val id = obj.getInt("id")
                    val level = obj.getInt("level")
                    manager.miners.find { it.id == id }?.level = level
                }
            }

            val carriersArr = json.optJSONArray("carriers")
            if (carriersArr != null) {
                for (i in 0 until carriersArr.length()) {
                    val obj = carriersArr.getJSONObject(i)
                    val id = obj.getInt("id")
                    val level = obj.getInt("level")
                    manager.carriers.find { it.id == id }?.level = level
                }
            }

            return json.optLong("lastSaveTime", System.currentTimeMillis())
        } catch (e: Exception) {
            e.printStackTrace()
            return 0L
        }
    }
}
