package com.tofiq.myimdb.data.remote

import com.tofiq.myimdb.data.model.domain.MovieResponse
import retrofit2.Response
import retrofit2.http.GET

interface MovieApiService {
    @GET("db.json")
    suspend fun getMovies(): Response<MovieResponse>
}