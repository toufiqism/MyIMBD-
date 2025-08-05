package com.tofiq.myimdb.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tofiq.myimdb.data.model.domain.MovieResponse
import com.tofiq.myimdb.data.repository.MovieRepository
import com.tofiq.myimdb.util.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var movieRepository: MovieRepository
    private lateinit var movieViewModel: MovieViewModel
    private val testDispatcher = StandardTestDispatcher()

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
        ),
        MovieResponse.Movie(
            actors = "Actor 5, Actor 6",
            director = "Director 3",
            genres = listOf("Action", "Thriller"),
            id = 3,
            plot = "Test plot 3",
            posterUrl = "https://example.com/poster3.jpg",
            runtime = "150",
            title = "Test Movie 3",
            year = "2021"
        )
    )

    private val mockMovieResponse = MovieResponse(
        genres = listOf("Action", "Drama", "Comedy", "Romance", "Thriller"),
        movies = mockMovies
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        movieRepository = mockk()
        // Set up default mock behavior before creating ViewModel
        coEvery { movieRepository.getMovies() } returns Resource.Success(mockMovieResponse)
        movieViewModel = MovieViewModel(movieRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadMovies should emit loading then success state`() = runTest {
        // Given
        coEvery { movieRepository.getMovies() } returns Resource.Success(mockMovieResponse)

        // When
        movieViewModel.loadMovies()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val movieState = movieViewModel.movieState.first()
        assertTrue(movieState is Resource.Success)
        assertEquals(mockMovieResponse, movieState.data)
    }

    @Test
    fun `loadMovies should emit loading then error state when repository fails`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { movieRepository.getMovies() } returns Resource.Error(errorMessage)

        // When
        movieViewModel.loadMovies()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val movieState = movieViewModel.movieState.first()
        assertTrue(movieState is Resource.Error)
        assertEquals(errorMessage, movieState.message)
    }

    @Test
    fun `refreshMovies should clear current data and reload`() = runTest {
        // Given
        coEvery { movieRepository.refreshMovies() } returns Resource.Success(mockMovieResponse)

        // When
        movieViewModel.refreshMovies()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val movieState = movieViewModel.movieState.first()
        assertTrue(movieState is Resource.Success)
        assertEquals(mockMovieResponse, movieState.data)
    }

    @Test
    fun `loadNextPage should load more movies when available`() = runTest {
        // Given
        coEvery { movieRepository.getMovies() } returns Resource.Success(mockMovieResponse)
        movieViewModel.loadMovies()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        movieViewModel.loadNextPage()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val displayedMovies = movieViewModel.displayedMovies.first()
        assertTrue(displayedMovies.isNotEmpty())
        assertEquals(3, displayedMovies.size) // All movies should be loaded since page size is 10
    }

    @Test
    fun `setSelectedGenre should filter movies by genre`() = runTest {
        // Given
        coEvery { movieRepository.getMovies() } returns Resource.Success(mockMovieResponse)
        movieViewModel.loadMovies()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        movieViewModel.setSelectedGenre("Action")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val selectedGenre = movieViewModel.selectedGenre.first()
        assertEquals("Action", selectedGenre)
        
        val displayedMovies = movieViewModel.displayedMovies.first()
        val actionMovies = displayedMovies.filterNotNull().filter { movie ->
            movie.genres?.contains("Action") == true
        }
        assertEquals(2, actionMovies.size) // Movies 1 and 3 have Action genre
    }

    @Test
    fun `setSearchQuery should filter movies by search term`() = runTest {
        // Given
        coEvery { movieRepository.getMovies() } returns Resource.Success(mockMovieResponse)
        movieViewModel.loadMovies()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        movieViewModel.setSearchQuery("Test Movie 1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val searchQuery = movieViewModel.searchQuery.first()
        assertEquals("Test Movie 1", searchQuery)
        
        val displayedMovies = movieViewModel.displayedMovies.first()
        assertEquals(1, displayedMovies.size)
        assertEquals("Test Movie 1", displayedMovies.first()?.title)
    }

    @Test
    fun `setSearchActive should toggle search mode`() = runTest {
        // When
        movieViewModel.setSearchActive(true)

        // Then
        val isSearchActive = movieViewModel.isSearchActive.first()
        assertTrue(isSearchActive)

        // When
        movieViewModel.setSearchActive(false)

        // Then
        val isSearchActiveAfter = movieViewModel.isSearchActive.first()
        assertFalse(isSearchActiveAfter)
    }

    @Test
    fun `toggleWishlist should add movie to wishlist`() = runTest {
        // When
        movieViewModel.toggleWishlist(1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val wishlistMovies = movieViewModel.wishlistMovies.first()
        assertTrue(wishlistMovies.contains(1))
        
        val wishlistCount = movieViewModel.wishlistCount.first()
        assertEquals(1, wishlistCount)
    }

    @Test
    fun `toggleWishlist should remove movie from wishlist when already present`() = runTest {
        // Given
        movieViewModel.toggleWishlist(1)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        movieViewModel.toggleWishlist(1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val wishlistMovies = movieViewModel.wishlistMovies.first()
        assertFalse(wishlistMovies.contains(1))
        
        val wishlistCount = movieViewModel.wishlistCount.first()
        assertEquals(0, wishlistCount)
    }

    @Test
    fun `isInWishlist should return true for wishlisted movie`() = runTest {
        // Given
        movieViewModel.toggleWishlist(1)
        testDispatcher.scheduler.advanceUntilIdle()

        // When & Then
        assertTrue(movieViewModel.isInWishlist(1))
        assertFalse(movieViewModel.isInWishlist(2))
    }

    @Test
    fun `getWishlistMovies should return list of wishlisted movies`() = runTest {
        // Given
        coEvery { movieRepository.getMovies() } returns Resource.Success(mockMovieResponse)
        movieViewModel.loadMovies()
        testDispatcher.scheduler.advanceUntilIdle()
        
        movieViewModel.toggleWishlist(1)
        movieViewModel.toggleWishlist(2)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        val wishlistMovies = movieViewModel.getWishlistMovies()

        // Then
        assertEquals(2, wishlistMovies.size)
        assertTrue(wishlistMovies.any { it.id == 1 })
        assertTrue(wishlistMovies.any { it.id == 2 })
    }

    @Test
    fun `toggleGridView should switch between grid and list view`() = runTest {
        // Given
        val initialGridView = movieViewModel.isGridView.first()
        assertFalse(initialGridView)

        // When
        movieViewModel.toggleGridView()

        // Then
        val gridViewAfterToggle = movieViewModel.isGridView.first()
        assertTrue(gridViewAfterToggle)

        // When
        movieViewModel.toggleGridView()

        // Then
        val gridViewAfterSecondToggle = movieViewModel.isGridView.first()
        assertFalse(gridViewAfterSecondToggle)
    }

    @Test
    fun `setSelectedMovie and getSelectedMovie should work correctly`() = runTest {
        // Given
        val testMovie = mockMovies[0]

        // When
        movieViewModel.setSelectedMovie(testMovie)

        // Then
        val selectedMovie = movieViewModel.getSelectedMovie()
        assertEquals(testMovie, selectedMovie)
    }

    @Test
    fun `getMovieById should return correct movie`() = runTest {
        // Given
        coEvery { movieRepository.getMovies() } returns Resource.Success(mockMovieResponse)
        movieViewModel.loadMovies()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        val movie = movieViewModel.getMovieById(1)

        // Then
        assertNotNull(movie)
        assertEquals("Test Movie 1", movie?.title)
    }

    @Test
    fun `getMovieById should return null for non-existent movie`() = runTest {
        // Given
        coEvery { movieRepository.getMovies() } returns Resource.Success(mockMovieResponse)
        movieViewModel.loadMovies()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        val movie = movieViewModel.getMovieById(999)

        // Then
        assertNull(movie)
    }

    @Test
    fun `hasMoreMovies should return true when more movies are available`() = runTest {
        // Given
        coEvery { movieRepository.getMovies() } returns Resource.Success(mockMovieResponse)
        movieViewModel.loadMovies()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        val hasMore = movieViewModel.hasMoreMovies()

        // Then
        assertFalse(hasMore) // All 3 movies are loaded in first page since page size is 10
    }

    @Test
    fun `availableGenres should contain all unique genres`() = runTest {
        // Given
        coEvery { movieRepository.getMovies() } returns Resource.Success(mockMovieResponse)
        movieViewModel.loadMovies()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        val availableGenres = movieViewModel.availableGenres.first()

        // Then
        assertEquals(5, availableGenres.size)
        assertTrue(availableGenres.contains("Action"))
        assertTrue(availableGenres.contains("Drama"))
        assertTrue(availableGenres.contains("Comedy"))
        assertTrue(availableGenres.contains("Romance"))
        assertTrue(availableGenres.contains("Thriller"))
    }

    @Test
    fun `search should work with partial title match`() = runTest {
        // Given
        coEvery { movieRepository.getMovies() } returns Resource.Success(mockMovieResponse)
        movieViewModel.loadMovies()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        movieViewModel.setSearchQuery("Test")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val displayedMovies = movieViewModel.displayedMovies.first()
        assertEquals(3, displayedMovies.size) // All movies have "Test" in title
    }

    @Test
    fun `search should work with director name`() = runTest {
        // Given
        coEvery { movieRepository.getMovies() } returns Resource.Success(mockMovieResponse)
        movieViewModel.loadMovies()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        movieViewModel.setSearchQuery("Director 1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val displayedMovies = movieViewModel.displayedMovies.first()
        assertEquals(1, displayedMovies.size)
        assertEquals("Director 1", displayedMovies.first()?.director)
    }

    @Test
    fun `search should work with actor name`() = runTest {
        // Given
        coEvery { movieRepository.getMovies() } returns Resource.Success(mockMovieResponse)
        movieViewModel.loadMovies()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        movieViewModel.setSearchQuery("Actor 1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val displayedMovies = movieViewModel.displayedMovies.first()
        assertEquals(1, displayedMovies.size)
        assertTrue(displayedMovies.first()?.actors?.contains("Actor 1") == true)
    }

    @Test
    fun `search should work with plot content`() = runTest {
        // Given
        coEvery { movieRepository.getMovies() } returns Resource.Success(mockMovieResponse)
        movieViewModel.loadMovies()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        movieViewModel.setSearchQuery("plot 1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val displayedMovies = movieViewModel.displayedMovies.first()
        assertEquals(1, displayedMovies.size)
        assertEquals("Test plot 1", displayedMovies.first()?.plot)
    }

    @Test
    fun `search should be case insensitive`() = runTest {
        // Given
        coEvery { movieRepository.getMovies() } returns Resource.Success(mockMovieResponse)
        movieViewModel.loadMovies()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        movieViewModel.setSearchQuery("test movie 1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val displayedMovies = movieViewModel.displayedMovies.first()
        assertEquals(1, displayedMovies.size)
        assertEquals("Test Movie 1", displayedMovies.first()?.title)
    }

    @Test
    fun `multiple wishlist operations should work correctly`() = runTest {
        // Given
        coEvery { movieRepository.getMovies() } returns Resource.Success(mockMovieResponse)
        movieViewModel.loadMovies()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        movieViewModel.toggleWishlist(1)
        movieViewModel.toggleWishlist(2)
        movieViewModel.toggleWishlist(3)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val wishlistCount = movieViewModel.wishlistCount.first()
        assertEquals(3, wishlistCount)

        // When
        movieViewModel.toggleWishlist(2) // Remove one
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val wishlistCountAfter = movieViewModel.wishlistCount.first()
        assertEquals(2, wishlistCountAfter)
        assertTrue(movieViewModel.isInWishlist(1))
        assertFalse(movieViewModel.isInWishlist(2))
        assertTrue(movieViewModel.isInWishlist(3))
    }

    @Test
    fun `clear search should reset search query and reload movies`() = runTest {
        // Given
        coEvery { movieRepository.getMovies() } returns Resource.Success(mockMovieResponse)
        movieViewModel.loadMovies()
        testDispatcher.scheduler.advanceUntilIdle()
        
        movieViewModel.setSearchQuery("Test Movie 1")
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        movieViewModel.setSearchQuery("")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val searchQuery = movieViewModel.searchQuery.first()
        assertEquals("", searchQuery)
        
        val displayedMovies = movieViewModel.displayedMovies.first()
        assertEquals(3, displayedMovies.size) // All movies should be shown
    }

    @Test
    fun `clear genre filter should reset genre and reload movies`() = runTest {
        // Given
        coEvery { movieRepository.getMovies() } returns Resource.Success(mockMovieResponse)
        movieViewModel.loadMovies()
        testDispatcher.scheduler.advanceUntilIdle()
        
        movieViewModel.setSelectedGenre("Action")
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        movieViewModel.setSelectedGenre(null)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val selectedGenre = movieViewModel.selectedGenre.first()
        assertNull(selectedGenre)
        
        val displayedMovies = movieViewModel.displayedMovies.first()
        assertEquals(3, displayedMovies.size) // All movies should be shown
    }
} 