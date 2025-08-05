package com.tofiq.myimdb.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.tofiq.myimdb.data.model.domain.MovieResponse
import com.tofiq.myimdb.ui.components.CommonAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    movie: MovieResponse.Movie,
    onBackClick: () -> Unit,
    onAddToWishlist: (Int) -> Unit,
    isInWishlist: Boolean
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Scaffold(
        topBar = {
            CommonAppBar(
                title = movie.title ?: "Movie Details",
                showBackButton = true,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(
                    durationMillis = 600,
                    easing = FastOutSlowInEasing
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = 600,
                    easing = FastOutSlowInEasing
                )
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Background with gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 32.dp)
                ) {
                    // Hero Section with Poster and Back Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                    ) {
                        // Poster Image
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(movie.posterUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Poster for ${movie.title}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit,
                            loading = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(48.dp),
                                        strokeWidth = 4.dp
                                    )
                                }
                            },
                            error = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Movie,
                                        contentDescription = "Movie placeholder",
                                        modifier = Modifier.size(64.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        )

                        // Gradient overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.7f)
                                        )
                                    )
                                )
                        )

                        // Wishlist Button at top right
                        androidx.compose.material3.IconButton(
                            onClick = { movie.id?.let { onAddToWishlist(it) } },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                                .size(48.dp)
                                .background(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                    RoundedCornerShape(24.dp)
                                )
                        ) {
                            Icon(
                                imageVector = if (isInWishlist) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isInWishlist) "Remove from wishlist" else "Add to wishlist",
                                modifier = Modifier.size(24.dp),
                                tint = if (isInWishlist) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Movie Title at bottom
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            AnimatedVisibility(
                                visible = isVisible,
                                enter = slideInHorizontally(
                                    initialOffsetX = { -it },
                                    animationSpec = tween(
                                        durationMillis = 800,
                                        easing = FastOutSlowInEasing
                                    )
                                ) + fadeIn(
                                    animationSpec = tween(
                                        durationMillis = 800,
                                        easing = FastOutSlowInEasing
                                    )
                                )
                            ) {
                                Text(
                                    text = movie.title ?: "Unknown Title",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    maxLines = 3,
                                    lineHeight = 36.sp,
                                    textAlign = TextAlign.Start
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            AnimatedVisibility(
                                visible = isVisible,
                                enter = slideInHorizontally(
                                    initialOffsetX = { -it },
                                    animationSpec = tween(
                                        durationMillis = 1000,
                                        easing = FastOutSlowInEasing
                                    )
                                ) + fadeIn(
                                    animationSpec = tween(
                                        durationMillis = 1000,
                                        easing = FastOutSlowInEasing
                                    )
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Released: ",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = movie.year ?: "Unknown",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }
                    }

                    // Content Section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 24.dp)
                    ) {
                        // Genre Tags
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(
                                    durationMillis = 1200,
                                    easing = FastOutSlowInEasing
                                )
                            ) + fadeIn(
                                animationSpec = tween(
                                    durationMillis = 1200,
                                    easing = FastOutSlowInEasing
                                )
                            )
                        ) {
                            movie.genres?.filterNotNull()?.let { genres ->
                                if (genres.isNotEmpty()) {
                                    Column {
                                        Text(
                                            text = "Genres",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            genres.forEach { genre ->
                                                Card(
                                                    modifier = Modifier,
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                                    ),
                                                    shape = RoundedCornerShape(16.dp)
                                                ) {
                                                    Text(
                                                        text = genre,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        modifier = Modifier.padding(
                                                            horizontal = 14.dp,
                                                            vertical = 8.dp
                                                        ),
                                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(24.dp))
                                    }
                                }
                            }
                        }

                        // Director
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(
                                    durationMillis = 1400,
                                    easing = FastOutSlowInEasing
                                )
                            ) + fadeIn(
                                animationSpec = tween(
                                    durationMillis = 1400,
                                    easing = FastOutSlowInEasing
                                )
                            )
                        ) {
                            if (!movie.director.isNullOrEmpty()) {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Director",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Text(
                                        text = movie.director,
                                        fontSize = 17.sp,
                                        lineHeight = 24.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 24.dp)
                                    )
                                }
                            }
                        }

                        // Runtime
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(
                                    durationMillis = 1600,
                                    easing = FastOutSlowInEasing
                                )
                            ) + fadeIn(
                                animationSpec = tween(
                                    durationMillis = 1600,
                                    easing = FastOutSlowInEasing
                                )
                            )
                        ) {
                            if (!movie.runtime.isNullOrEmpty()) {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Runtime",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Text(
                                        text = "${movie.runtime.let { (it.toInt() / 60).toString() }}h",
                                        fontSize = 17.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 24.dp)
                                    )
                                }
                            }
                        }

                        // Plot
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(
                                    durationMillis = 1800,
                                    easing = FastOutSlowInEasing
                                )
                            ) + fadeIn(
                                animationSpec = tween(
                                    durationMillis = 1800,
                                    easing = FastOutSlowInEasing
                                )
                            )
                        ) {
                            if (!movie.plot.isNullOrEmpty()) {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Plot",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Text(
                                        text = movie.plot,
                                        fontSize = 17.sp,
                                        lineHeight = 26.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 24.dp)
                                    )
                                }
                            }
                        }

                        // Actors
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(
                                    durationMillis = 2000,
                                    easing = FastOutSlowInEasing
                                )
                            ) + fadeIn(
                                animationSpec = tween(
                                    durationMillis = 2000,
                                    easing = FastOutSlowInEasing
                                )
                            )
                        ) {
                            if (!movie.actors.isNullOrEmpty()) {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Cast",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Text(
                                        text = movie.actors,
                                        fontSize = 17.sp,
                                        lineHeight = 24.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 32.dp)
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