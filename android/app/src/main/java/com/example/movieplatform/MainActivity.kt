package com.example.movieplatform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.movieplatform.core.ui.theme.MoviePlatformTheme
import com.example.movieplatform.feature.discover.DiscoverScreen
import com.example.movieplatform.feature.home.HomeScreen
import com.example.movieplatform.feature.movie.MovieDetailScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoviePlatformTheme {
                MainAppContent()
            }
        }
    }
}

@Composable
fun MainAppContent() {
    val navController = rememberNavController()

    Scaffold { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") { HomeScreen() }
            composable("discover") { DiscoverScreen() }
            composable("movie/{id}") { backStackEntry ->
                val movieId = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 1
                MovieDetailScreen(movieId = movieId)
            }
        }
    }
}
