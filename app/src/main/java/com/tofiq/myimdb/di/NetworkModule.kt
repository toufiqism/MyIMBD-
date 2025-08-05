package com.tofiq.myimdb.di

import android.content.Context
import com.tofiq.myimdb.data.local.MovieDB
import com.tofiq.myimdb.data.local.dao.MovieEntityDAO
import com.tofiq.myimdb.data.remote.MovieApiService
import com.tofiq.myimdb.data.repository.MovieRepository
import com.tofiq.myimdb.data.repository.MovieRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/erik-sytnyk/movies-list/master/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideMovieApiService(retrofit: Retrofit): MovieApiService {
        return retrofit.create(MovieApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideMovieDatabase(@ApplicationContext context: Context): MovieDB {
        return MovieDB.getInstance(context)
    }
    
    @Provides
    @Singleton
    fun provideMovieDao(database: MovieDB): MovieEntityDAO {
        return database.movieDao()
    }
    
    @Provides
    @Singleton
    fun provideMovieRepository(apiService: MovieApiService, movieDao: MovieEntityDAO): MovieRepository {
        return MovieRepositoryImpl(apiService, movieDao)
    }
} 