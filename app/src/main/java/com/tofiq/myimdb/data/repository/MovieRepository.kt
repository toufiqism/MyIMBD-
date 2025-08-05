package com.tofiq.myimdb.data.repository

import com.tofiq.myimdb.data.model.domain.MovieResponse
import com.tofiq.myimdb.util.Resource

interface MovieRepository {
    suspend fun getMovies(): Resource<MovieResponse>
    suspend fun refreshMovies(): Resource<MovieResponse>
    
    // Wishlist operations
    suspend fun getWishlistMovies(): List<MovieResponse.Movie>
    suspend fun addToWishlist(movie: MovieResponse.Movie)
    suspend fun removeFromWishlist(movieId: Int)
    suspend fun isMovieInWishlist(movieId: Int): Boolean
    suspend fun getWishlistCount(): Int
}