# 🧠 Manomitra

### Your AI Mind Companion — Voice, Chat, Mood & Journaling Android App

Manomitra is an Android application designed as a personal AI companion that brings **AI chat, voice interaction, mood tracking, journaling, daily insights, authentication, and cloud data storage** together in one mobile experience.

The app is built with **Kotlin and Jetpack Compose**, uses **Firebase Authentication and Firestore** for user data, and integrates **Google Gemini and Groq** for AI-powered conversations and voice features.

---
## 📱 Download Manomitra

### Current Version — v1.0.0

[![Download APK](https://img.shields.io/badge/Download-Manomitra%20v1.0.0-green?style=for-the-badge&logo=android)](https://github.com/akshayh0/manomitra-android/releases/tag/v1.0.0)

Download and install the latest available Android version from GitHub Releases.

**Version:** `v1.0.0`  
**Platform:** Android  
**APK:** `app-debug.apk`

---

### 🚀 Upcoming — v2.0.0

**Manomitra v2.0.0 — Coming Soon 🚀**

The next major version of Manomitra is currently under development.

Planned improvements include:

- 🤖 Enhanced AI conversations
- 🎙️ Improved voice interaction
- 🧠 Better AI-powered insights
- 😊 Enhanced mood tracking
- 📔 Improved journaling experience
- 🌐 Additional language support
- 🎨 UI/UX improvements
- ⚡ Performance improvements
- 🔒 Security and stability improvements

> **v2.0.0 is currently under development and will be released in a future update.**


## ✨ Features

### 🤖 AI Chat

- AI-powered conversational assistant
- Chat interface with message history
- Google Gemini integration
- Groq integration
- Persistent chat history using Firestore
- Clean and responsive Compose UI

### 🎙️ Voice Companion

- Voice-based interaction
- Speech-to-text support
- Android speech recognition
- Groq-based speech-to-text option
- Text-to-speech responses
- Audio recording support
- Voice wave animation
- Voice settings

### 😊 Mood Tracker

- Record daily moods
- Mood tracking interface
- Mood history
- Daily insights
- AI-assisted insight experience

### 📔 Personal Journal

- Create journal entries
- View journal entries
- Store journal data in Firestore
- Manage personal journal content

### 🔐 Authentication

- User registration
- User login
- Firebase Authentication
- Persistent authentication state
- User profile data stored with Firestore

### 👤 Profile

- View user profile
- Edit profile information
- Profile management

### ⚙️ Settings

- Application settings
- Voice settings
- User preferences

### 🎨 Modern UI

- Jetpack Compose
- Material 3
- Custom theme system
- Reusable UI components
- Bottom navigation
- Gradient buttons
- Chat bubbles
- Voice animations
- Responsive layouts
- Dark/light theme support through the app theme system

---

# 🏗️ Architecture

Manomitra follows a modular Android architecture with separation between UI, state management, repositories, services, and shared components.

```text
                         ┌─────────────────────┐
                         │       User          │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │   Jetpack Compose   │
                         │         UI          │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │     ViewModel       │
                         │   State Management  │
                         └──────────┬──────────┘
                                    │
                         ┌──────────┴──────────┐
                         │                     │
                         ▼                     ▼
                 ┌──────────────┐      ┌──────────────┐
                 │  Repository  │      │ Voice Layer  │
                 └──────┬───────┘      └──────┬───────┘
                        │                     │
              ┌─────────┴─────────┐     ┌─────┴──────────┐
              │                   │     │                │
              ▼                   ▼     ▼                ▼
        ┌───────────┐      ┌───────────┐ Android STT    Groq STT
        │ Firebase  │      │ Firestore │
        │   Auth    │      │   Data    │
        └───────────┘      └───────────┘

                         ┌─────────────────────┐
                         │      AI Layer       │
                         ├─────────────────────┤
                         │ Google Gemini       │
                         │ Groq                │
                         └─────────────────────┘
```

---

# 📱 Application Flow

```text
                         Manomitra
                            │
              ┌─────────────┼─────────────┐
              │             │             │
              ▼             ▼             ▼
           Splash         Auth          Main App
                            │             │
                      ┌─────┴─────┐       ├── Home
                      │           │       ├── Chat
                      ▼           ▼       ├── Mood
                   Login      Register     ├── Journal
                                          ├── Profile
                                          └── Settings
```

---

# 🧩 Main Modules

```text
app/src/main/java/com/manomitra/app/
│
├── auth/
│   ├── AuthRepository.kt
│   ├── AuthState.kt
│   ├── AuthViewModel.kt
│   ├── FirebaseAuthRepository.kt
│   ├── FirestoreRepository.kt
│   ├── FirestoreRepositoryImpl.kt
│   └── User.kt
│
├── core/
│   ├── components/
│   ├── navigation/
│   ├── settings/
│   ├── theme/
│   └── voice/
│
└── feature/
    ├── auth/
    │   ├── login/
    │   └── register/
    │
    ├── chat/
    │
    ├── home/
    │
    ├── journal/
    │
    ├── mood/
    │
    ├── onboarding/
    │
    ├── profile/
    │
    ├── settings/
    │
    ├── splash/
    │
    └── voice/
```

---

# 🗂️ Project Structure

```text
Manomitra/
│
├── app/
│   ├── src/
│   │   ├── androidTest/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/manomitra/app/
│   │       │
│   │       └── res/
│   │
│   ├── build.gradle.kts
│   ├── google-services.json
│   └── proguard-rules.pro
│
├── gradle/
│
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
└── .gitignore
```

---

# 🤖 AI Architecture

Manomitra supports AI-powered conversations using Google Gemini and Groq.

```text
                         User
                           │
                           ▼
                    Chat / Voice UI
                           │
                           ▼
                      ViewModel
                           │
                           ▼
                     AI Repository
                           │
                 ┌─────────┴─────────┐
                 │                   │
                 ▼                   ▼
           Gemini API            Groq API
                 │                   │
                 └─────────┬─────────┘
                           ▼
                     AI Response
                           │
                           ▼
                         User
```

---

# 🎙️ Voice Architecture

The application contains a dedicated voice layer.

```text
Voice Input
    │
    ▼
VoiceInputManager
    │
    ├───────────────┐
    │               │
    ▼               ▼
Android STT      Groq STT
    │               │
    └───────┬───────┘
            ▼
        Text Input
            │
            ▼
          AI Model
            │
            ▼
       AI Response
            │
            ▼
      Text-to-Speech
            │
            ▼
          User
```

Voice-related components include:

- `VoiceInputManager`
- `SpeechToTextRepository`
- `AndroidSpeechToTextRepository`
- `GroqSpeechToTextRepository`
- `AudioRecorder`
- `TextToSpeechManager`
- `VoiceSettings`

---

# 🔐 Authentication Architecture

Manomitra uses Firebase Authentication.

```text
User
 │
 ├── Register
 │
 └── Login
       │
       ▼
Firebase Authentication
       │
       ▼
Authenticated User
       │
       ▼
Firestore
       │
       ▼
User Profile / Application Data
```

Authentication components include:

- `AuthRepository`
- `AuthViewModel`
- `FirebaseAuthRepository`
- `FirestoreRepository`
- `FirestoreRepositoryImpl`
- `User`

---

# ☁️ Firebase Integration

Firebase is used for backend services.

### Firebase Authentication

Used for:

- User registration
- User login
- Authentication state
- User identity

### Cloud Firestore

Used for storing application data such as:

- User profiles
- Chat history
- Journal entries
- Mood entries
- Daily insights

---

# 💬 Chat History

Chat conversations can be persisted using Firestore.

```text
User
 │
 ▼
Chat Screen
 │
 ▼
ChatViewModel
 │
 ▼
Chat History Repository
 │
 ▼
Firestore
 │
 ▼
Stored Conversation
```

Relevant components include:

- `ChatMessage`
- `ChatViewModel`
- `ChatHistoryRepository`
- `FirestoreChatHistoryRepository`

---

# 😊 Mood Tracking

Mood data is handled through a dedicated feature module.

```text
User
 │
 ▼
Mood Tracker
 │
 ▼
MoodViewModel
 │
 ▼
Mood Repository
 │
 ▼
Firestore
 │
 ▼
Mood History
```

Mood-related components include:

- `MoodEntry`
- `MoodRepository`
- `MoodViewModel`
- `FirestoreMoodRepository`
- `MoodTrackerScreen`
- `DailyInsight`
- `DailyInsightRepository`

---

# 📔 Journal

The journal module allows users to create and manage personal journal entries.

```text
User
 │
 ▼
Journal Screen
 │
 ▼
JournalViewModel
 │
 ▼
Journal Repository
 │
 ▼
Firestore
```

Journal-related components include:

- `JournalEntry`
- `JournalRepository`
- `JournalViewModel`
- `FirestoreJournalRepository`
- `JournalScreen`

---

# 🧭 Navigation

The application uses Jetpack Navigation Compose.

Main navigation areas include:

```text
Splash
  │
  ▼
Onboarding
  │
  ▼
Login / Register
  │
  ▼
Home
  │
  ├── Chat
  ├── Mood
  ├── Journal
  ├── Profile
  └── Settings
```

---

# 🎨 UI Components

Manomitra contains reusable Compose components.

```text
core/components/
│
├── ChatBubble.kt
├── IllustrationCard.kt
├── ManomitraBottomNavigation.kt
├── PageIndicatorDots.kt
├── PrimaryGradientButton.kt
├── SecondaryTonalButton.kt
└── VoiceWaveAnimation.kt
```

These components help keep the UI consistent and reusable.

---

# 🎨 Design System

The project includes a centralized Compose theme system.

```text
core/theme/
│
├── Color.kt
├── Elevation.kt
├── Shape.kt
├── Spacing.kt
├── Theme.kt
└── Type.kt
```

This allows colors, typography, spacing, shapes, elevation, and other design values to be managed centrally.

---

# 🛠️ Technology Stack

## Android

| Technology | Purpose |
|---|---|
| Kotlin | Primary programming language |
| Jetpack Compose | Modern Android UI |
| Material 3 | UI components and design system |
| Navigation Compose | Screen navigation |
| ViewModel | UI state management |
| Kotlin Coroutines | Asynchronous operations |
| Android SDK | Android platform |

## Backend / Cloud

| Technology | Purpose |
|---|---|
| Firebase Authentication | User authentication |
| Cloud Firestore | Cloud data storage |
| Google Services | Firebase integration |

## AI

| Technology | Purpose |
|---|---|
| Google Gemini | AI conversations |
| Groq | AI and speech-related services |
| Speech-to-Text | Voice input |
| Text-to-Speech | Voice output |

## Development

| Technology | Purpose |
|---|---|
| Gradle Kotlin DSL | Build configuration |
| Android Studio | Development environment |
| Git | Version control |
| GitHub | Source code hosting |

---

# 📋 Requirements

Before building the project, install:

- Android Studio
- JDK 17
- Android SDK
- Git
- A Firebase project
- Google Gemini API key
- Groq API key

The project is configured with:

```text
compileSdk = 36
targetSdk  = 36
minSdk     = 29
Java       = 17
Kotlin     = JVM 17
```

---

# 🚀 Getting Started

## 1. Clone the Repository

```bash
git clone https://github.com/akshayh0/manomitra-android.git
```

Move into the project:

```bash
cd manomitra-android
```

---

# 2. Open in Android Studio

1. Open Android Studio.
2. Select **Open**.
3. Choose the cloned `manomitra-android` folder.
4. Allow Gradle to sync.
5. Wait for dependencies to finish downloading.

---

# 3. Configure Firebase

Create or select a Firebase project.

Enable:

- Firebase Authentication
- Cloud Firestore

Add your Android application using the package name:

```text
com.manomitra.app
```

Download:

```text
google-services.json
```

Place it inside:

```text
app/google-services.json
```

> Never expose private credentials or API keys in a public repository.

---

# 4. Configure AI API Keys

The application expects API keys to be supplied through `local.properties`.

Create or update:

```text
local.properties
```

Add:

```properties
GEMINI_API_KEY=your_gemini_api_key
GROQ_API_KEY=your_groq_api_key
```

The project reads these values during the Gradle build and exposes them through `BuildConfig`.

> ⚠️ Do not commit `local.properties` to GitHub.

---

# 5. Build the Application

On Windows:

```bash
gradlew.bat assembleDebug
```

On Linux/macOS:

```bash
./gradlew assembleDebug
```

---

# 6. Run the Application

You can run the app using:

- Android Studio emulator
- Physical Android device

From Android Studio:

```text
Run → Run 'app'
```

---

# 🧪 Testing

The project contains Android instrumentation testing support.

Testing technologies include:

- JUnit
- AndroidX Test
- Espresso
- Compose UI testing
- Mockito
- Kotlin Coroutines Test

Run unit tests:

```bash
./gradlew test
```

Run Android instrumentation tests:

```bash
./gradlew connectedAndroidTest
```

On Windows:

```bash
gradlew.bat test
```

---

# 🔒 Security

Manomitra requires external API credentials for AI functionality.

### Recommended security practices

- Keep API keys outside source control.
- Use `local.properties` for development secrets.
- Do not commit secret keys.
- Configure Firebase security rules.
- Restrict Firestore access based on authenticated users.
- Review production API-key restrictions.
- Never hard-code production secrets.

---

# 📱 Permissions

The application requests microphone access for voice functionality.

Required permissions include:

```text
android.permission.RECORD_AUDIO
android.permission.INTERNET
```

Microphone access is required for speech and voice features.

---

# 📈 Future Enhancements

Potential improvements include:

- [ ] More regional language support
- [ ] Advanced AI personalization
- [ ] AI-powered mood insights
- [ ] Mood analytics dashboard
- [ ] Improved voice conversations
- [ ] Offline journal support
- [ ] Push notifications
- [ ] Daily wellness reminders
- [ ] Advanced journaling analytics
- [ ] AI-powered journal summaries
- [ ] Export journal entries
- [ ] Improved accessibility
- [ ] Automated CI/CD
- [ ] Expanded test coverage
- [ ] Production monitoring
- [ ] Play Store release

---

# 🗺️ Roadmap

```text
Phase 1
├── Authentication
├── Onboarding
└── Basic UI

Phase 2
├── AI Chat
├── Mood Tracking
└── Journaling

Phase 3
├── Voice Companion
├── Speech-to-Text
└── Text-to-Speech

Phase 4
├── AI Insights
├── Personalization
└── Advanced Analytics

Phase 5
├── Testing
├── Optimization
└── Play Store Release
```

---

# 📸 Screenshots

Add screenshots of the application to a `screenshots` folder.

Recommended screenshots:

```text
screenshots/
│
├── onboarding.png
├── login.png
├── home.png
├── chat.png
├── mood.png
├── journal.png
├── voice.png
├── profile.png
└── settings.png
```

Then add them to this README:

```markdown
![Onboarding](screenshots/onboarding.png)

![Home](screenshots/home.png)

![AI Chat](screenshots/chat.png)

![Mood Tracker](screenshots/mood.png)

![Journal](screenshots/journal.png)

![Voice Companion](screenshots/voice.png)
```

---

# 🧑‍💻 Developer

## Akshay H

Artificial Intelligence & Machine Learning Student

### Interests

- Artificial Intelligence
- Machine Learning
- Generative AI
- Android Development
- Kotlin
- Jetpack Compose
- Firebase
- Google Gemini
- Groq
- Full-Stack Development

---

# 📚 What This Project Demonstrates

Manomitra demonstrates practical experience with:

- Android application development
- Kotlin
- Jetpack Compose
- Material 3
- MVVM-style architecture
- ViewModel state management
- Repository pattern
- Firebase Authentication
- Cloud Firestore
- Generative AI
- Google Gemini
- Groq
- Speech-to-Text
- Text-to-Speech
- Voice interaction
- Cloud data persistence
- Modular project structure
- API integration
- UI/UX design
- Automated testing

---

# ⭐ Contributing

Contributions and suggestions are welcome.

To contribute:

```bash
git fork
git clone <your-fork-url>
git checkout -b feature/your-feature
```

Make your changes, commit them, and open a pull request.

---

# 📄 License

This project is developed for educational and development purposes.

---

# 🚀 Manomitra

### Your AI Mind Companion

**Chat • Voice • Mood • Journal • Insights • AI**

Built with ❤️ using **Kotlin, Jetpack Compose, Firebase, Gemini & Groq**.
