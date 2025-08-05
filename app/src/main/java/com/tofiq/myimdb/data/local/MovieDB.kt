package com.tofiq.myimdb.data.local

import androidx.room.Database
import com.tofiq.myimdb.data.local.dao.MovieEntityDAO
import com.tofiq.myimdb.data.local.dao.WishlistEntityDAO
import com.tofiq.myimdb.data.local.entity.MovieEntity
import com.tofiq.myimdb.data.local.entity.WishlistEntity
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MovieEntity::class, WishlistEntity::class], version = 1, exportSchema = false)
abstract class MovieDB : RoomDatabase() {
    abstract fun movieDao(): MovieEntityDAO
    abstract fun wishlistDao(): WishlistEntityDAO

    companion object {
        @Volatile
        private var instance: MovieDB? = null

        fun getInstance(context: Context): MovieDB {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    MovieDB::class.java,
                    "movie.db"
                ).build().also { instance = it }
            }
        }
    }
}