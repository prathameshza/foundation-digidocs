# OrbitScanner Architecture Guide

Welcome to the **OrbitScanner** codebase! This document explains how the project is structured, where different types of code belong, and how the modules interact with each other.

---

## 1. Overall Architecture Overview

OrbitScanner is built using a **Modularized Architecture** combined with **Clean Architecture** principles. 

Instead of putting all our code into a single massive folder, we split the app into smaller, self-contained units called **modules**. This keeps the code clean, makes testing easier, and allows multiple developers to work on different features without conflicts.

### The Three Module Layers
Our project is divided into three main layers:

```
                  ┌─────────────────────────┐
                  │       :app Module       │ (The Brain & Router)
                  └────────────┬────────────┘
                               │
         ┌─────────────────────┼─────────────────────┐
         ▼                     ▼                     ▼
┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐
│  :feature-home  │   │ :feature-editor │   │:feature-scanner │ (Independent Features)
└────────┬────────┘   └────────┬────────┘   └────────┬────────┘
         │                     │                     │
         └─────────────────────┼─────────────────────┘
                               │
                               ▼
                  ┌─────────────────────────┐
                  │      :core Module       │ (The Shared Foundation)
                  └─────────────────────────┘
```

### Project Root Folder Structure

When you clone the project, you will see these folders at the root level. Here is how they correspond to our three layers:

```
OrbitScanner/
├── gradle/                                 # Gradle wrapper and build configuration files
├── build.gradle.kts                        # Root-level build configuration script
├── settings.gradle.kts                     # Declares all modules in the project
│
├── app/                                    # LAYER 1: The App Module (Brain & Router)
│   ├── build.gradle.kts                    # App-specific build dependencies
│   └── src/main/java/com/pluton/orbitscanner/  # Aggregates features and handles navigation
│
├── core/                                   # LAYER 2: The Core Module (Shared Foundation)
│   ├── build.gradle.kts                    # Core-specific build dependencies
│   └── src/main/java/com/pluton/orbitscanner/core/ # Shared utilities, database, network, & models
│
├── feature-home/                           # LAYER 3: Feature Modules (Independent Tools)
├── feature-scanner/                        # Each feature folder is structured identically,
├── feature-editor/                         # containing its own Presentation, Domain, and
├── feature-aiocr/                          # Data layers. They do not know about each other's
├── feature-pdftools/                       # internal code.
└── feature-subscription/
```

---

## 2. The Core Module (`:core`)

The **Core Module** is the shared engine of OrbitScanner. It contains code that does not belong to any single feature but is required by many.

### Core Folder Structure & Responsibilities

```
core/
└── src/main/java/com/pluton/orbitscanner/core/
    ├── common/          # Coroutine dispatchers, API Result wrappers, and utility loggers.
    ├── ui/              # Reusable UI elements (custom buttons, shared layouts, design systems).
    ├── database/        # Local Relational Database configuration (Room setup, DAO interfaces).
    ├── network/         # API clients (Retrofit configuration, interceptors, network DTOs).
    ├── preferences/     # Lightweight local key-value storage (Jetpack DataStore).
    ├── scanner/         # Camera helper scripts and raw capture wrapper tools (No UI).
    ├── pdf/             # Non-UI PDF handling utilities (compressing, merging, converting).
    ├── storage/         # Local file system utilities and Scoped Storage helpers.
    ├── model/           # App-wide global models used by multiple features (e.g., UserProfile).
    └── di/              # Core Hilt modules (provides Database, Network, and Storage instances).
```

---

## 3. The Feature Modules (`:feature-*`)

Features represent the distinct functional parts of our app. We keep them separate to maintain clean boundaries. Examples include:
*   `:feature-home` (Landing screen, search, and directory navigation)
*   `:feature-scanner` (Camera UI, multi-page crop, document edge-detection)
*   `:feature-editor` (Annotating files, adding signatures)
*   `:feature-aiocr` (Extracting text and tables using AI)
*   `:feature-pdftools` (Compressing, protecting, or converting PDFs)
*   `:feature-subscription` (Managing premium paywalls and plans)

### How an Internal Feature is Structured
To make it easy for a developer to navigate any feature, every `:feature-*` module follows the exact same internal directory structure. This is a simplified split of **Presentation, Domain, and Data**:

```
feature-home/
└── src/main/java/com/pluton/orbitscanner/feature/home/
    ├── data/                      # 1. THE DATA LAYER (The "How")
    │   ├── mapper/                # Converts database/network objects into clean models for the UI.
    │   └── repository/            # Implements the Repository interfaces defined in the domain layer.
    │
    ├── domain/                    # 2. THE DOMAIN LAYER (The "What")
    │   ├── model/                 # Data structures unique to this feature (e.g., HomeItem).
    │   └── repository/            # Interfaces that define what actions this feature can perform.
    │
    ├── presentation/              # 3. THE PRESENTATION LAYER (The "Show")
    │   ├── component/             # Reusable UI widgets built specifically for this feature.
    │   ├── screen/                # Full-screen Composable layouts (e.g., HomeScreen, SearchScreen).
    │   ├── state/                 # Classes describing current UI state (Loading, Success, Error).
    │   └── viewmodel/             # Manages UI logic and holds/updates state using Coroutines.
    │
    └── di/                        # 4. DEPENDENCY INJECTION
        └── HomeModule.kt          # Binds repository implementations to their interfaces using Hilt.
```

