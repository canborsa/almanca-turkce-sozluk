package com.tmdbclone.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tmdbclone.auth.AuthViewModel
import com.tmdbclone.di.ViewModelFactory

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel // Get the shared AuthViewModel instance
) {
    // Create the ProfileViewModel using the factory
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ViewModelFactory(authViewModel)
    )

    val authUiState = authViewModel.uiState

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            authUiState.isAuthLoading -> {
                CircularProgressIndicator()
            }
            authUiState.isAuthenticated -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Welcome!")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { profileViewModel.signOut() }) {
                        Text("Sign Out")
                    }
                }
            }
            else -> {
                Text("Please sign in to view your profile.")
            }
        }
    }
}