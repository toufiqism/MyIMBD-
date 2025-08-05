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
- **Success State**: Displays movie data in a scrollable list
- **Error State**: Shows error message with retry option
- **Cached Data**: Displays cached data while refreshing in background

### Navigation Flow

1. **Splash Screen**: 
   - Shows app branding and loading animation
   - Loads data from local database or API
   - Navigates to home screen automatically

2. **Home Screen**:
   - Displays movies in a card-based list
   - Pull-to-refresh functionality
   - Error handling with retry options
   - Responsive design for different screen sizes

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
- Card-based movie display
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

## Future Enhancements

Potential improvements for the app:
- Search functionality with local database queries
- Movie details screen with full information
- Image loading with Coil for movie posters
- Pagination for large datasets
- Unit tests for ViewModels and Repository
- UI tests for Compose screens
- Dark/Light theme support
- Movie favorites functionality
- Advanced filtering and sorting options
