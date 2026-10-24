package com.example.movieplatform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.movieplatform.core.ui.MoviePlatformTheme
import com.example.movieplatform.feature.home.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoviePlatformTheme {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF1E1E1E)) {
                NavigationBarItem(
                    selected = true,
                    onClick = { navController.navigate("home") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Ana Sayfa", tint = Color.White) },
                    label = { Text("Ana Sayfa", color = Color.White) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("discover") },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Keşfet", tint = Color.Gray) },
                    label = { Text("Keşfet", color = Color.Gray) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("diary") },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Günlüğüm", tint = Color.Gray) },
                    label = { Text("Günlüğüm", color = Color.Gray) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("library") },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Kütüphanem", tint = Color.Gray) },
                    label = { Text("Kütüphanem", color = Color.Gray) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("profile") },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil", tint = Color.Gray) },
                    label = { Text("Profil", color = Color.Gray) }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                HomeScreen(onMovieClick = { movieId ->
                    navController.navigate("movie_detail/$movieId")
                })
            }
            composable("discover") {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
                    Text("Keşfet Ekranı", color = Color.White)
                }
            }
            composable("diary") {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
                    Text("Film Günlüğüm Ekranı", color = Color.White)
                }
            }
            composable("library") {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
                    Text("Kütüphanem (Favoriler / Watchlist)", color = Color.White)
                }
            }
            composable("profile") {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
                    Text("Profil Ekranı", color = Color.White)
                }
            }
        }
    }
}
