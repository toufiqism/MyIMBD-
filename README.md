# MyIMDB - Movie Database App

A modern Android application built with Jetpack Compose that allows users to browse movies, search, filter, and manage their personal wishlist.

## Features

### Core Features
- **Movie Browsing**: Browse through a comprehensive collection of movies
- **Search Functionality**: Search movies by title, plot, director, actors, or genres
- **Genre Filtering**: Filter movies by specific genres
- **Grid/List View**: Toggle between grid and list view modes
- **Wishlist Management**: Add/remove movies to/from your personal wishlist
- **Offline Support**: Movies are cached locally for offline viewing
- **Responsive Design**: Optimized for different screen sizes

### Wishlist Implementation

The wishlist feature has been implemented using Room database for persistent storage:

#### Database Components
- **WishlistEntity**: Room entity for storing wishlist movies
  - `movieId`: Primary key (movie ID)
  - `title`, `year`, `posterUrl`, `plot`, `director`, `actors`: Movie details
  - `genres`: Stored as comma-separated string
  - `addedAt`: Timestamp when added to wishlist

- **WishlistEntityDAO**: Data Access Object for wishlist operations
  - `getAllWishlistMovies()`: Retrieve all wishlist movies
  - `getWishlistMovieIds()`: Get list of wishlist movie IDs
  - `isMovieInWishlist(movieId)`: Check if movie is in wishlist
  - `insertWishlistMovie()`: Add movie to wishlist
  - `deleteWishlistMovieById()`: Remove movie from wishlist
  - `getWishlistCount()`: Get total wishlist count

#### UI Components
- **Wishlist Button**: Heart icon on movie cards (filled for wishlisted, outlined for not wishlisted)
- **Wishlist Screen**: Dedicated screen showing all wishlisted movies
- **Wishlist Counter**: Shows number of movies in wishlist in the app bar

#### Architecture
- **Repository Pattern**: `MovieRepository` interface with `MovieRepositoryImpl` implementation
- **ViewModel**: `MovieViewModel` manages wishlist state and operations
- **Dependency Injection**: Hilt for managing dependencies
- **State Management**: StateFlow for reactive UI updates

## Technical Stack

- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Repository pattern
- **Database**: Room for local storage
- **Dependency Injection**: Hilt
- **Networking**: Retrofit with OkHttp
- **Image Loading**: Coil
- **State Management**: StateFlow and LiveData
- **Build System**: Gradle

## Project Structure

```
app/src/main/java/com/tofiq/myimdb/
├── data/
│   ├── local/
│   │   ├── entity/
│   │   │   ├── MovieEntity.kt
│   │   │   └── WishlistEntity.kt
│   │   ├── dao/
│   │   │   ├── MovieEntityDAO.kt
│   │   │   └── WishlistEntityDAO.kt
│   │   └── MovieDB.kt
│   ├── remote/
│   │   └── MovieApiService.kt
│   ├── repository/
│   │   ├── MovieRepository.kt
│   │   └── MovieRepositoryImpl.kt
│   └── model/
│       └── domain/
│           └── MovieResponse.kt
├── ui/
│   ├── screens/
│   │   ├── HomeScreen.kt
│   │   └── WishlistScreen.kt
│   ├── components/
│   │   ├── CommonAppBar.kt
│   │   ├── FilterDropdown.kt
│   │   └── GridMovieCard.kt
│   └── viewmodel/
│       └── MovieViewModel.kt
├── di/
│   └── NetworkModule.kt
└── util/
    └── Resource.kt
```

## Setup and Installation

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Build and run the application

## Dependencies

### Core Dependencies
- `androidx.compose.ui:ui`: Jetpack Compose UI
- `androidx.compose.material3:material3`: Material Design 3
- `androidx.lifecycle:lifecycle-viewmodel-compose`: ViewModel integration
- `androidx.navigation:navigation-compose`: Navigation

### Database
- `androidx.room:room-runtime`: Room database
- `androidx.room:room-ktx`: Room Kotlin extensions
- `kapt:androidx.room:room-compiler`: Room annotation processor

### Networking
- `com.squareup.retrofit2:retrofit`: HTTP client
- `com.squareup.retrofit2:converter-gson`: JSON converter
- `com.squareup.okhttp3:logging-interceptor`: HTTP logging

### Dependency Injection
- `com.google.dagger:hilt-android`: Hilt DI
- `androidx.hilt:hilt-navigation-compose`: Hilt navigation

### Image Loading
- `io.coil-kt:coil-compose`: Image loading

## Usage

### Adding Movies to Wishlist
1. Browse movies in the home screen
2. Tap the heart icon on any movie card
3. The heart will fill to indicate the movie is in your wishlist

### Viewing Wishlist
1. Tap the wishlist icon in the app bar (shows count)
2. View all your wishlisted movies
3. Remove movies by tapping the heart icon

### Searching and Filtering
1. Use the search bar to find specific movies
2. Use the filter button to filter by genre
3. Toggle between grid and list view using the view button

## Best Practices Implemented

- **SOLID Principles**: Clean architecture with separation of concerns
- **Repository Pattern**: Abstraction layer for data operations
- **State Management**: Reactive UI with StateFlow
- **Error Handling**: Graceful error handling throughout the app
- **Offline Support**: Local caching for better user experience
- **Performance**: Efficient database queries and UI updates
- **Accessibility**: Proper content descriptions and UI labels

## Future Enhancements

- Movie details screen with more information
- User ratings and reviews
- Movie recommendations
- Dark/Light theme toggle
- Multiple language support
- Push notifications for new releases
- Social features (sharing wishlists)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
