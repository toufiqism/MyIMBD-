package com.tofiq.myimdb.data.repository

import com.tofiq.myimdb.data.model.domain.MovieResponse
import com.tofiq.myimdb.util.Resource

interface MovieRepository {
    suspend fun getMovies(): Resource<MovieResponse>
}