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

    init {
        getMovies()
    }

    fun getMovies() {
        viewModelScope.launch {
            _movieState.value = Resource.Loading()
            val result = movieRepository.getMovies()
            _movieState.value = result
        }
    }

    fun refreshMovies() {
        getMovies()
    }
} 