#### Layer Cheat-Sheet for Freshers:
*   **Domain:** The core business rules. It contains only clean Kotlin code and does not care where the data comes from (database, network, or mock).
*   **Data:** The implementation detail. This is where we query the database/network and map those raw objects into clean domain models.
*   **Presentation:** The visible UI. The **ViewModel** communicates with the **Domain Repositories**, receives data, prepares a **UiState**, and the **Screens** draw themselves based on that state.

---

## 4. The App Module (`:app`)

The **App Module** is the glue of the application. It depends on all of our features and compiles them into a single runnable application.

### App Folder Structure & Responsibilities

```
app/
└── src/main/java/com/pluton/orbitscanner/
    ├── OrbitScannerApplication.kt     # Hilt's Application class (The root entry point of the app).
    ├── MainActivity.kt            # The single host Activity for our Jetpack Compose UI.
    └── navigation/
        └── AppNavHost.kt          # Coordinates navigation across different, isolated features.
```

*Why keep navigation in App?* Because features do not know about other features, they cannot navigate to each other directly. The `:app` module handles this routing by connecting the screens.

---

## 5. Dependency Rules (Enforce Logic)

To prevent the code from becoming a tangled mess over time, we strictly enforce dependency rules. These rules ensure that changes to one feature cannot accidentally break another.

```
                     ┌───────────────┐
                     │     :app      │
                     └───────┬───────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │   :feature-*    │
                    └────────┬────────┘
                             │
                             ▼
                     ┌───────────────┐
                     │     :core     │
                     └───────────────┘
```

### The Rules of Engagement:

| Dependency Type | Allowed? | Why? |
| :--- | :---: | :--- |
| **`feature` $\to$ `core`** | **✅ YES** | Features need access to shared databases, networks, and design system components. |
| **`app` $\to$ `feature`** | **✅ YES** | The app module must assemble the features and route between them. |
| **`feature` $\to$ `feature`** | **❌ NO** | Features must remain completely decoupled. If Feature A needs to talk to Feature B, it must be coordinated through the `:app` module or shared via `:core`. |
| **`core` $\to$ `feature`** | **❌ NO** | Core is the foundation. It must never depend on any feature layer; otherwise, it creates circular dependency issues. |


## 6. Build & Dependency Configuration

To keep the development lifecycle scalable and the build files clean, dependency versions and release details are centralized [5].

### Dependency Consolidation (Bundles)
Using Gradle Version Catalog bundles (such as `libs.bundles.compose.ui`, `libs.bundles.test.core`, and `libs.bundles.android.test.core`) simplifies dependency declarations across module-level `build.gradle.kts` files:
*   **Reduced Duplication:** Prevents listing individual packages repeatedly across different module modules.
*   **Enforced Consistency:** Ensures that Jetpack Compose components and the test suite libraries remain synchronized on identical compiler and runtime versions.

### Dynamic App Versioning Plugin (`AppReleaseVersionPlugin`)
The project utilizes a custom script plugin housed in the `buildSrc` directory to manage releases on the Google Play Console:
*   **Modern API Integration:** By targeting `ApplicationAndroidComponentsExtension` and updating configurations within `onVariants`, the plugin relies on modern, configuration-cache-friendly Gradle APIs.
*   **DSL Decoupling:** This isolates the logic required to calculate `versionCode` and `versionName` (e.g., handling environment flags or patch counts) entirely from the main `:app` module's DSL.

## 7. Gradle Toolchain Resolution

The project uses dynamic toolchain resolution configured in `gradle.properties` without hardcoding paths. The priority is:
1. **Primary**: Environment variable `JAVA_HOME`.
2. **Fallback**: Detected local JDKs.
3. **Last Resort**: Auto-download via Foojay.

### Verification Example
```console
PS C:\Users\USER\Desktop\Atomic\OrbitScanner> ./gradlew -q javaToolchains

 + Options
     | Auto-detection:     Disabled
     | Auto-download:      Enabled

 + Eclipse Temurin JDK 21 (21.0.9+10-LTS)
     | Location:           C:\Users\USER\.jdks\temurin-21.0.9
     | Language Version:   21
     | Vendor:             Eclipse Temurin
     | Architecture:       amd64
     | Is JDK:             true
     | Detected by:        environment variable 'JAVA_HOME'

PS C:\Users\USER\Desktop\Atomic\OrbitScanner> ./gradlew --version

------------------------------------------------------------
Gradle 9.4.1
------------------------------------------------------------

Build time:    2026-03-19 08:46:28 UTC
Revision:      2d6327017519d23b96af35865dc997fcb544fb40

Kotlin:        2.3.0
Groovy:        4.0.29
Ant:           Apache Ant(TM) version 1.10.15 compiled on August 25 2024
Launcher JVM:  21.0.9 (Eclipse Adoptium 21.0.9+10-LTS)
Daemon JVM:    Compatible with Java 21, any vendor, nativeImageCapable=false (from gradle/gradle-daemon-jvm.properties)
OS:            Windows 11 10.0 amd64
```