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
- **Database**: Room (configured but not yet implemented)
- **Image Loading**: Coil

### Project Structure

```
app/src/main/java/com/tofiq/myimdb/
├── data/
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
│   ├── viewmodel/
│   │   └── MovieViewModel.kt             # ViewModel with StateFlow
│   └── theme/                            # UI theme components
├── util/
│   └── Resource.kt                       # Resource wrapper for API responses
├── MainActivity.kt                       # Main activity with Compose UI
└── MyIMDBApplication.kt                  # Application class for Hilt
```

## API Integration

### Completed Features

1. **API Service Layer**
   - `MovieApiService`: Retrofit interface for fetching movies from external API
   - Base URL: `https://raw.githubusercontent.com/erik-sytnyk/movies-list/master/`
   - Endpoint: `db.json` - Returns movie data in JSON format

2. **Repository Pattern**
   - `MovieRepository`: Interface defining data operations
   - `MovieRepositoryImpl`: Implementation with proper error handling
   - Handles HTTP exceptions, network errors, and unexpected errors
   - Returns `Resource<T>` wrapper for consistent error handling

3. **ViewModel with StateFlow**
   - `MovieViewModel`: Manages UI state using StateFlow
   - Provides loading, success, and error states
   - Automatically fetches movies on initialization
   - Supports manual refresh functionality

4. **Dependency Injection**
   - Hilt modules for network dependencies
   - Singleton scoped API service and repository
   - Proper injection into ViewModels and Activities

5. **Resource Wrapper**
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

### State Management

The app uses StateFlow for reactive state management:

- **Loading State**: Shows loading indicator
- **Success State**: Displays movie data
- **Error State**: Shows error message with retry option

### Error Handling

Comprehensive error handling for:
- HTTP exceptions (4xx, 5xx errors)
- Network connectivity issues
- JSON parsing errors
- Unexpected exceptions

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
- `androidx.lifecycle:lifecycle-viewmodel-compose` - ViewModel integration
- `androidx.lifecycle:lifecycle-runtime-compose` - Compose lifecycle

## Next Steps

The API integration is complete and ready for UI implementation. Future enhancements could include:
- Local database caching with Room
- Search functionality
- Movie details screen
- Image loading with Coil
- Pagination for large datasets
- Unit tests for ViewModels and Repository
- UI tests for Compose screens
