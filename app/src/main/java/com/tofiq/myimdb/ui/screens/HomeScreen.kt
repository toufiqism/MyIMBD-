package com.tofiq.myimdb.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tofiq.myimdb.ui.viewmodel.MovieViewModel
import com.tofiq.myimdb.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    movieViewModel: MovieViewModel
) {
    val movieState by movieViewModel.movieState.collectAsState()
    val isLoading by movieViewModel.isLoading.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MyIMDB Movies") },
                actions = {
                    IconButton(
                        onClick = { movieViewModel.refreshMovies() },
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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
                        movieState.data?.movies?.let { movies ->
                            MovieList(
                                movies = movies,
                                onRefresh = { movieViewModel.refreshMovies() }
                            )
                        }
                    }
                }
                is Resource.Success -> {
                    val movies = movieState.data?.movies ?: emptyList()
                    if (movies.isNotEmpty()) {
                        MovieList(
                            movies = movies,
                            onRefresh = { movieViewModel.refreshMovies() }
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

@Composable
fun MovieList(
    movies: List<com.tofiq.myimdb.data.model.domain.MovieResponse.Movie?>,
    onRefresh: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(movies.filterNotNull()) { movie ->
            MovieCard(movie = movie)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieCard(
    movie: com.tofiq.myimdb.data.model.domain.MovieResponse.Movie
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = movie.title ?: "Unknown Title",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Year: ${movie.year ?: "Unknown"}",
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Genre: ${movie.genres?.joinToString(", ") ?: "Unknown"}",
                fontSize = 14.sp
            )
            if (!movie.director.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Director: ${movie.director}",
                    fontSize = 14.sp
                )
            }
            if (!movie.actors.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Actors: ${movie.actors}",
                    fontSize = 14.sp
                )
            }
            if (!movie.plot.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = movie.plot,
                    fontSize = 14.sp
                )
            }
        }
    }
}

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