package com.assemcorp.cuttingapp.repository

import android.content.Context
import com.assemcorp.cuttingapp.data.Part
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PartRepository(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("parts_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveParts(parts: List<Part>) {
        val json = gson.toJson(parts)
        sharedPreferences.edit().putString("parts_list", json).apply()
    }

    fun loadParts(): List<Part> {
        val json = sharedPreferences.getString("parts_list", null)
        return if (json != null) {
            val type = object : TypeToken<List<Part>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
}
