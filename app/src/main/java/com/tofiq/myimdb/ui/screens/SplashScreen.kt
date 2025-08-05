package com.tofiq.myimdb.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tofiq.myimdb.ui.theme.Purple80
import com.tofiq.myimdb.ui.viewmodel.MovieViewModel
import com.tofiq.myimdb.util.Resource
import com.tofiq.myimdb.ui.components.CommonAppBar

@Composable
fun SplashScreen(
    movieViewModel: MovieViewModel,
    onNavigateToHome: () -> Unit
) {
    val movieState by movieViewModel.movieState.collectAsState()
    val isLoading by movieViewModel.isLoading.collectAsState()

    // Animation for the loading indicator
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(movieState) {
        when (movieState) {
            is Resource.Success -> {
                // Navigate to home screen after successful data loading
                onNavigateToHome()
            }

            is Resource.Error -> {
                // Navigate to home screen even on error (user can retry)
                onNavigateToHome()
            }

            is Resource.Loading -> {
                // Stay on splash screen while loading
            }
        }
    }

    Scaffold(
        topBar = {
            CommonAppBar(
                title = "MyIMDB",
                showBackButton = false,
                showRefreshButton = false
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Purple80),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // App Logo/Title
                Text(
                    text = "MyIMDB",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Loading indicator
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(60.dp)
                            .scale(scale),
                        color = Color.White,
                        strokeWidth = 4.dp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = when (movieState) {
                            is Resource.Loading -> "Loading movies..."
                            is Resource.Success -> "Movies loaded successfully!"
                            is Resource.Error -> "Error loading movies"
                        },
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}