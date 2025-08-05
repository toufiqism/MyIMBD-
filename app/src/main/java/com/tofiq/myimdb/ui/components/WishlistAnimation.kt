package com.tofiq.myimdb.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun WishlistAnimation(
    isVisible: Boolean,
    startPosition: androidx.compose.ui.geometry.Offset,
    endPosition: androidx.compose.ui.geometry.Offset,
    onAnimationComplete: () -> Unit
) {
    if (!isVisible) return
    
    val density = LocalDensity.current
    
    var animationState by remember { mutableStateOf(0f) }
    
    val animatedOffset by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "wishlist_animation"
    )
    
    val animatedScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.5f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        ),
        label = "wishlist_scale"
    )
    
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "wishlist_alpha"
    )
    
    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(800)
            onAnimationComplete()
        }
    }
    
    Box(
        modifier = Modifier
            .offset(
                x = with(density) {
                    (startPosition.x + (endPosition.x - startPosition.x) * animatedOffset).toDp()
                },
                y = with(density) {
                    (startPosition.y + (endPosition.y - startPosition.y) * animatedOffset).toDp()
                }
            )
            .scale(animatedScale)
            .alpha(animatedAlpha)
            .size(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Wishlist animation",
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
} 