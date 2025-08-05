# MyIMDB - Android Movie Application

A modern Android application built with Jetpack Compose that provides a comprehensive movie browsing experience with advanced features like wishlist management, search, filtering, and offline support.

## 🎬 Features

### Core Features
- **Movie Browsing**: Browse through an extensive collection of movies with detailed information
- **Movie Details**: View comprehensive movie information including plot, cast, director, runtime, and genres
- **Wishlist Management**: Add/remove movies to/from your personal wishlist with real-time updates
- **Search Functionality**: Search movies by title, plot, director, actors, or genres
- **Genre Filtering**: Filter movies by specific genres with easy-to-use dropdown interface
- **Grid/List View Toggle**: Switch between grid and list view layouts for different browsing preferences
- **Offline Support**: Cached data for offline viewing with automatic refresh capabilities
- **Infinite Scrolling**: Smooth pagination with automatic loading of more movies
- **Pull-to-Refresh**: Refresh movie data with pull-to-refresh functionality

### UI/UX Features
- **Material Design 3**: Modern Material Design 3 theming with dynamic color support
- **Responsive Design**: Optimized for various screen sizes and orientations
- **Smooth Animations**: Elegant animations and transitions throughout the app
- **Loading States**: Comprehensive loading indicators and placeholder states
- **Error Handling**: User-friendly error messages with retry options
- **Image Loading**: Efficient image loading with Coil library and placeholder support
- **Edge-to-Edge Design**: Modern edge-to-edge design with system bar integration

## 🏗️ Architecture

### Architecture Pattern
The application follows **MVVM (Model-View-ViewModel)** architecture pattern with **Clean Architecture** principles:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   UI Layer      │    │  Domain Layer   │    │   Data Layer    │
│   (Compose)     │◄──►│  (ViewModel)    │◄──►│  (Repository)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │                        │
                              ▼                        ▼
                       ┌─────────────────┐    ┌─────────────────┐
                       │   Use Cases     │    │   Local/Remote  │
                       │   (Business)    │    │   Data Sources  │
                       └─────────────────┘    └─────────────────┘
