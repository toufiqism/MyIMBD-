package com.tofiq.myimdb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tofiq.myimdb.data.model.domain.MovieResponse
import com.tofiq.myimdb.data.repository.MovieRepository
import com.tofiq.myimdb.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _movieState = MutableStateFlow<Resource<MovieResponse>>(Resource.Loading())
    val movieState: StateFlow<Resource<MovieResponse>> = _movieState.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()
    
    private val _displayedMovies = MutableStateFlow<List<MovieResponse.Movie?>>(emptyList())
    val displayedMovies: StateFlow<List<MovieResponse.Movie?>> = _displayedMovies.asStateFlow()
    
    private val _allMovies = MutableStateFlow<List<MovieResponse.Movie?>>(emptyList())
    private val _currentPage = MutableStateFlow(0)
    private val _selectedMovie = MutableStateFlow<MovieResponse.Movie?>(null)
    private val pageSize = 10

    init {
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch {
            _movieState.value = Resource.Loading()
            _isLoading.value = true
            val result = movieRepository.getMovies()
            _movieState.value = result
            
            when (result) {
                is Resource.Success -> {
                    result.data?.movies?.let { movies ->
                        _allMovies.value = movies
                        loadNextPage()
                    }
                }
                else -> {
                    // Handle error or loading states
                }
            }
            _isLoading.value = false
        }
    }

    fun refreshMovies() {
        viewModelScope.launch {
            _movieState.value = Resource.Loading()
            _isLoading.value = true
            _currentPage.value = 0
            _displayedMovies.value = emptyList()
            
            val result = movieRepository.refreshMovies()
            _movieState.value = result
            
            when (result) {
                is Resource.Success -> {
                    result.data?.movies?.let { movies ->
                        _allMovies.value = movies
                        loadNextPage()
                    }
                }
                else -> {
                    // Handle error or loading states
                }
            }
            _isLoading.value = false
        }
    }
    
    fun loadNextPage() {
        if (_isLoadingMore.value) return
        
        val allMovies = _allMovies.value
        val currentPage = _currentPage.value
        val startIndex = currentPage * pageSize
        
        if (startIndex >= allMovies.size) return // No more movies to load
        
        viewModelScope.launch {
            _isLoadingMore.value = true
            
            val endIndex = minOf(startIndex + pageSize, allMovies.size)
            val newMovies = allMovies.subList(startIndex, endIndex)
            
            _displayedMovies.value = _displayedMovies.value + newMovies
            _currentPage.value = currentPage + 1
            
            _isLoadingMore.value = false
        }
    }
    
    fun hasMoreMovies(): Boolean {
        val allMovies = _allMovies.value
        val currentPage = _currentPage.value
        val startIndex = currentPage * pageSize
        return startIndex < allMovies.size
    }
    
    fun getMovieById(movieId: Int): MovieResponse.Movie? {
        return _allMovies.value.find { it?.id == movieId }
    }
    
    fun setSelectedMovie(movie: MovieResponse.Movie) {
        _selectedMovie.value = movie
    }
    
    fun getSelectedMovie(): MovieResponse.Movie? {
        return _selectedMovie.value
    }
} 