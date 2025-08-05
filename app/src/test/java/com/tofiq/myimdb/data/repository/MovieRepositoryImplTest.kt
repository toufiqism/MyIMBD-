package com.tofiq.myimdb.data.repository

import com.google.gson.Gson
import com.tofiq.myimdb.data.local.dao.MovieEntityDAO
import com.tofiq.myimdb.data.local.entity.MovieEntity
import com.tofiq.myimdb.data.model.domain.MovieResponse
import com.tofiq.myimdb.data.remote.MovieApiService
import com.tofiq.myimdb.util.Resource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class MovieRepositoryImplTest {

    private lateinit var movieRepository: MovieRepositoryImpl
    private lateinit var apiService: MovieApiService
    private lateinit var movieDao: MovieEntityDAO
    private lateinit var gson: Gson

    private val mockMovies = listOf(
        MovieResponse.Movie(
            actors = "Actor 1, Actor 2",
            director = "Director 1",
            genres = listOf("Action", "Drama"),
            id = 1,
            plot = "Test plot 1",
            posterUrl = "https://example.com/poster1.jpg",
            runtime = "120",
            title = "Test Movie 1",
            year = "2023"
        ),
        MovieResponse.Movie(
            actors = "Actor 3, Actor 4",
            director = "Director 2",
            genres = listOf("Comedy", "Romance"),
            id = 2,
            plot = "Test plot 2",
            posterUrl = "https://example.com/poster2.jpg",
            runtime = "90",
            title = "Test Movie 2",
            year = "2022"
        )
    )

    private val mockMovieResponse = MovieResponse(
        genres = listOf("Action", "Drama", "Comedy", "Romance"),
        movies = mockMovies
    )

    @Before
    fun setup() {
        apiService = mockk()
        movieDao = mockk()
        gson = Gson()
        movieRepository = MovieRepositoryImpl(apiService, movieDao)
    }

    @Test
    fun `getMovies should return local data when available`() = runTest {
        // Given
        val localMovieEntity = MovieEntity(response = gson.toJson(mockMovieResponse))
        coEvery { movieDao.getMovie() } returns localMovieEntity

        // When
        val result = movieRepository.getMovies()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(mockMovieResponse, result.data)
        coVerify(exactly = 0) { apiService.getMovies() }
    }

    @Test
    fun `getMovies should fetch from API when no local data available`() = runTest {
        // Given
        coEvery { movieDao.getMovie() } returns null
        coEvery { apiService.getMovies() } returns Response.success(mockMovieResponse)
        coEvery { movieDao.insertMovie(any()) } returns Unit

        // When
        val result = movieRepository.getMovies()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(mockMovieResponse, result.data)
        coVerify { movieDao.getMovie() }
        coVerify { apiService.getMovies() }
        coVerify { movieDao.insertMovie(any()) }
    }

    @Test
    fun `getMovies should return error when API returns unsuccessful response`() = runTest {
        // Given
        coEvery { movieDao.getMovie() } returns null
        coEvery { apiService.getMovies() } returns Response.error(
            404, "Not Found".toResponseBody()
        )

        // When
        val result = movieRepository.getMovies()

        // Then
        assertTrue(result is Resource.Error)
        assertTrue(result.message?.contains("HTTP 404") == true)
        coVerify { movieDao.getMovie() }
        coVerify { apiService.getMovies() }
        coVerify(exactly = 0) { movieDao.insertMovie(any()) }
    }

    @Test
    fun `getMovies should return error when API returns null body`() = runTest {
        // Given
        coEvery { movieDao.getMovie() } returns null
        coEvery { apiService.getMovies() } returns Response.success(null)

        // When
        val result = movieRepository.getMovies()

        // Then
        assertTrue(result is Resource.Error)
        assertEquals("Empty response body", result.message)
        coVerify { movieDao.getMovie() }
        coVerify { apiService.getMovies() }
        coVerify(exactly = 0) { movieDao.insertMovie(any()) }
    }

    @Test
    fun `getMovies should handle HttpException`() = runTest {
        // Given
        coEvery { movieDao.getMovie() } returns null
        coEvery { apiService.getMovies() } throws HttpException(
            Response.error<MovieResponse>(500, "Server Error".toResponseBody())
        )

        // When
        val result = movieRepository.getMovies()

        // Then
        assertTrue(result is Resource.Error)
        assertTrue(result.message?.contains("HTTP Exception") == true)
    }

    @Test
    fun `getMovies should handle IOException`() = runTest {
        // Given
        coEvery { movieDao.getMovie() } returns null
        coEvery { apiService.getMovies() } throws IOException("Network error")

        // When
        val result = movieRepository.getMovies()

        // Then
        assertTrue(result is Resource.Error)
        assertTrue(result.message?.contains("Network error") == true)
    }

    @Test
    fun `getMovies should handle unexpected exceptions`() = runTest {
        // Given
        coEvery { movieDao.getMovie() } returns null
        coEvery { apiService.getMovies() } throws RuntimeException("Unexpected error")

        // When
        val result = movieRepository.getMovies()

        // Then
        assertTrue(result is Resource.Error)
        assertTrue(result.message?.contains("Unexpected error") == true)
    }

    @Test
    fun `refreshMovies should clear local data and fetch fresh data`() = runTest {
        // Given
        coEvery { movieDao.deleteAllMovies() } returns Unit
        coEvery { apiService.getMovies() } returns Response.success(mockMovieResponse)
        coEvery { movieDao.insertMovie(any()) } returns Unit

        // When
        val result = movieRepository.refreshMovies()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(mockMovieResponse, result.data)
        coVerify { movieDao.deleteAllMovies() }
        coVerify { apiService.getMovies() }
        coVerify { movieDao.insertMovie(any()) }
    }

    @Test
    fun `refreshMovies should return error when API fails`() = runTest {
        // Given
        coEvery { movieDao.deleteAllMovies() } returns Unit
        coEvery { apiService.getMovies() } returns Response.error(
            500, "Server Error".toResponseBody()
        )

        // When
        val result = movieRepository.refreshMovies()

        // Then
        assertTrue(result is Resource.Error)
        assertTrue(result.message?.contains("HTTP 500") == true)
        coVerify { movieDao.deleteAllMovies() }
        coVerify { apiService.getMovies() }
        coVerify(exactly = 0) { movieDao.insertMovie(any()) }
    }

    @Test
    fun `refreshMovies should handle HttpException`() = runTest {
        // Given
        coEvery { movieDao.deleteAllMovies() } returns Unit
        coEvery { apiService.getMovies() } throws HttpException(
            Response.error<MovieResponse>(503, "Service Unavailable".toResponseBody())
        )

        // When
        val result = movieRepository.refreshMovies()

        // Then
        assertTrue(result is Resource.Error)
        assertTrue(result.message?.contains("HTTP Exception") == true)
        coVerify { movieDao.deleteAllMovies() }
    }

    @Test
    fun `refreshMovies should handle IOException`() = runTest {
        // Given
        coEvery { movieDao.deleteAllMovies() } returns Unit
        coEvery { apiService.getMovies() } throws IOException("Connection timeout")

        // When
        val result = movieRepository.refreshMovies()

        // Then
        assertTrue(result is Resource.Error)
        assertTrue(result.message?.contains("Network error") == true)
        coVerify { movieDao.deleteAllMovies() }
    }

    @Test
    fun `refreshMovies should handle unexpected exceptions`() = runTest {
        // Given
        coEvery { movieDao.deleteAllMovies() } returns Unit
        coEvery { apiService.getMovies() } throws IllegalStateException("Invalid state")

        // When
        val result = movieRepository.refreshMovies()

        // Then
        assertTrue(result is Resource.Error)
        assertTrue(result.message?.contains("Unexpected error") == true)
        coVerify { movieDao.deleteAllMovies() }
    }

    @Test
    fun `refreshMovies should return error when API returns null body`() = runTest {
        // Given
        coEvery { movieDao.deleteAllMovies() } returns Unit
        coEvery { apiService.getMovies() } returns Response.success(null)

        // When
        val result = movieRepository.refreshMovies()

        // Then
        assertTrue(result is Resource.Error)
        assertEquals("Empty response body", result.message)
        coVerify { movieDao.deleteAllMovies() }
        coVerify { apiService.getMovies() }
        coVerify(exactly = 0) { movieDao.insertMovie(any()) }
    }

    @Test
    fun `getMovies should store API response in local database`() = runTest {
        // Given
        coEvery { movieDao.getMovie() } returns null
        coEvery { apiService.getMovies() } returns Response.success(mockMovieResponse)
        coEvery { movieDao.insertMovie(any()) } returns Unit

        // When
        movieRepository.getMovies()

        // Then
        coVerify {
            movieDao.insertMovie(match {
                it.response == gson.toJson(mockMovieResponse)
            })
        }
    }

    @Test
    fun `refreshMovies should store fresh API response in local database`() = runTest {
        // Given
        coEvery { movieDao.deleteAllMovies() } returns Unit
        coEvery { apiService.getMovies() } returns Response.success(mockMovieResponse)
        coEvery { movieDao.insertMovie(any()) } returns Unit

        // When
        movieRepository.refreshMovies()

        // Then
        coVerify {
            movieDao.insertMovie(match {
                it.response == gson.toJson(mockMovieResponse)
            })
        }
    }

    @Test
    fun `getMovies should handle malformed local data gracefully`() = runTest {
        // Given
        val malformedLocalData = MovieEntity(response = "invalid json")
        coEvery { movieDao.getMovie() } returns malformedLocalData

        // When
        val result = movieRepository.getMovies()

        // Then
        assertTrue(result is Resource.Error)
        assertTrue(result.message?.contains("Unexpected error") == true)
    }

    @Test
    fun `getMovies should handle empty local data`() = runTest {
        // Given
        val emptyLocalData = MovieEntity(response = "")
        coEvery { movieDao.getMovie() } returns emptyLocalData

        // When
        val result = movieRepository.getMovies()

        // Then
        // Empty string might be parsed as null by Gson, so we expect success with null data
        assertTrue(result is Resource.Success)
        assertNull(result.data)
    }

    @Test
    fun `getMovies should handle null local data gracefully`() = runTest {
        // Given
        coEvery { movieDao.getMovie() } returns null
        coEvery { apiService.getMovies() } returns Response.success(mockMovieResponse)
        coEvery { movieDao.insertMovie(any()) } returns Unit

        // When
        val result = movieRepository.getMovies()

        // Then
        assertTrue(result is Resource.Success)
        assertEquals(mockMovieResponse, result.data)
    }

    @Test
    fun `refreshMovies should handle database deletion errors`() = runTest {
        // Given
        coEvery { movieDao.deleteAllMovies() } throws RuntimeException("Database error")
        coEvery { apiService.getMovies() } returns Response.success(mockMovieResponse)

        // When
        val result = movieRepository.refreshMovies()

        // Then
        assertTrue(result is Resource.Error)
        assertTrue(result.message?.contains("Unexpected error") == true)
        coVerify { movieDao.deleteAllMovies() }
        coVerify(exactly = 0) { apiService.getMovies() }
    }

    @Test
    fun `getMovies should handle database insertion errors`() = runTest {
        // Given
        coEvery { movieDao.getMovie() } returns null
        coEvery { apiService.getMovies() } returns Response.success(mockMovieResponse)
        coEvery { movieDao.insertMovie(any()) } throws RuntimeException("Insert error")

        // When
        val result = movieRepository.getMovies()

        // Then
        assertTrue(result is Resource.Error)
        assertTrue(result.message?.contains("Unexpected error") == true)
        coVerify { movieDao.insertMovie(any()) }
    }

    @Test
    fun `refreshMovies should handle database insertion errors`() = runTest {
        // Given
        coEvery { movieDao.deleteAllMovies() } returns Unit
        coEvery { apiService.getMovies() } returns Response.success(mockMovieResponse)
        coEvery { movieDao.insertMovie(any()) } throws RuntimeException("Insert error")

        // When
        val result = movieRepository.refreshMovies()

        // Then
        assertTrue(result is Resource.Error)
        assertTrue(result.message?.contains("Unexpected error") == true)
        coVerify { movieDao.insertMovie(any()) }
    }

    @Test
    fun `getMovies should handle database query errors`() = runTest {
        // Given
        coEvery { movieDao.getMovie() } throws RuntimeException("Query error")
        coEvery { apiService.getMovies() } returns Response.success(mockMovieResponse)
        coEvery { movieDao.insertMovie(any()) } returns Unit

        // When
        val result = movieRepository.getMovies()

        // Then
        assertTrue(result is Resource.Error)
        assertTrue(result.message?.contains("Unexpected error") == true)
        coVerify { movieDao.getMovie() }
        coVerify(exactly = 0) { apiService.getMovies() }
    }

    @Test
    fun `getMovies should return success with correct data structure`() = runTest {
        // Given
        coEvery { movieDao.getMovie() } returns null
        coEvery { apiService.getMovies() } returns Response.success(mockMovieResponse)
        coEvery { movieDao.insertMovie(any()) } returns Unit

        // When
        val result = movieRepository.getMovies()

        // Then
        assertTrue(result is Resource.Success)
        assertNotNull(result.data)
        assertEquals(2, result.data?.movies?.size)
        assertEquals(4, result.data?.genres?.size)
        assertEquals("Test Movie 1", result.data?.movies?.get(0)?.title)
        assertEquals("Test Movie 2", result.data?.movies?.get(1)?.title)
    }

    @Test
    fun `refreshMovies should return success with correct data structure`() = runTest {
        // Given
        coEvery { movieDao.deleteAllMovies() } returns Unit
        coEvery { apiService.getMovies() } returns Response.success(mockMovieResponse)
        coEvery { movieDao.insertMovie(any()) } returns Unit

        // When
        val result = movieRepository.refreshMovies()

        // Then
        assertTrue(result is Resource.Success)
        assertNotNull(result.data)
        assertEquals(2, result.data?.movies?.size)
        assertEquals(4, result.data?.genres?.size)
        assertEquals("Test Movie 1", result.data?.movies?.get(0)?.title)
        assertEquals("Test Movie 2", result.data?.movies?.get(1)?.title)
    }
} 