package com.tofiq.myimdb.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "movie",
)
data class MovieEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val response: String
)