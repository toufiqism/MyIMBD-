package com.tofiq.myimdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.tofiq.myimdb.data.model.domain.MovieResponse
import com.tofiq.myimdb.ui.screens.HomeScreen
import com.tofiq.myimdb.ui.screens.MovieDetailsScreen
import com.tofiq.myimdb.ui.screens.SplashScreen
import com.tofiq.myimdb.ui.screens.WishlistScreen
import com.tofiq.myimdb.ui.theme.MyIMDBTheme
import com.tofiq.myimdb.ui.viewmodel.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val movieViewModel: MovieViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyIMDBTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyIMDBApp(movieViewModel = movieViewModel)
                }
            }
        }
    }
}

@Composable
fun MyIMDBApp(movieViewModel: MovieViewModel) {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                movieViewModel = movieViewModel,
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        
        composable("home") {
            HomeScreen(
                movieViewModel = movieViewModel,
                onMovieClick = { movie ->
                    // Navigate to details with movie data passed through ViewModel
                    movieViewModel.setSelectedMovie(movie)
                    navController.navigate("movie_details")
                },
                onWishlistClick = {
                    navController.navigate("wishlist")
                }
            )
        }
        
        composable("movie_details") {
            val selectedMovie = movieViewModel.getSelectedMovie()
            selectedMovie?.let { movie ->
                MovieDetailsScreen(
                    movie = movie,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onAddToWishlist = { movieId ->
                        movieViewModel.toggleWishlist(movieId)
                    },
                    isInWishlist = movieViewModel.isInWishlist(selectedMovie.id ?: 0),
                    isWishlistLoading = movieViewModel.isWishlistLoading.collectAsState().value
                )
            }
        }
        
        composable("wishlist") {
            WishlistScreen(
                movieViewModel = movieViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onMovieClick = { movie ->
                    movieViewModel.setSelectedMovie(movie)
                    navController.navigate("movie_details")
                }
            )
        }
    }
}