package com.tmdbclone.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.tmdbclone.TMDBCloneApp
import com.tmdbclone.viewmodel.profile.ProfileViewModel
import com.tmdbclone.viewmodel.profile.UserProfile
import com.tmdbclone.viewmodel.profile.UserReview

@Composable
fun ProfileScreen(
    navController: NavController
) {
    val app = LocalContext.current.applicationContext as TMDBCloneApp
    val viewModel: ProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(app.convex) as T
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.fetchProfileData()
    }

    val uiState by viewModel.uiState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
                }
            }
            uiState.user != null -> {
                ProfileContent(
                    user = uiState.user!!,
                    reviews = uiState.reviews,
                    onLogout = { viewModel.logout() }
                )
            }
        }
    }
}

@Composable
fun ProfileContent(user: UserProfile, reviews: List<UserReview>, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        AsyncImage(
            model = user.profileImageUrl,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(128.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = user.name, style = MaterialTheme.typography.headlineSmall)
        Text(text = user.email, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onLogout) {
            Text("Logout")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("My Reviews", style = MaterialTheme.typography.titleLarge)
        LazyColumn {
            items(reviews.size) { index ->
                val review = reviews[index]
                // TODO: Add movie title to the review if possible, for now just comment and rating
                Text("Movie ID: ${review.movieId} - Rating: ${review.rating}/5")
                Text(review.comment)
                Divider()
            }
        }
    }
}
