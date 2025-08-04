package com.tofiq.myimdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tofiq.myimdb.ui.theme.MyIMDBTheme
import com.tofiq.myimdb.ui.viewmodel.MovieViewModel
import com.tofiq.myimdb.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val movieViewModel: MovieViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyIMDBTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MovieScreen(
                        modifier = Modifier.padding(innerPadding),
                        movieViewModel = movieViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun MovieScreen(
    modifier: Modifier = Modifier,
    movieViewModel: MovieViewModel
) {
    val movieState by movieViewModel.movieState.collectAsState()
    
    when (movieState) {
        is Resource.Loading -> {
            Text(text = "Loading movies...", modifier = modifier)
        }
        is Resource.Success -> {
            val movies = (movieState as Resource.Success).data
            Text(
                text = "Loaded ${movies?.movies?.size ?: 0} movies",
                modifier = modifier
            )
        }
        is Resource.Error -> {
            val error = (movieState as Resource.Error).message
            Text(text = "Error: $error", modifier = modifier)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyIMDBTheme {
        Greeting("Android")
    }
}