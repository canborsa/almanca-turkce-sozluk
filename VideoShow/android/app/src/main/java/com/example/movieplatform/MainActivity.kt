package com.example.movieplatform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.movieplatform.core.ui.theme.DarkBackground
import com.example.movieplatform.core.ui.theme.PrimaryGold
import com.example.movieplatform.core.ui.theme.VideoShowTheme
import com.example.movieplatform.feature.auth.AuthScreen
import com.example.movieplatform.feature.diary.DiaryScreen
import com.example.movieplatform.feature.discover.DiscoverScreen
import com.example.movieplatform.feature.home.HomeScreen
import com.example.movieplatform.feature.library.LibraryScreen
import com.example.movieplatform.feature.movie.MovieDetailScreen
import com.example.movieplatform.feature.person.PersonDetailScreen
import com.example.movieplatform.feature.profile.ProfileScreen
import com.example.movieplatform.feature.search.SearchScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoShowTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        if (currentRoute in listOf("home", "discover", "diary", "library", "profile")) {
                            NavigationBar(containerColor = DarkBackground) {
                                NavigationBarItem(
                                    selected = currentRoute == "home",
                                    onClick = { navController.navigate("home") },
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Ana Sayfa") },
                                    label = { Text("Ana Sayfa") },
                                    colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryGold, selectedTextColor = PrimaryGold, unselectedIconColor = Color.Gray)
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "discover",
                                    onClick = { navController.navigate("discover") },
                                    icon = { Icon(Icons.Default.Search, contentDescription = "Keşfet") },
                                    label = { Text("Keşfet") },
                                    colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryGold, selectedTextColor = PrimaryGold, unselectedIconColor = Color.Gray)
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "diary",
                                    onClick = { navController.navigate("diary") },
                                    icon = { Icon(Icons.Default.AddCircle, contentDescription = "Günlüğüm") },
                                    label = { Text("Günlüğüm") },
                                    colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryGold, selectedTextColor = PrimaryGold, unselectedIconColor = Color.Gray)
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "library",
                                    onClick = { navController.navigate("library") },
                                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Kütüphanem") },
                                    label = { Text("Kütüphanem") },
                                    colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryGold, selectedTextColor = PrimaryGold, unselectedIconColor = Color.Gray)
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "profile",
                                    onClick = { navController.navigate("profile") },
                                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                                    label = { Text("Profil") },
                                    colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryGold, selectedTextColor = PrimaryGold, unselectedIconColor = Color.Gray)
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") { HomeScreen(onMovieClick = { id -> navController.navigate("movie_detail/$id") }) }
                        composable("discover") { DiscoverScreen(onMovieClick = { id -> navController.navigate("movie_detail/$id") }) }
                        composable("search") { SearchScreen(onMovieClick = { id -> navController.navigate("movie_detail/$id") }) }
                        composable("diary") { DiaryScreen() }
                        composable("library") { LibraryScreen(onMovieClick = { id -> navController.navigate("movie_detail/$id") }) }
                        composable("profile") { ProfileScreen(onLogout = { navController.navigate("auth") }) }
                        composable("auth") { AuthScreen(onLoginSuccess = { navController.navigate("home") }) }
                        composable("movie_detail/{id}") { backStackEntry ->
                            val movieId = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
                            MovieDetailScreen(movieId = movieId, onBackClick = { navController.popBackStack() })
                        }
                        composable("person_detail/{id}") { backStackEntry ->
                            val personId = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
                            PersonDetailScreen(personId = personId, onBackClick = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
