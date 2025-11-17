package com.tmdbclone.auth

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("tmdb_session", Context.MODE_PRIVATE)

    companion object {
        private const val USER_ID_KEY = "user_id_token"
    }

    fun saveSession(userId: String) {
        prefs.edit().putString(USER_ID_KEY, userId).apply()
    }

    fun getUserId(): String? {
        return prefs.getString(USER_ID_KEY, null)
    }

    fun clearSession() {
        prefs.edit().remove(USER_ID_KEY).apply()
    }
}
