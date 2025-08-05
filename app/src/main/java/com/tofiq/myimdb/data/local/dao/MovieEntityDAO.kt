package com.tofiq.myimdb.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tofiq.myimdb.data.local.entity.MovieEntity

@Dao
interface MovieEntityDAO {
    @Query("SELECT * FROM movie LIMIT 1")
    suspend fun getMovie(): MovieEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    @Query("DELETE FROM movie")
    suspend fun deleteAllMovies()
}