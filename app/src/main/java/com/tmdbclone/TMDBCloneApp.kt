package com.tmdbclone

import android.app.Application
import com.tmdbclone.auth.SessionManager

class TMDBCloneApp : Application() {
    companion object {
        lateinit var sessionManager: SessionManager
            private set
    }

    override fun onCreate() {
        super.onCreate()
        sessionManager = SessionManager(applicationContext)
    }
}
