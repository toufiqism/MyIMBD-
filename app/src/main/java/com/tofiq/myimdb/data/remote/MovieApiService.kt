package com.tofiq.myimdb.data.remote

import com.tofiq.myimdb.data.model.domain.MovieResponse
import retrofit2.Response
import retrofit2.http.GET

interface MovieApiService {
    /* @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")k
        apiKey: String = API_KEY
    ): Response<NewsResponse>*/

    @GET("db.json")
    suspend fun getMovies(): Response<MovieResponse>
}