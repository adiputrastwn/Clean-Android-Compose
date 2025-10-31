# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Clean Android Compose is a modern Android application template built with Jetpack Compose, following Clean Architecture principles. The project demonstrates best practices for Android development with Kotlin, including dependency injection, static analysis, automated testing, and deployment workflows.

**Package**: `com.adiputrastwn.cleanandroidcompose`

## Build System

### Gradle Configuration
- **Build System**: Gradle with Kotlin DSL (`.gradle.kts`)
- **Version Catalog**: All dependencies are centralized in `gradle/libs.versions.toml`
- **Java Toolchain**: JDK 21
- **Kotlin**: 2.2.21
- **Min SDK**: 23
- **Target/Compile SDK**: 36

### Common Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release AAB (requires signing configuration)
./gradlew bundleRelease

# Clean build
./gradlew clean

# Install debug build on connected device
./gradlew installDebug

# Run unit tests
./gradlew testDebugUnitTest

# Run all checks (lint + tests + detekt)
./gradlew check
```

## Code Quality & Static Analysis

### Detekt Configuration
Static code analysis is configured at the project level in `build.gradle.kts`:
- **Config file**: `config/detekt/detekt.yml`
- **Baseline file**: `config/detekt/baseline.xml`
- **Parallel execution**: Enabled
- **Fail on findings**: Yes

```bash
# Run Detekt on all modules
./gradlew detektAll

# Generate baseline for existing violations
./gradlew detektBaseline
```

### Lint
```bash
# Run Android lint on debug variant
./gradlew lintDebug

# View report at: app/build/reports/lint-results-debug.html
```

## Architecture

### Dependency Injection - Hilt
The project uses **Dagger Hilt** for dependency injection:
- **Application class**: `CleanAndroidComposeApp` annotated with `@HiltAndroidApp`
- **Module location**: `app/src/main/java/com/adiputrastwn/cleanandroidcompose/di/AppModule.kt`
- **KSP configuration**: Includes necessary compiler arguments for Hilt

Key provided dependencies in `AppModule`:
- `OkHttpClient` - Singleton for network operations
- `ImageLoader` (Coil) - Configured with memory cache (10% of available memory) and disk cache (50MB)

### Application Structure
```
app/src/main/java/com/adiputrastwn/cleanandroidcompose/
├── CleanAndroidComposeApp.kt   # Application class with Hilt and Timber initialization
├── MainActivity.kt              # Main Compose activity
├── di/                          # Dependency injection modules
│   └── AppModule.kt            # Application-level dependencies
├── ui/                          # UI components and theme
│   └── theme/                  # Material3 theme configuration
└── samples/                     # Sample implementations and demos
    ├── ImageLoadingSamples.kt  # Coil image loading examples
    ├── MemoryLeakDemoActivity.kt # LeakCanary demonstration
    └── LeakCanarySamples.kt    # Memory leak pattern documentation
```

### Logging - Timber
Timber is initialized in `CleanAndroidComposeApp`:
- Debug builds: `Timber.DebugTree()` planted automatically
- Production: Placeholder for custom crash reporting tree
- Usage: `Timber.d("message")`, `Timber.e(throwable, "message")`

### Image Loading - Coil
Coil 3.3.0 is configured in `AppModule` with:
- OkHttp integration for network fetching
- Memory cache: 10% of available memory
- Disk cache: 50MB in `cache_dir/image_cache`
- Crossfade animations enabled
- Debug logging in debug builds

See `docs/COIL_IMPLEMENTATION.md` for detailed usage examples.

### Memory Leak Detection - LeakCanary
LeakCanary 2.14 is automatically enabled in debug builds:
- No configuration needed - works out of the box
- Automatically watches Activities, Fragments, ViewModels, Services
- Interactive demo available in `MemoryLeakDemoActivity`

See `docs/LEAKCANARY_DEMO.md` and `docs/LEAKCANARY_IMPLEMENTATION.md` for detailed information.

## Fastlane Automation

Fastlane is configured for CI/CD automation. All lanes are defined in `fastlane/Fastfile`.

### Available Lanes

```bash
# List all available lanes
bundle exec fastlane lanes

