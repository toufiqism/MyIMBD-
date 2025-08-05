package com.tofiq.myimdb.data.repository

import com.google.gson.Gson
import com.tofiq.myimdb.data.local.dao.MovieEntityDAO
import com.tofiq.myimdb.data.local.dao.WishlistEntityDAO
import com.tofiq.myimdb.data.local.entity.MovieEntity
import com.tofiq.myimdb.data.local.entity.WishlistEntity
import com.tofiq.myimdb.data.model.domain.MovieResponse
import com.tofiq.myimdb.data.remote.MovieApiService
import com.tofiq.myimdb.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val apiService: MovieApiService,
    private val movieDao: MovieEntityDAO,
    private val wishlistDao: WishlistEntityDAO
) : MovieRepository {
    
    override suspend fun getMovies(): Resource<MovieResponse> {
        return try {
            // First, try to get data from local database
            val localMovie = movieDao.getMovie()
            if (localMovie != null) {
                // Data exists in local database, return it
                val movieResponse = Gson().fromJson(localMovie.response, MovieResponse::class.java)
                return Resource.Success(movieResponse)
            }
            
            // No local data, fetch from API
            val response = apiService.getMovies()
            if (response.isSuccessful) {
                response.body()?.let { movieResponse ->
                    // Store the response in local database
                    val movieEntity = MovieEntity(response = Gson().toJson(movieResponse))
                    movieDao.insertMovie(movieEntity)
                    Resource.Success(movieResponse)
                } ?: Resource.Error("Empty response body")
            } else {
                Resource.Error("HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: HttpException) {
            Resource.Error("HTTP Exception: ${e.message}")
        } catch (e: IOException) {
            Resource.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Unexpected error: ${e.message}")
        }
    }
    
    override suspend fun refreshMovies(): Resource<MovieResponse> {
        return try {
            // Clear local database
            movieDao.deleteAllMovies()
            
            // Fetch fresh data from API
            val response = apiService.getMovies()
            if (response.isSuccessful) {
                response.body()?.let { movieResponse ->
                    // Store the new response in local database
                    val movieEntity = MovieEntity(response = Gson().toJson(movieResponse))
                    movieDao.insertMovie(movieEntity)
                    Resource.Success(movieResponse)
                } ?: Resource.Error("Empty response body")
            } else {
                Resource.Error("HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: HttpException) {
            Resource.Error("HTTP Exception: ${e.message}")
        } catch (e: IOException) {
            Resource.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Unexpected error: ${e.message}")
        }
    }

    // Wishlist operations
    override suspend fun getWishlistMovies(): List<MovieResponse.Movie> {
        return try {
            val wishlistEntities = wishlistDao.getAllWishlistMovies()
            wishlistEntities.map { entity ->
                MovieResponse.Movie(
                    id = entity.movieId,
                    title = entity.title,
                    year = entity.year,
                    posterUrl = entity.posterUrl,
                    plot = entity.plot,
                    director = entity.director,
                    actors = entity.actors,
                    genres = entity.genres?.split(",")?.filter { it.isNotEmpty() },
                    runtime = entity.runtime
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addToWishlist(movie: MovieResponse.Movie) {
        try {
            val wishlistEntity = WishlistEntity(
                movieId = movie.id ?: return,
                title = movie.title,
                year = movie.year,
                posterUrl = movie.posterUrl,
                plot = movie.plot,
                director = movie.director,
                actors = movie.actors,
                genres = movie.genres?.joinToString(","),
                runtime = movie.runtime
            )
            wishlistDao.insertWishlistMovie(wishlistEntity)
        } catch (e: Exception) {
            // Handle error silently or log it
        }
    }

    override suspend fun removeFromWishlist(movieId: Int) {
        try {
            wishlistDao.deleteWishlistMovieById(movieId)
        } catch (e: Exception) {
            // Handle error silently or log it
        }
    }

    override suspend fun isMovieInWishlist(movieId: Int): Boolean {
        return try {
            wishlistDao.isMovieInWishlist(movieId)
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getWishlistCount(): Int {
        return try {
            wishlistDao.getWishlistCount()
        } catch (e: Exception) {
            0
        }
    }
}
