package com.tofiq.myimdb.data.model.domain

data class MovieResponse(
    val genres: List<String?>?,
    val movies: List<Movie?>?
) {
    data class Movie(
        val actors: String?,
        val director: String?,
        val genres: List<String?>?,
        val id: Int?,
        val plot: String?,
        val posterUrl: String?,
        val runtime: String?,
        val title: String?,
        val year: String?
    )
}