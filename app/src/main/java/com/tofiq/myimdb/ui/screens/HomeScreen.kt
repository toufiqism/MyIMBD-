package com.tofiq.myimdb.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.tofiq.myimdb.ui.viewmodel.MovieViewModel
import com.tofiq.myimdb.util.Resource
import com.tofiq.myimdb.ui.components.CommonAppBar
import com.tofiq.myimdb.ui.components.FilterDropdown

@Composable
fun EmptyState(
    message: String,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRefresh) {
            Text("Refresh")
        }
    }
}

@Composable
fun ErrorState(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error: $error",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    movieViewModel: MovieViewModel,
    onMovieClick: (com.tofiq.myimdb.data.model.domain.MovieResponse.Movie) -> Unit,
    onWishlistClick: () -> Unit
) {
    val movieState by movieViewModel.movieState.collectAsState()
    val isLoading by movieViewModel.isLoading.collectAsState()
    val displayedMovies by movieViewModel.displayedMovies.collectAsState()
    val isLoadingMore by movieViewModel.isLoadingMore.collectAsState()
    val selectedGenre by movieViewModel.selectedGenre.collectAsState()
    val availableGenres by movieViewModel.availableGenres.collectAsState()
    val searchQuery by movieViewModel.searchQuery.collectAsState()
    val isSearchActive by movieViewModel.isSearchActive.collectAsState()
    val wishlistCount by movieViewModel.wishlistCount.collectAsState()

    var showFilterDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CommonAppBar(
                title = "MyIMDB Movies",
                showRefreshButton = true,
                showFilterButton = true,
                showSearchButton = true,
                showWishlistButton = true,
                wishlistCount = wishlistCount,
                isSearchActive = isSearchActive,
                searchQuery = searchQuery,
                onRefreshClick = { movieViewModel.refreshMovies() },
                onFilterClick = { showFilterDropdown = true },
                onSearchClick = { movieViewModel.setSearchActive(true) },
                onWishlistClick = onWishlistClick,
                onSearchQueryChange = { movieViewModel.setSearchQuery(it) },
                onSearchActiveChange = { movieViewModel.setSearchActive(it) },
                isLoading = isLoading
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter and search indicators
            if (selectedGenre != null || searchQuery.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        if (selectedGenre != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Filtered by: $selectedGenre",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                TextButton(
                                    onClick = { movieViewModel.setSelectedGenre(null) }
                                ) {
                                    Text(
                                        text = "Clear",
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                        
                        if (searchQuery.isNotEmpty()) {
                            if (selectedGenre != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Search: \"$searchQuery\"",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                TextButton(
                                    onClick = { movieViewModel.setSearchQuery("") }
                                ) {
                                    Text(
                                        text = "Clear",
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Movie content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                // Filter Dropdown
                if (showFilterDropdown) {
                    FilterDropdown(
                        genres = availableGenres,
                        selectedGenre = selectedGenre,
                        onGenreSelected = { genre ->
                            movieViewModel.setSelectedGenre(genre)
                        },
                        onDismiss = {
                            showFilterDropdown = false
                        }
                    )
                }

                when (movieState) {
                    is Resource.Loading -> {
                        if (isLoading) {
                            // Show loading indicator
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Loading movies...")
                            }
                        } else {
                            // Show cached data while refreshing
                            if (displayedMovies.isNotEmpty()) {
                                MovieList(
                                    movies = displayedMovies,
                                    onRefresh = { movieViewModel.refreshMovies() },
                                    onLoadMore = { movieViewModel.loadNextPage() },
                                    isLoadingMore = isLoadingMore,
                                    hasMoreMovies = movieViewModel.hasMoreMovies(),
                                    onMovieClick = onMovieClick
                                )
                            }
                        }
                    }

                    is Resource.Success -> {
                        if (displayedMovies.isNotEmpty()) {
                            MovieList(
                                movies = displayedMovies,
                                onRefresh = { movieViewModel.refreshMovies() },
                                onLoadMore = { movieViewModel.loadNextPage() },
                                isLoadingMore = isLoadingMore,
                                hasMoreMovies = movieViewModel.hasMoreMovies(),
                                onMovieClick = onMovieClick
                            )
                        } else {
                            EmptyState(
                                message = "No movies found",
                                onRefresh = { movieViewModel.refreshMovies() }
                            )
                        }
                    }

                    is Resource.Error -> {
                        ErrorState(
                            error = movieState.message ?: "Unknown error occurred",
                            onRetry = { movieViewModel.refreshMovies() }
                        )
                    }
                }
            }
        }
    }
    
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieCard(
    movie: com.tofiq.myimdb.data.model.domain.MovieResponse.Movie,
    onMovieClick: (com.tofiq.myimdb.data.model.domain.MovieResponse.Movie) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMovieClick(movie) }
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
                        // Loading placeholder
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
                        // Error placeholder with movie icon
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
                                    imageVector = Icons.Default.Movie,
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
            
            // Arrow indicator
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "View details",
                modifier = Modifier
                    .size(20.dp)
                    .rotate(180f),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MovieList(
    movies: List<com.tofiq.myimdb.data.model.domain.MovieResponse.Movie?>,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    isLoadingMore: Boolean,
    hasMoreMovies: Boolean,
    onMovieClick: (com.tofiq.myimdb.data.model.domain.MovieResponse.Movie) -> Unit
) {
    val listState = rememberLazyListState()

    // Detect when user scrolls near the end to load more movies
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val lastVisibleItem = visibleItems.lastOrNull()
                if (lastVisibleItem != null) {
                    val threshold = movies.size - 3 // Load more when 3 items away from end
                    if (lastVisibleItem.index >= threshold && hasMoreMovies && !isLoadingMore) {
                        onLoadMore()
                    }
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 12.dp,
            end = 12.dp,
            top = 8.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(
            items = movies.filterNotNull(),
            key = { _, movie -> movie.id ?: movie.hashCode() }
        ) { _, movie ->
            MovieCard(
                movie = movie,
                onMovieClick = onMovieClick
            )
        }

        // Loading indicator at the bottom when loading more
        if (isLoadingMore) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Loading more movies...",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // End of list indicator
        if (!hasMoreMovies && movies.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "End of list",
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "You've reached the end of the list",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "All movies have been loaded",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