```

### Technology Stack
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Clean Architecture
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp
- **Image Loading**: Coil
- **Local Database**: Room
- **Navigation**: Navigation Compose
- **State Management**: Kotlin Flow
- **Build System**: Gradle with Version Catalogs

## 📁 Project Structure

```
app/src/main/java/com/tofiq/myimdb/
├── data/                           # Data Layer
│   ├── local/                      # Local Data Sources
│   │   ├── dao/                    # Data Access Objects
│   │   │   └── MovieEntityDAO.kt
│   │   ├── entity/                 # Database Entities
│   │   │   └── MovieEntity.kt
│   │   └── MovieDB.kt              # Room Database
│   ├── model/                      # Data Models
│   │   └── domain/                 # Domain Models
│   │       └── MovieResponse.kt
│   ├── remote/                     # Remote Data Sources
│   │   └── MovieApiService.kt      # API Service Interface
│   └── repository/                 # Repository Layer
│       ├── MovieRepository.kt      # Repository Interface
│       └── MovieRepositoryImpl.kt  # Repository Implementation
├── di/                             # Dependency Injection
│   └── NetworkModule.kt            # Network Module Configuration
├── ui/                             # UI Layer
│   ├── components/                 # Reusable UI Components
│   │   ├── CommonAppBar.kt         # Common App Bar Component
│   │   ├── FilterDropdown.kt       # Genre Filter Dropdown
│   │   ├── GridMovieCard.kt        # Grid View Movie Card
│   │   └── WishlistAnimation.kt    # Wishlist Animation
│   ├── screens/                    # Screen Components
│   │   ├── HomeScreen.kt           # Main Movie List Screen
│   │   ├── MovieDetailsScreen.kt   # Movie Details Screen
│   │   ├── SplashScreen.kt         # Splash Screen
│   │   └── WishlistScreen.kt       # Wishlist Screen
│   ├── theme/                      # UI Theme Configuration
│   │   ├── Color.kt                # Color Definitions
│   │   ├── Theme.kt                # Theme Configuration
│   │   └── Type.kt                 # Typography Definitions
│   └── viewmodel/                  # ViewModels
│       └── MovieViewModel.kt       # Main Movie ViewModel
├── util/                           # Utility Classes
│   └── Resource.kt                 # Resource Wrapper for API Responses
├── MainActivity.kt                 # Main Activity
└── MyIMDBApplication.kt            # Application Class
```

## 🔄 Data Flow

### 1. App Initialization
```
SplashScreen → MovieViewModel → MovieRepository → API/Local DB → UI Update
```

### 2. Movie Loading
```
User Action → ViewModel → Repository → API Service → Local Cache → UI Update
```

### 3. Search & Filtering
```
User Input → ViewModel (Filter Logic) → UI Update with Filtered Results
```

### 4. Wishlist Operations
```
User Action → ViewModel → Local State Update → UI Update
```

## 🎯 Key Components

### MovieViewModel
The central ViewModel that manages all movie-related state and business logic:

**State Management:**
- `movieState`: API response state (Loading/Success/Error)
- `displayedMovies`: Currently displayed movies with pagination
- `selectedGenre`: Currently selected genre filter
- `searchQuery`: Current search query
- `wishlistMovies`: Set of wishlisted movie IDs
- `isGridView`: Grid/List view toggle state

**Key Functions:**
- `loadMovies()`: Initial movie loading
- `refreshMovies()`: Refresh movie data
- `loadNextPage()`: Load more movies for pagination
- `setSelectedGenre()`: Apply genre filter
- `setSearchQuery()`: Apply search filter
- `toggleWishlist()`: Add/remove from wishlist
- `toggleGridView()`: Switch between grid and list views

### MovieRepository
Implements the repository pattern for data management:

**Features:**
- **Caching Strategy**: First checks local database, then fetches from API
- **Offline Support**: Returns cached data when offline
- **Data Persistence**: Stores API responses in Room database
- **Error Handling**: Comprehensive error handling for network issues

### UI Components

#### CommonAppBar
A reusable app bar component with dynamic features:
- Search functionality with real-time input
- Filter button for genre selection
- Wishlist button with badge counter
- Grid/List view toggle
- Refresh button
- Back navigation

#### HomeScreen
The main screen displaying movie listings:
- **Dual Layout**: Grid and List view support
- **Infinite Scrolling**: Automatic pagination
- **Filter Indicators**: Shows active filters with clear options
- **Loading States**: Comprehensive loading and error states
- **Empty States**: User-friendly empty state handling

#### MovieDetailsScreen
Detailed movie information display:
- **Hero Section**: Large poster with gradient overlay
- **Animated Content**: Staggered animations for content sections
- **Wishlist Integration**: Direct wishlist toggle
- **Comprehensive Info**: Plot, cast, director, runtime, genres

#### WishlistScreen
Personal wishlist management:
- **Wishlist Display**: Shows all wishlisted movies
- **Remove Functionality**: Easy removal from wishlist
- **Empty State**: Encouraging empty state design
- **Navigation**: Seamless navigation to movie details

## 🛠️ Dependencies

### Core Dependencies
```kotlin
// Jetpack Compose
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.ui:ui-tooling-preview")

// Navigation
implementation("androidx.navigation:navigation-compose:2.9.3")

// Dependency Injection
implementation("com.google.dagger:hilt-android:2.57")
kapt("com.google.dagger:hilt-android-compiler:2.57")

// Networking
implementation("com.squareup.retrofit2:retrofit:3.0.0")
implementation("com.squareup.retrofit2:converter-gson:3.0.0")
implementation("com.squareup.okhttp3:okhttp:5.1.0")
implementation("com.squareup.okhttp3:logging-interceptor:5.1.0")

// Image Loading
implementation("io.coil-kt:coil-compose:2.7.0")

// Local Database
implementation("androidx.room:room-runtime:2.7.2")
kapt("androidx.room:room-compiler:2.7.2")
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 34 or higher
- Kotlin 2.2.0 or higher

### Installation
1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Build and run the application

