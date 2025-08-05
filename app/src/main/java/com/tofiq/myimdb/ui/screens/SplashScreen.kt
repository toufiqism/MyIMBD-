package com.tofiq.myimdb.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tofiq.myimdb.ui.viewmodel.MovieViewModel
import com.tofiq.myimdb.util.Resource
import com.tofiq.myimdb.ui.components.CommonAppBar
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    movieViewModel: MovieViewModel,
    onNavigateToHome: () -> Unit
) {
    val movieState by movieViewModel.movieState.collectAsState()
    val isLoading by movieViewModel.isLoading.collectAsState()

    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }
    var showLoading by remember { mutableStateOf(false) }

    // Multiple animations
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    
    // Scale animation for the main icon
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Rotation animation for the star icon
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Pulse animation for the loading indicator
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Start animations
    LaunchedEffect(Unit) {
        isVisible = true
        delay(300)
        showContent = true
        delay(500)
        showLoading = true
    }

    LaunchedEffect(movieState) {
        when (movieState) {
            is Resource.Success -> {
                delay(500) // Show success briefly
                onNavigateToHome()
            }
            is Resource.Error -> {
                delay(1000) // Show error briefly
                onNavigateToHome()
            }
            is Resource.Loading -> {
                // Stay on splash screen while loading
            }
        }
    }

    Scaffold(

    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Animated background elements
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(1000)) + scaleIn(
                    initialScale = 0.3f,
                    animationSpec = tween(1000, easing = FastOutSlowInEasing)
                )
            ) {
                // Floating background elements
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .alpha(0.1f)
                            .scale(scale * (0.8f + index * 0.1f))
                            .offset(
                                x = (index * 200 - 200).dp,
                                y = (index * 150 - 150).dp
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            // Main content
            AnimatedVisibility(
                visible = showContent,
                enter = slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(800, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(800))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Main app icon with animations
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(scale),
                        contentAlignment = Alignment.Center
                    ) {
                        // Background circle
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                                    shape = androidx.compose.foundation.shape.CircleShape
                                )
                        )
                        
                        // Main movie icon
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = "MyIMDB",
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        
                        // Rotating star overlay
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .offset(x = 30.dp, y = (-30).dp)
                                .scale(scale * 0.8f),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // App title with animation
                    AnimatedVisibility(
                        visible = showContent,
                        enter = slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(1000, delayMillis = 300, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(1000, delayMillis = 300))
                    ) {
                        Text(
                            text = "MyIMDB",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Subtitle
                    AnimatedVisibility(
                        visible = showContent,
                        enter = slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(1000, delayMillis = 500, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(1000, delayMillis = 500))
                    ) {
                        Text(
                            text = "Your Movie Database",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    // Loading section
                    AnimatedVisibility(
                        visible = showLoading,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(800, delayMillis = 700, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(800, delayMillis = 700))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (isLoading) {
                                // Animated loading indicator
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .scale(pulse),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 3.dp
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Status text with animation
                                AnimatedContent(
                                    targetState = movieState,
                                    transitionSpec = {
                                        slideInVertically { height -> height } + fadeIn() togetherWith
                                        slideOutVertically { height -> -height } + fadeOut()
                                    }
                                ) { state ->
                                    Text(
                                        text = when (state) {
                                            is Resource.Loading -> "Loading movies..."
                                            is Resource.Success -> "Movies loaded successfully!"
                                            is Resource.Error -> "Error loading movies"
                                        },
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}