# Code quality
bundle exec fastlane lint              # Run Android lint
bundle exec fastlane detekt            # Run Detekt static analysis
bundle exec fastlane code_quality      # Run both lint and detekt

# Testing
bundle exec fastlane test              # Run unit tests

# Building
bundle exec fastlane build_aab         # Build release AAB
bundle exec fastlane build_apk         # Build release APK

# CI/CD
bundle exec fastlane ci                # Full CI pipeline (quality + tests + build)

# Deployment (requires Google Play Console setup)
bundle exec fastlane deploy_internal   # Deploy to internal testing track
bundle exec fastlane deploy_beta       # Deploy to beta track
bundle exec fastlane deploy_production # Deploy to production

# Firebase App Distribution (requires Firebase setup)
bundle exec fastlane firebase_debug    # Distribute debug build
bundle exec fastlane firebase_release  # Distribute release build

# Version management
bundle exec fastlane version_info      # Display current version
bundle exec fastlane bump_version_code # Increment version code
bundle exec fastlane bump_version_name type:patch|minor|major
bundle exec fastlane bump_version type:patch|minor|major
```

See `docs/FASTLANE.md` for comprehensive Fastlane setup and usage guide.

## Testing

### Unit Tests
```bash
# Run all unit tests
./gradlew test

# Run tests for specific variant
./gradlew testDebugUnitTest

# Run tests with coverage (if configured)
./gradlew testDebugUnitTestCoverage
```

### Instrumented Tests
```bash
# Run instrumented tests on connected device
./gradlew connectedAndroidTest

# Run specific instrumented test
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.adiputrastwn.cleanandroidcompose.ExampleInstrumentedTest
```

## Documentation

Comprehensive documentation is available in the `docs/` directory:
- **COIL_IMPLEMENTATION.md** - Coil image loading setup and usage examples
- **DETEKT.md** - Detekt static analysis setup and best practices
- **FASTLANE.md** - Complete Fastlane integration guide with Google Play and Firebase setup
- **LEAKCANARY_DEMO.md** - Interactive memory leak detection guide
- **LEAKCANARY_IMPLEMENTATION.md** - LeakCanary setup summary

## Key Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| Jetpack Compose | 2025.10.01 (BOM) | UI framework |
| Hilt | 2.57.2 | Dependency injection |
| Coil | 3.3.0 | Image loading |
| Timber | 5.0.1 | Logging |
| LeakCanary | 2.14 | Memory leak detection (debug only) |
| Detekt | 1.23.8 | Static code analysis |

## Development Workflow

1. **Before committing**:
   ```bash
   ./gradlew detektAll  # Run static analysis
   ./gradlew test       # Run unit tests
   ```

2. **Creating new modules**:
   - Detekt is automatically applied to all subprojects via root `build.gradle.kts`
   - Add module to `settings.gradle.kts` with `include(":module-name")`

3. **Adding dependencies**:
   - Add versions to `[versions]` section in `gradle/libs.versions.toml`
   - Add library definitions to `[libraries]` section
   - Reference in module's `build.gradle.kts` as `implementation(libs.library.name)`

4. **Dependency injection**:
   - Create `@Module` classes in `di/` package
   - Annotate with `@InstallIn(SingletonComponent::class)` for app-level dependencies
   - Use `@Provides` for factory methods, `@Binds` for interface implementations

5. **Image loading**:
   - Use `AsyncImage` composable for basic loading
   - Use `SubcomposeAsyncImage` for custom loading states
   - ImageLoader is automatically injected via Hilt

## Branch Strategy

- **Main branch**: `master`
- **Current branch**: `implement-coil-compose`

When creating PRs, target the `master` branch.

## Important Notes

- **LeakCanary** only runs in debug builds - no production impact
- **Detekt baseline** is used to manage existing violations - update with `./gradlew detektBaseline`
- **Fastlane** requires Ruby and Bundler - run `bundle install` to set up dependencies
- **Version catalog** approach is used - avoid hardcoding dependency versions in build files
- **KSP** is used instead of kapt for Hilt annotation processing (faster compilation)