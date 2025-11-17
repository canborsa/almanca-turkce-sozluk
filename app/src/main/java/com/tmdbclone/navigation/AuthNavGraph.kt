package com.tmdbclone.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.tmdbclone.auth.AuthViewModel
import com.tmdbclone.screens.auth.LoginScreen
import com.tmdbclone.screens.auth.SignUpScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    navigation(
        startDestination = "login",
        route = "auth"
    ) {
        composable("login") {
            LoginScreen(
                onLoginClick = { email, password ->
                    authViewModel.logIn(email, password)
                },
                onSignUpClick = {
                    navController.navigate("signup")
                },
                errorMessage = authViewModel.uiState.errorMessage
            )
        }
        composable("signup") {
            SignUpScreen(
                onSignUpClick = { name, email, password ->
                    authViewModel.signUp(name, email, password)
                },
                onLoginClick = {
                    navController.popBackStack() // Go back to login
                },
                errorMessage = authViewModel.uiState.errorMessage
            )
        }
    }
}
