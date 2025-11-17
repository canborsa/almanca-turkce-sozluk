package com.tmdbclone.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.tmdbclone.ui.navigation.Screen
import com.tmdbclone.ui.screens.detail.MovieDetailScreen
import com.tmdbclone.ui.screens.favorites.FavoritesScreen
import com.tmdbclone.ui.screens.home.HomeScreen
import com.tmdbclone.ui.screens.profile.ProfileScreen
import com.tmdbclone.ui.screens.search.SearchScreen
import com.tmdbclone.ui.screens.watchlist.WatchlistScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val items = listOf(
                    Screen.Home,
                    Screen.Search,
                    Screen.Favorites,
                    Screen.Watchlist,
                    Screen.Profile
                )
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
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Home.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(navController = navController) }
            composable(Screen.Search.route) { SearchScreen(navController = navController) }
            composable(Screen.Favorites.route) { FavoritesScreen(navController = navController) }
            composable(Screen.Watchlist.route) { WatchlistScreen(navController = navController) }
            composable(Screen.Profile.route) { ProfileScreen(navController = navController) }
            composable(
                route = "movie_detail/{movieId}",
                arguments = listOf(navArgument("movieId") { type = NavType.IntType })
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments!!.getInt("movieId")
                MovieDetailScreen(movieId = movieId)
            }
        }
    }
}

@Composable
fun Placeholder(screen: Screen? = null, text: String? = null) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text ?: "${screen!!.title} Screen")
    }
}
