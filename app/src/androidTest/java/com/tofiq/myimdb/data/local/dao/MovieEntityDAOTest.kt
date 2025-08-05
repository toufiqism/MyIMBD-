package com.tofiq.myimdb.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tofiq.myimdb.data.local.MovieDB
import com.tofiq.myimdb.data.local.entity.MovieEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieEntityDAOTest {

    private lateinit var movieDB: MovieDB
    private lateinit var movieDao: MovieEntityDAO

    @Before
    fun setup() {
        movieDB = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MovieDB::class.java
        ).allowMainThreadQueries().build()
        movieDao = movieDB.movieDao()
    }

    @After
    fun tearDown() {
        movieDB.close()
    }

    @Test
    fun insertMovie_should_store_movie_entity_in_database() = runTest {
        // Given
        val movieEntity = MovieEntity(response = "{\"test\": \"data\"}")

        // When
        movieDao.insertMovie(movieEntity)

        // Then
        val retrievedMovie = movieDao.getMovie()
        assertNotNull(retrievedMovie)
        assertEquals("{\"test\": \"data\"}", retrievedMovie?.response)
    }

    @Test
    fun insertMovie_should_replace_existing_movie_when_using_REPLACE_strategy() = runTest {
        // Given
        val firstMovie = MovieEntity(response = "{\"first\": \"data\"}")
        val secondMovie = MovieEntity(response = "{\"second\": \"data\"}")

        // When
        movieDao.insertMovie(firstMovie)
        movieDao.insertMovie(secondMovie)

        // Then
        val retrievedMovie = movieDao.getMovie()
        assertNotNull(retrievedMovie)
        // Since we're using auto-generated IDs, the first movie should still be returned
        // as it has the lower ID (1 vs 2)
        assertEquals("{\"first\": \"data\"}", retrievedMovie?.response)
    }

    @Test
    fun getMovie_should_return_null_when_database_is_empty() = runTest {
        // When
        val retrievedMovie = movieDao.getMovie()

        // Then
        assertNull(retrievedMovie)
    }

    @Test
    fun getMovie_should_return_the_first_movie_when_multiple_movies_exist() = runTest {
        // Given
        val firstMovie = MovieEntity(response = "{\"first\": \"data\"}")
        val secondMovie = MovieEntity(response = "{\"second\": \"data\"}")

        // When
        movieDao.insertMovie(firstMovie)
        movieDao.insertMovie(secondMovie)

        // Then
        val retrievedMovie = movieDao.getMovie()
        assertNotNull(retrievedMovie)
        assertEquals("{\"first\": \"data\"}", retrievedMovie?.response)
    }

    @Test
    fun deleteAllMovies_should_remove_all_movies_from_database() = runTest {
        // Given
        val firstMovie = MovieEntity(response = "{\"first\": \"data\"}")
        val secondMovie = MovieEntity(response = "{\"second\": \"data\"}")
        movieDao.insertMovie(firstMovie)
        movieDao.insertMovie(secondMovie)

        // Verify movies exist
        assertNotNull(movieDao.getMovie())

        // When
        movieDao.deleteAllMovies()

        // Then
        val retrievedMovie = movieDao.getMovie()
        assertNull(retrievedMovie)
    }

    @Test
    fun deleteAllMovies_should_work_when_database_is_already_empty() = runTest {
        // Given
        assertNull(movieDao.getMovie())

        // When
        movieDao.deleteAllMovies()

        // Then
        val retrievedMovie = movieDao.getMovie()
        assertNull(retrievedMovie)
    }

    @Test
    fun insertMovie_should_handle_large_JSON_response() = runTest {
        // Given
        val largeResponse = """
            {
                "genres": ["Action", "Drama", "Comedy", "Romance", "Thriller", "Horror", "Sci-Fi", "Fantasy"],
                "movies": [
                    {
                        "id": 1,
                        "title": "Test Movie 1",
                        "year": "2023",
                        "director": "Director 1",
                        "actors": "Actor 1, Actor 2, Actor 3, Actor 4, Actor 5",
                        "plot": "This is a very long plot description that contains a lot of text to test the database's ability to handle large JSON responses. It should work correctly without any issues.",
                        "runtime": "120",
                        "genres": ["Action", "Drama"],
                        "posterUrl": "https://example.com/poster1.jpg"
                    },
                    {
                        "id": 2,
                        "title": "Test Movie 2",
                        "year": "2022",
                        "director": "Director 2",
                        "actors": "Actor 6, Actor 7, Actor 8, Actor 9, Actor 10",
                        "plot": "Another long plot description with even more text to ensure the database can handle substantial amounts of data without any problems.",
                        "runtime": "150",
                        "genres": ["Comedy", "Romance"],
                        "posterUrl": "https://example.com/poster2.jpg"
                    }
                ]
            }
        """.trimIndent()

        val movieEntity = MovieEntity(response = largeResponse)

        // When
        movieDao.insertMovie(movieEntity)

        // Then
        val retrievedMovie = movieDao.getMovie()
        assertNotNull(retrievedMovie)
        assertEquals(largeResponse, retrievedMovie?.response)
    }

    @Test
    fun insertMovie_should_handle_special_characters_in_JSON() = runTest {
        // Given
        val specialCharsResponse = """
            {
                "genres": ["Action & Adventure", "Sci-Fi & Fantasy"],
                "movies": [
                    {
                        "id": 1,
                        "title": "Movie with \"quotes\" and 'apostrophes'",
                        "year": "2023",
                        "director": "Director with & symbol",
                        "actors": "Actor 1, Actor 2, Actor 3",
                        "plot": "Plot with special characters: & < > \" ' \\ /",
                        "runtime": "120",
                        "genres": ["Action & Adventure"],
                        "posterUrl": "https://example.com/poster1.jpg"
                    }
                ]
            }
        """.trimIndent()

        val movieEntity = MovieEntity(response = specialCharsResponse)

        // When
        movieDao.insertMovie(movieEntity)

        // Then
        val retrievedMovie = movieDao.getMovie()
        assertNotNull(retrievedMovie)
        assertEquals(specialCharsResponse, retrievedMovie?.response)
    }

    @Test
    fun insertMovie_should_handle_empty_JSON_response() = runTest {
        // Given
        val emptyResponse = "{}"
        val movieEntity = MovieEntity(response = emptyResponse)

        // When
        movieDao.insertMovie(movieEntity)

        // Then
        val retrievedMovie = movieDao.getMovie()
        assertNotNull(retrievedMovie)
        assertEquals(emptyResponse, retrievedMovie?.response)
    }

    @Test
    fun insertMovie_should_handle_null_JSON_response() = runTest {
        // Given
        val nullResponse = "null"
        val movieEntity = MovieEntity(response = nullResponse)

        // When
        movieDao.insertMovie(movieEntity)

        // Then
        val retrievedMovie = movieDao.getMovie()
        assertNotNull(retrievedMovie)
        assertEquals(nullResponse, retrievedMovie?.response)
    }

    @Test
    fun insertMovie_should_handle_very_long_movie_titles() = runTest {
        // Given
        val longTitleResponse = """
            {
                "genres": ["Action"],
                "movies": [
                    {
                        "id": 1,
                        "title": "This is a very long movie title that contains many words and should be handled correctly by the database without any truncation or issues. The title goes on and on to test the limits.",
                        "year": "2023",
                        "director": "Director",
                        "actors": "Actors",
                        "plot": "Plot",
                        "runtime": "120",
                        "genres": ["Action"],
                        "posterUrl": "https://example.com/poster.jpg"
                    }
                ]
            }
        """.trimIndent()

        val movieEntity = MovieEntity(response = longTitleResponse)

        // When
        movieDao.insertMovie(movieEntity)

        // Then
        val retrievedMovie = movieDao.getMovie()
        assertNotNull(retrievedMovie)
        assertEquals(longTitleResponse, retrievedMovie?.response)
    }

    @Test
    fun insertMovie_should_handle_multiple_operations_in_sequence() = runTest {
        // Given
        val responses = listOf(
            "{\"test\": \"data1\"}",
            "{\"test\": \"data2\"}",
            "{\"test\": \"data3\"}",
            "{\"test\": \"data4\"}",
            "{\"test\": \"data5\"}"
        )

        // When
        responses.forEach { response ->
            movieDao.insertMovie(MovieEntity(response = response))
        }

        // Then
        val retrievedMovie = movieDao.getMovie()
        assertNotNull(retrievedMovie)
        // Since we're using auto-generated IDs, the first movie should be returned
        // as it has the lowest ID
        assertEquals("{\"test\": \"data1\"}", retrievedMovie?.response)
    }

    @Test
    fun deleteAllMovies_should_work_after_multiple_insertions() = runTest {
        // Given
        val responses = listOf(
            "{\"test\": \"data1\"}",
            "{\"test\": \"data2\"}",
            "{\"test\": \"data3\"}"
        )

        responses.forEach { response ->
            movieDao.insertMovie(MovieEntity(response = response))
        }

        // Verify movies exist
        assertNotNull(movieDao.getMovie())

        // When
        movieDao.deleteAllMovies()

        // Then
        val retrievedMovie = movieDao.getMovie()
        assertNull(retrievedMovie)
    }

    @Test
    fun getMovie_should_return_consistent_results() = runTest {
        // Given
        val movieEntity = MovieEntity(response = "{\"test\": \"data\"}")
        movieDao.insertMovie(movieEntity)

        // When
        val firstRetrieval = movieDao.getMovie()
        val secondRetrieval = movieDao.getMovie()
        val thirdRetrieval = movieDao.getMovie()

        // Then
        assertNotNull(firstRetrieval)
        assertNotNull(secondRetrieval)
        assertNotNull(thirdRetrieval)
        assertEquals(firstRetrieval?.response, secondRetrieval?.response)
        assertEquals(secondRetrieval?.response, thirdRetrieval?.response)
    }

    @Test
    fun insertMovie_should_handle_JSON_with_nested_objects() = runTest {
        // Given
        val nestedJsonResponse = """
            {
                "genres": ["Action"],
                "movies": [
                    {
                        "id": 1,
                        "title": "Test Movie",
                        "year": "2023",
                        "director": "Director",
                        "actors": "Actors",
                        "plot": "Plot",
                        "runtime": "120",
                        "genres": ["Action"],
                        "posterUrl": "https://example.com/poster.jpg",
                        "metadata": {
                            "rating": 8.5,
                            "votes": 1000,
                            "awards": {
                                "oscar": false,
                                "golden_globe": true
                            }
                        }
                    }
                ]
            }
        """.trimIndent()

        val movieEntity = MovieEntity(response = nestedJsonResponse)

        // When
        movieDao.insertMovie(movieEntity)

        // Then
        val retrievedMovie = movieDao.getMovie()
        assertNotNull(retrievedMovie)
        assertEquals(nestedJsonResponse, retrievedMovie?.response)
    }

    @Test
    fun insertMovie_should_handle_JSON_with_arrays() = runTest {
        // Given
        val arrayJsonResponse = """
            {
                "genres": ["Action", "Drama", "Comedy"],
                "movies": [
                    {
                        "id": 1,
                        "title": "Test Movie",
                        "year": "2023",
                        "director": "Director",
                        "actors": ["Actor 1", "Actor 2", "Actor 3"],
                        "plot": "Plot",
                        "runtime": "120",
                        "genres": ["Action", "Drama"],
                        "posterUrl": "https://example.com/poster.jpg",
                        "ratings": [8.5, 9.0, 7.5]
                    }
                ]
            }
        """.trimIndent()

        val movieEntity = MovieEntity(response = arrayJsonResponse)

        // When
        movieDao.insertMovie(movieEntity)

        // Then
        val retrievedMovie = movieDao.getMovie()
        assertNotNull(retrievedMovie)
        assertEquals(arrayJsonResponse, retrievedMovie?.response)
    }

    @Test
    fun insertMovie_should_handle_JSON_with_unicode_characters() = runTest {
        // Given
        val unicodeJsonResponse = """
            {
                "genres": ["Action", "Drama"],
                "movies": [
                    {
                        "id": 1,
                        "title": "Test Movie with Unicode: 测试电影 🎬",
                        "year": "2023",
                        "director": "Director with émojis 🎭",
                        "actors": "Actors with special chars: ñáéíóú",
                        "plot": "Plot with unicode: 这是一个测试情节",
                        "runtime": "120",
                        "genres": ["Action", "Drama"],
                        "posterUrl": "https://example.com/poster.jpg"
                    }
                ]
            }
        """.trimIndent()

        val movieEntity = MovieEntity(response = unicodeJsonResponse)

        // When
        movieDao.insertMovie(movieEntity)

        // Then
        val retrievedMovie = movieDao.getMovie()
        assertNotNull(retrievedMovie)
        assertEquals(unicodeJsonResponse, retrievedMovie?.response)
    }
} 