### Build Configuration
```kotlin
android {
    compileSdk = 34
    defaultConfig {
        applicationId = "com.tofiq.myimdb"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
}
```

## 🎨 UI/UX Design

### Design System
- **Material Design 3**: Modern design language with dynamic colors
- **Typography**: Consistent typography scale with proper hierarchy
- **Spacing**: 8dp grid system for consistent spacing
- **Colors**: Semantic color system with light/dark theme support
- **Icons**: Material Design icons for consistency

### Responsive Design
- **Adaptive Layouts**: Optimized for phones and tablets
- **Orientation Support**: Portrait and landscape orientations
- **Edge-to-Edge**: Modern edge-to-edge design
- **System Integration**: Proper system bar handling

## 🔧 Configuration

### Network Configuration
The app uses a mock API service for demonstration. To integrate with a real API:

1. Update `MovieApiService.kt` with your API endpoints
2. Configure base URL in `NetworkModule.kt`
3. Update data models to match your API response structure

### Database Configuration
Room database is configured for local caching:
- **Database Name**: `movie_database`
- **Version**: 1
- **Entities**: `MovieEntity`
- **Migrations**: Automatic schema migration support

## 🧪 Testing

### Test Structure
```
app/src/test/java/com/tofiq/myimdb/
├── data/
│   ├── local/
│   │   └── dao/
│   └── repository/
└── ExampleUnitTest.kt
```

### Testing Strategy
- **Unit Tests**: ViewModel, Repository, and Use Cases
- **Integration Tests**: Database operations and API calls
- **UI Tests**: Compose UI component testing
- **End-to-End Tests**: Complete user workflows

## 📱 Platform Support

### Android Versions
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)
- **Recommended**: API 26+ for optimal experience

### Device Support
- **Phones**: All screen sizes from 4.5" to 7"
- **Tablets**: 7" to 12" tablets with adaptive layouts
- **Foldables**: Support for foldable device configurations

## 🔄 State Management

### State Flow Architecture
The app uses Kotlin Flow for reactive state management:

```kotlin
// State Flow Example
private val _movieState = MutableStateFlow<Resource<MovieResponse>>(Resource.Loading())
val movieState: StateFlow<Resource<MovieResponse>> = _movieState.asStateFlow()
```

### State Updates
- **Unidirectional Data Flow**: State flows from ViewModel to UI
- **Immutable State**: State objects are immutable
- **Predictable Updates**: All state changes go through ViewModel

## 🎯 Performance Optimizations

### Image Loading
- **Coil Library**: Efficient image loading and caching
- **Placeholder Support**: Loading and error placeholders
- **Crossfade Animations**: Smooth image transitions
- **Memory Management**: Automatic memory cleanup

### List Performance
- **Lazy Loading**: Efficient list rendering with Compose LazyColumn/LazyVerticalGrid
- **Key Optimization**: Proper key usage for list items
- **Pagination**: Infinite scrolling with automatic loading
- **RecyclerView Optimization**: Efficient item recycling

### Memory Management
- **ViewModel Scoping**: Proper ViewModel lifecycle management
- **Coroutine Management**: Structured concurrency with viewModelScope
- **Resource Cleanup**: Automatic resource cleanup in onCleared()

## 🔒 Security Considerations

### Network Security
- **HTTPS Only**: All network requests use HTTPS
- **Certificate Pinning**: Optional certificate pinning support
- **Input Validation**: Proper input sanitization
- **Error Handling**: Secure error message handling

### Data Security
- **Local Storage**: Secure local database storage
- **Sensitive Data**: No sensitive data stored locally
- **Permissions**: Minimal permission requirements


### Development Guidelines
1. Follow Kotlin coding conventions
2. Use SOLID principles
3. Write comprehensive tests
4. Document code changes
5. Follow Material Design guidelines

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👨‍💻 Author

**Tofiq** - Android Developer

## 🙏 Acknowledgments

- Material Design team for design guidelines
- Jetpack Compose team for the amazing UI framework
- Android community for best practices and libraries
- Open source contributors for the libraries used in this project

---

**MyIMDB** - Your personal movie companion built with modern Android development practices.
