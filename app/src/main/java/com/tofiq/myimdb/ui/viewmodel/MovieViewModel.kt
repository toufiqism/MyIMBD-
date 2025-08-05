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
    private val _selectedGenre = MutableStateFlow<String?>(null)
    val selectedGenre: StateFlow<String?> = _selectedGenre.asStateFlow()
    private val _availableGenres = MutableStateFlow<List<String>>(emptyList())
    val availableGenres: StateFlow<List<String>> = _availableGenres.asStateFlow()
    
    // Search functionality
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()
    
    // Wishlist functionality
    private val _wishlistMovies = MutableStateFlow<Set<Int>>(emptySet())
    val wishlistMovies: StateFlow<Set<Int>> = _wishlistMovies.asStateFlow()
    private val _wishlistCount = MutableStateFlow(0)
    val wishlistCount: StateFlow<Int> = _wishlistCount.asStateFlow()
    
    // Grid/List view functionality
    private val _isGridView = MutableStateFlow(false)
    val isGridView: StateFlow<Boolean> = _isGridView.asStateFlow()
    
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
                        // Sort by year descending
                        _allMovies.value = movies.filterNotNull().sortedByDescending { it.year?.toIntOrNull() ?: 0 }
                        updateAvailableGenres()
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
                        // Sort by year descending
                        _allMovies.value = movies.filterNotNull().sortedByDescending { it.year?.toIntOrNull() ?: 0 }
                        updateAvailableGenres()
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
        val selectedGenre = _selectedGenre.value
        val searchQuery = _searchQuery.value
        
        // Filter movies by selected genre and search query
        val filteredMovies = allMovies.filterNotNull().filter { movie ->
            val matchesGenre = selectedGenre == null || movie.genres?.contains(selectedGenre) == true
            val matchesSearch = searchQuery.isEmpty() || matchesSearchQuery(movie, searchQuery)
            matchesGenre && matchesSearch
        }
        
        val currentPage = _currentPage.value
        val startIndex = currentPage * pageSize
        
        if (startIndex >= filteredMovies.size) return // No more movies to load
        
        viewModelScope.launch {
            _isLoadingMore.value = true
            
            val endIndex = minOf(startIndex + pageSize, filteredMovies.size)
            val newMovies = filteredMovies.subList(startIndex, endIndex)
            
            _displayedMovies.value = _displayedMovies.value + newMovies
            _currentPage.value = currentPage + 1
            
            _isLoadingMore.value = false
        }
    }
    
    fun hasMoreMovies(): Boolean {
        val allMovies = _allMovies.value
        val selectedGenre = _selectedGenre.value
        val searchQuery = _searchQuery.value
        
        // Filter movies by selected genre and search query
        val filteredMovies = allMovies.filterNotNull().filter { movie ->
            val matchesGenre = selectedGenre == null || movie.genres?.contains(selectedGenre) == true
            val matchesSearch = searchQuery.isEmpty() || matchesSearchQuery(movie, searchQuery)
            matchesGenre && matchesSearch
        }
        
        val currentPage = _currentPage.value
        val startIndex = currentPage * pageSize
        return startIndex < filteredMovies.size
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
    
    fun setSelectedGenre(genre: String?) {
        _selectedGenre.value = genre
        _currentPage.value = 0
        _displayedMovies.value = emptyList()
        loadNextPage()
    }
    
    // Search functionality
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        _currentPage.value = 0
        _displayedMovies.value = emptyList()
        loadNextPage()
    }
    
    fun setSearchActive(active: Boolean) {
        _isSearchActive.value = active
        if (!active) {
            _searchQuery.value = ""
            _currentPage.value = 0
            _displayedMovies.value = emptyList()
            loadNextPage()
        }
    }
    
    private fun matchesSearchQuery(movie: MovieResponse.Movie, query: String): Boolean {
        val searchQuery = query.trim().lowercase()
        if (searchQuery.isEmpty()) return true
        
        return movie.title?.lowercase()?.contains(searchQuery) == true ||
               movie.plot?.lowercase()?.contains(searchQuery) == true ||
               movie.director?.lowercase()?.contains(searchQuery) == true ||
               movie.actors?.lowercase()?.contains(searchQuery) == true ||
               movie.genres?.any { it?.lowercase()?.contains(searchQuery) == true } == true
    }
    

    
    private fun updateAvailableGenres() {
        val genres = _allMovies.value
            .filterNotNull()
            .flatMap { movie -> movie.genres?.filterNotNull() ?: emptyList() }
            .distinct()
            .sorted()
        _availableGenres.value = genres
    }
    
    // Wishlist functionality
    fun toggleWishlist(movieId: Int) {
        val currentWishlist = _wishlistMovies.value.toMutableSet()
        if (currentWishlist.contains(movieId)) {
            currentWishlist.remove(movieId)
        } else {
            currentWishlist.add(movieId)
        }
        _wishlistMovies.value = currentWishlist
        _wishlistCount.value = currentWishlist.size
    }
    
    fun isInWishlist(movieId: Int): Boolean {
        return _wishlistMovies.value.contains(movieId)
    }
    
    fun getWishlistMovies(): List<MovieResponse.Movie> {
        return _allMovies.value.filterNotNull().filter { movie ->
            movie.id != null && _wishlistMovies.value.contains(movie.id)
        }
    }
    
    // Grid/List view functionality
    fun toggleGridView() {
        _isGridView.value = !_isGridView.value
    }
} 