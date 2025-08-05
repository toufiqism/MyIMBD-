# MyIMBD-
A Simple but well-architected IMDB app using modern Android development tools and principles

## Architecture Overview

This project follows Clean Architecture principles with MVVM pattern and uses modern Android development tools:

### Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Clean Architecture
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp
- **Reactive Programming**: Kotlin Coroutines + StateFlow
- **Database**: Room (implemented with local caching)
- **Navigation**: Compose Navigation
- **Image Loading**: Coil

### Project Structure

```
app/src/main/java/com/tofiq/myimdb/
├── data/
│   ├── local/
│   │   ├── dao/
│   │   │   └── MovieEntityDAO.kt        # Room DAO for database operations
│   │   ├── entity/
│   │   │   └── MovieEntity.kt           # Room entity for local storage
│   │   └── MovieDB.kt                   # Room database configuration
│   ├── model/
│   │   └── domain/
│   │       └── MovieResponse.kt          # Data models
│   ├── remote/
│   │   └── MovieApiService.kt            # API service interface
│   └── repository/
│       ├── MovieRepository.kt            # Repository interface
│       └── MovieRepositoryImpl.kt        # Repository implementation
├── di/
│   └── NetworkModule.kt                  # Dependency injection modules
├── ui/
│   ├── screens/
│   │   ├── SplashScreen.kt              # Splash screen with loading animation
│   │   └── HomeScreen.kt                # Home screen displaying movies
│   ├── viewmodel/
│   │   └── MovieViewModel.kt             # ViewModel with StateFlow
│   └── theme/                            # UI theme components
├── util/
│   └── Resource.kt                       # Resource wrapper for API responses
├── MainActivity.kt                       # Main activity with Compose Navigation
└── MyIMDBApplication.kt                  # Application class for Hilt
```

## Features

### Completed Features

1. **Splash Screen**
   - Animated loading screen with app branding
   - Handles initial data loading from API or local database
   - Automatic navigation to home screen after data loading
   - Graceful error handling with fallback navigation

2. **Navigation System**
   - Compose Navigation implementation
   - Splash screen as start destination
   - Home screen for movie display
   - Proper navigation flow with back stack management

3. **Local Database Integration**
   - Room database for caching movie data
   - Automatic data persistence from API responses
   - Offline-first approach: loads cached data first, then refreshes from API
   - Efficient data storage using JSON serialization

4. **API Service Layer**
   - `MovieApiService`: Retrofit interface for fetching movies from external API
   - Base URL: `https://raw.githubusercontent.com/erik-sytnyk/movies-list/master/`
   - Endpoint: `db.json` - Returns movie data in JSON format

5. **Repository Pattern with Caching**
   - `MovieRepository`: Interface defining data operations
   - `MovieRepositoryImpl`: Implementation with local database integration
   - Smart caching: checks local database first, then API
   - Refresh functionality to clear cache and fetch fresh data
   - Comprehensive error handling for all scenarios

6. **ViewModel with Enhanced State Management**
   - `MovieViewModel`: Manages UI state using StateFlow
   - Separate loading states for initial load and refresh operations
   - Provides loading, success, and error states
   - Supports manual refresh functionality
   - Optimized for both online and offline scenarios

7. **Dependency Injection**
   - Hilt modules for network and database dependencies
   - Singleton scoped API service, database, and repository
   - Proper injection into ViewModels and Activities

8. **Resource Wrapper**
   - `Resource<T>` sealed class for handling API responses
   - Three states: Loading, Success, and Error
   - Provides consistent error handling across the app

### Data Models

```kotlin
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
```

### Local Database Schema

```kotlin
@Entity(tableName = "movie")
data class MovieEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val response: String  // JSON string of MovieResponse
)
```

### State Management

The app uses StateFlow for reactive state management:

- **Loading State**: Shows loading indicator with animated progress
- **Success State**: Displays movie data in a scrollable list with pagination
- **Error State**: Shows error message with retry option
- **Cached Data**: Displays cached data while refreshing in background
- **Pagination State**: Manages loading more movies with separate loading states

### Pagination Implementation

The app implements efficient pagination with the following features:

- **Page Size**: 10 movies per page for optimal performance
- **Smart Loading**: Automatically loads more content when user scrolls near the end
- **Loading States**: Separate loading indicators for initial load and pagination
- **Memory Efficient**: Only displays loaded movies, not all movies at once
- **Scroll Detection**: Uses LazyListState to detect when to load more content
- **End Detection**: Shows appropriate message when all movies are loaded

### Movie Details Screen

The app features a beautiful, animated details screen with the following characteristics:

- **Hero Section**: Large poster image with gradient overlay for visual impact
- **Animated Entrance**: Smooth slide-in animation when the screen opens
- **Staggered Content**: Information animates in sequence for a polished look
- **Genre Tags**: Beautiful pill-shaped tags with Material Design colors
- **Comprehensive Information**: Shows all movie details including plot, director, cast, and runtime
- **Smooth Navigation**: Back button with semi-transparent background
- **Responsive Design**: Adapts to different screen sizes and orientations
- **Loading States**: Proper loading indicators for image loading
- **Error Handling**: Graceful fallback for missing images

### Genre Filtering System

The app features a comprehensive genre filtering system:

- **Dynamic Genre Extraction**: Automatically extracts all unique genres from the movie database
- **Filter Dropdown**: Beautiful Material Design dialog with all available genres
- **Active Filter Indicator**: Shows currently selected genre with clear filter option
- **Filter Persistence**: Maintains selected filter during pagination and refresh operations
- **Real-time Filtering**: Instantly filters movies based on selected genre
- **Clear Filter Option**: Easy way to remove filter and show all movies
- **Responsive Design**: Filter dropdown adapts to different screen sizes

