package com.tofiq.myimdb.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import com.tofiq.myimdb.data.local.entity.WishlistEntity

@Dao
interface WishlistEntityDAO {
    @Query("SELECT * FROM wishlist ORDER BY addedAt DESC")
    suspend fun getAllWishlistMovies(): List<WishlistEntity>

    @Query("SELECT movieId FROM wishlist")
    suspend fun getWishlistMovieIds(): List<Int>

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist WHERE movieId = :movieId)")
    suspend fun isMovieInWishlist(movieId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlistMovie(wishlistMovie: WishlistEntity)

    @Delete
    suspend fun deleteWishlistMovie(wishlistMovie: WishlistEntity)

    @Query("DELETE FROM wishlist WHERE movieId = :movieId")
    suspend fun deleteWishlistMovieById(movieId: Int)

    @Query("DELETE FROM wishlist")
    suspend fun deleteAllWishlistMovies()

    @Query("SELECT COUNT(*) FROM wishlist")
    suspend fun getWishlistCount(): Int
}