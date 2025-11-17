package com.tmdbclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tmdbclone.ui.screens.auth.AuthScreen
import com.tmdbclone.ui.theme.TMDBCloneTheme
import com.tmdbclone.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as TMDBCloneApp
        setContent {
            TMDBCloneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authViewModel: AuthViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return AuthViewModel(app.convex) as T
                            }
                        }
                    )
                    AppEntry(authViewModel)
                }
            }
        }
    }
}

import com.tmdbclone.ui.screens.MainScreen

@Composable
fun AppEntry(authViewModel: AuthViewModel) {
    val uiState = authViewModel.uiState.collectAsState().value

    if (uiState.isLoggedIn) {
        MainScreen()
    } else {
        AuthScreen(authViewModel = authViewModel, onLoginSuccess = {
            // This callback is handled by the LaunchedEffect in AuthScreen
        })
    }
}
