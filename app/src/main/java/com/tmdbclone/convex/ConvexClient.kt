package com.tmdbclone.convex

import com.tmdbclone.BuildConfig
import dev.convex.convex.Convex
import dev.convex.convex.ConvexClient

object ConvexClient {
    // Convex URL is now accessed securely from BuildConfig
    val CONVEX_URL: String = BuildConfig.CONVEX_URL

    val client: ConvexClient by lazy {
        Convex.createClient(CONVEX_URL)
    }
}
