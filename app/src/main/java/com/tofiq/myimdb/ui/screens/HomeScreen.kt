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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Refresh
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
    onMovieClick: (com.tofiq.myimdb.data.model.domain.MovieResponse.Movie) -> Unit
) {
    val movieState by movieViewModel.movieState.collectAsState()
    val isLoading by movieViewModel.isLoading.collectAsState()
    val displayedMovies by movieViewModel.displayedMovies.collectAsState()
    val isLoadingMore by movieViewModel.isLoadingMore.collectAsState()
    val selectedGenre by movieViewModel.selectedGenre.collectAsState()
    val availableGenres by movieViewModel.availableGenres.collectAsState()

    var showFilterDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CommonAppBar(
                title = "MyIMDB Movies",
                showRefreshButton = true,
                showFilterButton = true,
                onRefreshClick = { movieViewModel.refreshMovies() },
                onFilterClick = { showFilterDropdown = true },
                isLoading = isLoading
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter indicator
            if (selectedGenre != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
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
            .clickable { onMovieClick(movie) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Poster Image with Placeholder
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(movie.posterUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Poster for ${movie.title}",
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Fit,
                loading = {
                    // Loading placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                },
                error = {
                    // Error placeholder with movie icon
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = "Movie placeholder",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Movie Details (Simplified)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = movie.title ?: "Unknown Title",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Year: ${movie.year ?: "Unknown"}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Genre: ${movie.genres?.joinToString(", ") ?: "Unknown"}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
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
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp
                    )
                }
            }
        }

        // End of list indicator
        if (!hasMoreMovies && movies.isNotEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "You've reached the end of the list",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
