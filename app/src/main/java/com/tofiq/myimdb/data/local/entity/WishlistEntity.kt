package com.tofiq.myimdb.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "wishlist"
)
data class WishlistEntity(
    @PrimaryKey val movieId: Int,
    val title: String?,
    val year: String?,
    val posterUrl: String?,
    val plot: String?,
    val director: String?,
    val actors: String?,
    val runtime: String?,
    val genres: String?, // Store as JSON string
    val addedAt: Long = System.currentTimeMillis()
) 