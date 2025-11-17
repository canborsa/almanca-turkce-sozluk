package com.tmdbclone.screens

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tmdbclone.auth.AuthViewModel
import com.tmdbclone.navigation.Screen
import com.tmdbclone.screens.detail.DetailScreen
import com.tmdbclone.screens.favorites.FavoritesScreen
import com.tmdbclone.screens.home.HomeScreen
import com.tmdbclone.screens.profile.ProfileScreen
import com.tmdbclone.screens.search.SearchScreen
import com.tmdbclone.screens.watchlist.WatchlistScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    // Share AuthViewModel among screens that need it
    val authViewModel: AuthViewModel = viewModel()

    val items = listOf(
        Screen.Home,
        Screen.Search,
        Screen.Favorites,
        Screen.Watchlist,
        Screen.Profile // Added Profile screen
    )

    val onMovieClick: (Int) -> Unit = { movieId ->
        navController.navigate("detail/$movieId")
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) {
        NavHost(navController, startDestination = Screen.Home.route) {
            composable(Screen.Home.route) {
                HomeScreen(onMovieClick = onMovieClick)
            }
            composable(Screen.Search.route) {
                SearchScreen(onMovieClick = onMovieClick)
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(authViewModel = authViewModel, onMovieClick = onMovieClick)
            }
            composable(Screen.Watchlist.route) {
                WatchlistScreen(authViewModel = authViewModel, onMovieClick = onMovieClick)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(authViewModel = authViewModel)
            }
            composable(
                route = "detail/{movieId}",
                arguments = listOf(navArgument("movieId") { type = NavType.IntType })
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getInt("movieId")
                requireNotNull(movieId) { "Movie ID is required" }
                DetailScreen(movieId = movieId, authViewModel = authViewModel)
            }
        }
    }
}