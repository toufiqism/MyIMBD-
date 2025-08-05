package com.tofiq.myimdb.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.tofiq.myimdb.data.model.domain.MovieResponse
import com.tofiq.myimdb.ui.components.CommonAppBar
import com.tofiq.myimdb.ui.viewmodel.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    movieViewModel: MovieViewModel,
    onBackClick: () -> Unit,
    onMovieClick: (MovieResponse.Movie) -> Unit
) {
    val wishlistMovies by movieViewModel.wishlistMovies.collectAsState()
    val allMovies by movieViewModel.displayedMovies.collectAsState()
    val wishlistCount by movieViewModel.wishlistCount.collectAsState()
    
    val wishlistedMovies = allMovies.filterNotNull().filter { movie ->
        movie.id != null && wishlistMovies.contains(movie.id)
    }

    Scaffold(
        topBar = {
            CommonAppBar(
                title = "My Wishlist",
                showBackButton = true,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        if (wishlistedMovies.isEmpty()) {
            // Empty wishlist state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Empty wishlist",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your wishlist is empty",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Add movies to your wishlist to see them here",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                    top = 8.dp,
                    bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = wishlistedMovies,
                    key = { movie -> movie.id ?: movie.hashCode() }
                ) { movie ->
                    WishlistMovieCard(
                        movie = movie,
                        onMovieClick = onMovieClick,
                        onRemoveFromWishlist = {
                            movie.id?.let { movieViewModel.toggleWishlist(it) }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistMovieCard(
    movie: MovieResponse.Movie,
    onMovieClick: (MovieResponse.Movie) -> Unit,
    onRemoveFromWishlist: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 8.dp
        ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Poster Image with Placeholder
            Card(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
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
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            MaterialTheme.colorScheme.surface
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                strokeWidth = 3.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            MaterialTheme.colorScheme.surface
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Movie placeholder",
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "No Image",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Movie Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = movie.title ?: "Unknown Title",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    lineHeight = 22.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Year with icon
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Year",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = movie.year ?: "Unknown",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                // Genre tags
                movie.genres?.filterNotNull()?.take(2)?.let { genres ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        genres.forEach { genre ->
                            Card(
                                modifier = Modifier,
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = genre,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(
                                        horizontal = 8.dp,
                                        vertical = 4.dp
                                    ),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                        if ((movie.genres?.size ?: 0) > 2) {
                            Text(
                                text = "+${(movie.genres?.size ?: 0) - 2}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 4.dp
                                )
                            )
                        }
                    }
                }
            }
            
            // Remove from wishlist button
            IconButton(
                onClick = onRemoveFromWishlist
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Remove from wishlist",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
} 