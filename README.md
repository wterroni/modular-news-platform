# modular-news-platform

# 📱 Modular News App

A production-ready Android application built as part of a Senior Android Developer technical assessment.

This app demonstrates modern Android development practices, focusing on clean architecture, offline-first behavior, and a polished user experience.

---

# 🚀 Features

## 🔐 Authentication

* Sign Up with name, email, and password
* Login with credential validation
* Secure password storage using **hash + salt (SHA-256)**
* Persistent session using **DataStore**
* Logout with confirmation dialog
* Splash screen to handle session state (no UI flicker)

---

## 📰 Stories

* Fetches top stories from Hacker News API
* Displays:

    * Title
    * Author
    * Score
    * Relative time (e.g. "2 hours ago")
    * Comment count
* Pull-to-refresh
* Infinite scroll pagination (manual implementation)
* Loading and empty states

---

## 📖 Story Detail

* In-app article viewing using **WebView**
* Share article via Android native share
* Metadata display (author, score, time)

---

## ⭐ Favorites

* Save and remove favorite stories
* Dedicated Favorites screen
* Persisted locally using **Room**
* Favorites are scoped per user
* Empty state for better UX

---

## 🎨 UI / UX

* Built with **Jetpack Compose**
* Material Design 3
* Smooth animations and transitions
* Dark mode support
* Responsive layout
* Proper loading, empty, and error states

---

## 📡 Offline-first Architecture

* Stories are cached locally using Room
* UI always reads from local database
* API is only used to refresh data
* Works without internet after initial load

---

# 🏗️ Architecture

The app follows **Clean Architecture + Multi-module structure**.

## Layers

* **Presentation**

    * Compose UI
    * ViewModels (StateFlow)

* **Domain**

    * UseCases
    * Business logic
    * Repository interfaces

* **Data**

    * Repository implementations
    * API services
    * Local storage (Room + DataStore)

---

## 📦 Modules

* `feature-auth`
* `feature-stories`
* `core-data`
* `core-database`
* `core-network`

---

## 🔁 Data Flow

UI → ViewModel → UseCase → Repository → DataSource (API / DB)

---

# 🛠️ Tech Stack

## Core

* Kotlin
* Coroutines + Flow
* Clean Architecture

## UI

* Jetpack Compose
* Material Design 3
* Navigation Compose

## DI

* Koin

## Local Storage

* Room (favorites + cached stories)
* DataStore (session + user data)

## Networking

* Ktor (or Retrofit, depending on your setup)

## Testing

* JUnit
* MockK
* kotlinx-coroutines-test

---

# 🔐 Security

Authentication is fully local, but implemented with proper security practices:

* Passwords are **never stored in plain text**
* Password hashing using:

    * SHA-256
    * Random salt per user
* Session state stored securely via DataStore

---

# ⚡ Pagination Strategy

Instead of using Paging 3, a custom pagination solution was implemented:

* Offset-based pagination (20 items per page)
* Triggered by scroll position (LazyListState)
* Prevents duplicate requests
* Works seamlessly with offline-first caching

---

# 🧪 Testing Strategy

* Focused on **business logic layers**
* Covered:

    * UseCases
    * Repositories
* Tools:

    * MockK for mocking
    * JUnit for assertions
    * Coroutines test for async flows

⚠️ No Robolectric or UI tests were used to keep tests fast and reliable.

---

# 📲 APK

## Download Debug APK

Download the latest debug APK:

[**modular-news-app-debug.apk**](releases/modular-news-app-debug.apk) *(25.8 MB)*

### Installation

1. Download the APK file above
2. Enable "Install from unknown sources" on your device
3. Install the APK
4. Launch the app and enjoy!

---

# 📦 Build & Run

## Requirements

* Android Studio (latest stable)
* JDK 17+

## Steps

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle
4. Run the app

### Build APK

```bash
# Generate debug APK
./gradlew assembleDebug

# APK location: app/build/outputs/apk/debug/app-debug.apk
```

---

# 📊 Trade-offs & Decisions

## Why not Paging 3?

* Reduced complexity
* Faster implementation
* Full control over pagination logic

## Why no backend?

* Requirement specified local authentication
* Focus on architecture and client-side logic

## Why no Robolectric?

* Faster tests
* Less complexity
* Pure unit testing approach

---

# 🚀 Future Improvements

* Pagination optimization (pre-fetching)
* Biometric authentication
* UI tests (Compose)
* CI/CD pipeline
* Remote sync for favorites
* Better error handling UI

---

# 👨‍💻 Author

Wellington Terroni

Senior Android Developer with experience in building scalable mobile architectures, platform engineering, and high-performance applications.

---

# 🏁 Final Notes

This project prioritizes:

* Clean and maintainable code
* Real-world architecture decisions
* Strong user experience
* Practical engineering trade-offs

---

