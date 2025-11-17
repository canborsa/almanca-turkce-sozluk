package com.tmdbclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tmdbclone.auth.AuthViewModel
import com.tmdbclone.navigation.authNavGraph
import com.tmdbclone.screens.MainScreen
import com.tmdbclone.ui.theme.TMDBCloneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TMDBCloneTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val authState = authViewModel.uiState

    // This effect will run once when isAuthenticated changes.
    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated) {
            navController.navigate("main") {
                // Clear the auth back stack so the user can't go back to the login screen.
                popUpTo("auth") { inclusive = true }
            }
        } else {
             // Optional: If you want to navigate back to login when user signs out
             // from the main app, you can add that logic here.
             // For now, we only handle the initial navigation.
        }
    }

    NavHost(
        navController = navController,
        startDestination = "auth" // Always start at auth, LaunchedEffect will redirect if needed
    ) {
        // Authentication flow
        authNavGraph(navController, authViewModel)

        // Main app flow
        composable("main") {
            MainScreen()
        }
    }
}
