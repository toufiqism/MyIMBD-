package com.tofiq.myimdb.data.repository

import com.tofiq.myimdb.data.model.domain.MovieResponse
import com.tofiq.myimdb.data.remote.MovieApiService
import com.tofiq.myimdb.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val apiService: MovieApiService
) : MovieRepository {
    
    override suspend fun getMovies(): Resource<MovieResponse> {
        return try {
            val response = apiService.getMovies()
            if (response.isSuccessful) {
                response.body()?.let { movieResponse ->
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
}
