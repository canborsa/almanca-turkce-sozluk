package com.tmdbclone

import android.app.Application
import dev.convex.android.Convex
import dev.convex.android.ConvexClient
import dev.convex.android.ConvexAuth
import dev.convex.android.authentication.GoogleAuth
import java.net.URL

class TMDBCloneApp : Application() {

    lateinit var convex: ConvexClient
        private set

    override fun onCreate() {
        super.onCreate()

        // =====================================================================================
        // !! IMPORTANT !!
        // Replace this placeholder with your own Convex deployment URL.
        // To get your URL, run `npx convex deploy` in your backend directory.
        // The app will not work without this.
        // =====================================================================================
        val convexUrl = "https://YOUR_CONVEX_URL.convex.cloud"

        val googleAuth = GoogleAuth(this, R.string.default_web_client_id)
        val auth = ConvexAuth(googleAuth)

        this.convex = Convex.create(URL(convexUrl), auth)
    }
}