### Performance Optimization

The app is optimized for performance with the following characteristics:

- **Clean Code**: Simplified list UI with optimized details screen animations
- **Efficient Rendering**: Smart animation timing and easing functions
- **Fast Loading**: Quick display of content with progressive animation loading
- **Memory Efficient**: Optimized image loading and state management
- **Smooth Scrolling**: Native scrolling performance with minimal interference
- **Responsive UI**: Immediate response to user interactions
- **Material Design**: Follows Material Design principles with beautiful aesthetics

### Common App Bar Component

The app features a unified app bar design across all screens:

- **Consistent Design**: Same visual style and behavior throughout the app
- **Flexible Configuration**: Supports back button, refresh button, and custom titles
- **Loading States**: Disables buttons during loading operations
- **Material Design**: Follows Material Design 3 guidelines
- **Responsive**: Adapts to different screen sizes and orientations
- **Accessibility**: Proper content descriptions for screen readers

### Navigation Flow

1. **Splash Screen**: 
   - Shows app branding and loading animation
   - Common app bar with app title
   - Loads data from local database or API
   - Navigates to home screen automatically

2. **Home Screen**:
   - Displays movies in a simplified card-based list
   - Common app bar with refresh functionality
   - Clickable cards for navigation to details
   - Pull-to-refresh functionality
   - Error handling with retry options
   - Responsive design for different screen sizes

3. **Movie Details Screen**:
   - Beautiful animated details page
   - Common app bar with back navigation
   - Direct data passing through ViewModel
   - Smooth navigation transitions
   - Comprehensive movie information display

### Error Handling

Comprehensive error handling for:
- HTTP exceptions (4xx, 5xx errors)
- Network connectivity issues
- JSON parsing errors
- Database operations
- Unexpected exceptions
- Graceful fallback to cached data

## Setup and Installation

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle dependencies
4. Run the app

## Dependencies

Key dependencies used:
- `com.google.dagger:hilt-android` - Dependency injection
- `com.squareup.retrofit2:retrofit` - HTTP client
- `com.squareup.retrofit2:converter-gson` - JSON parsing
- `com.squareup.okhttp3:logging-interceptor` - Network logging
- `androidx.room:room-runtime` - Local database
- `androidx.room:room-compiler` - Room annotation processor
- `androidx.navigation:navigation-compose` - Compose navigation
- `androidx.lifecycle:lifecycle-viewmodel-compose` - ViewModel integration
- `androidx.lifecycle:lifecycle-runtime-compose` - Compose lifecycle

## User Experience Features

### Splash Screen
- Beautiful animated loading indicator
- App branding with "MyIMDB" title
- Dynamic status messages during loading
- Automatic navigation after data loading

### Home Screen
- Material Design 3 components
- Enhanced card-based movie display with poster images
- Horizontal layout showing movie poster, title, year, genre, director, and plot
- Image loading with Coil library for smooth poster display
- Refresh button in app bar
- Responsive layout for different screen sizes
- Pull-to-refresh functionality
- Error states with retry options

### Performance Optimizations
- Local database caching for offline support
- Efficient JSON serialization/deserialization
- Lazy loading of movie lists
- Optimized database queries
- Background data refresh

## Recent Updates

### Movie List Screen Enhancement (Latest)
- **Genre Filtering**: Filter dropdown in app bar with all available genres from the movie database
- **Filter UI**: Beautiful dialog-based filter dropdown with Material Design styling
- **Active Filter Indicator**: Shows currently selected genre with clear filter option
- **Dynamic Genre List**: Automatically extracts and displays all unique genres from loaded movies
- **Filter Persistence**: Maintains selected filter during pagination and refresh operations
- **Common App Bar**: Consistent app bar design across all screens with unified navigation
- **Simplified List View**: Clean, minimal movie cards showing only essential information (title, year, genre)
- **Movie Details Screen**: Beautiful, animated details page with full movie information
- **Navigation**: Smooth navigation between list and details with transition animations
- **Hero Section**: Large poster image with gradient overlay and animated title
- **Staggered Animations**: Content animates in sequence for a polished experience
- **Genre Tags**: Beautiful pill-shaped genre tags with Material Design colors
- **Pagination Support**: Loads 10 movies initially and loads 10 more when scrolling near the end
- **Smart Loading**: Automatically detects when user scrolls near the end to load more content
- **Loading Indicators**: Shows loading indicator at the bottom while fetching more movies
- **End of List Indicator**: Displays a message when all movies have been loaded
- **Poster Image Display**: Added movie poster images using Coil library for smooth loading
- **Placeholder Images**: Shows a movie icon placeholder when poster images are unavailable or fail to load
- **Loading States**: Displays a loading indicator while images are being fetched
- **Improved Layout**: Changed from vertical to horizontal card layout for better visual appeal
- **Better Typography**: Improved text hierarchy with proper font sizes and colors
- **Responsive Design**: Optimized for different screen sizes with proper spacing and text wrapping
- **Performance Optimized**: Clean, efficient code with optimized animations for better performance

## Future Enhancements

Potential improvements for the app:
- Search functionality with local database queries
- Movie details screen with full information
- Pagination for large datasets
- Unit tests for ViewModels and Repository
- UI tests for Compose screens
- Dark/Light theme support
- Movie favorites functionality
- Advanced filtering and sorting options
