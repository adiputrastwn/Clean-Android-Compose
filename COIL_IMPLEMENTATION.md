# Coil Compose Implementation Guide

This document describes the Coil image loading library integration in the Clean Android Compose project.

## Overview

Coil 3.3.0 has been successfully integrated into the project to provide efficient, modern image loading capabilities for Jetpack Compose.

## What is Coil?

Coil is a fast, lightweight, and modern image loading library for Android and Compose Multiplatform. It leverages Kotlin Coroutines and Okio for efficient image handling.

Key features:
- Fast and lightweight
- Kotlin-first with Coroutines support
- Memory and disk caching
- Image transformations
- Crossfade animations
- Compose-native integration

## Implementation Details

### 1. Dependencies Added

**Version Catalog** (`gradle/libs.versions.toml`):
```toml
[versions]
coil = "3.3.0"

[libraries]
coil-compose = { group = "io.coil-kt.coil3", name = "coil-compose", version.ref = "coil" }
coil-network-okhttp = { group = "io.coil-kt.coil3", name = "coil-network-okhttp", version.ref = "coil" }
```

**App Build File** (`app/build.gradle.kts`):
```kotlin
// Coil image loading
implementation(libs.coil.compose)
implementation(libs.coil.network.okhttp)
```

### 2. Hilt Configuration

**ImageLoader Provider** (`di/AppModule.kt`):

The `ImageLoader` is configured as a singleton and provided through Hilt dependency injection with:

- **Memory Cache**: 10% of available memory for fast repeated access
- **Disk Cache**: 50 MB fixed size for offline access and persistence
- **Crossfade Animation**: Smooth transitions when images load
- **OkHttp Integration**: Efficient network requests with connection pooling
- **Debug Logging**: Enabled in debug builds for troubleshooting
- **Okio Path**: Uses Okio for efficient file system operations

```kotlin
@Provides
@Singleton
fun provideImageLoader(
    @ApplicationContext context: Context,
    okHttpClient: OkHttpClient
): ImageLoader {
    return ImageLoader.Builder(context)
        .components {
            add(OkHttpNetworkFetcherFactory(okHttpClient))
        }
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(context, 0.10)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache").toOkioPath())
                .maxSizeBytes(50 * 1024 * 1024) // 50 MB
                .build()
        }
        .crossfade(true)
        .apply {
            if (BuildConfig.DEBUG) {
                logger(DebugLogger())
            }
        }
        .build()
}
```

### 3. Sample Implementation

**Image Loading Samples** (`samples/ImageLoadingSamples.kt`):

The project includes comprehensive examples demonstrating:

1. **Basic Image Loading**: Simple `AsyncImage` with URL
2. **Loading States**: `SubcomposeAsyncImage` with `CircularProgressIndicator`
3. **Circle Avatar**: Image with `CircleShape` clip
4. **Rounded Corners**: Image with `RoundedCornerShape`
5. **Advanced Configuration**: `ImageRequest` with crossfade and custom settings

### 4. MainActivity Integration

The main screen now includes a button to view Coil image loading samples, allowing users to explore different image loading patterns.

## Usage Examples

### Basic Image Loading

```kotlin
AsyncImage(
    model = "https://example.com/image.jpg",
    contentDescription = "Description for accessibility",
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp),
    contentScale = ContentScale.Crop
)
```

### Image with Loading State

```kotlin
SubcomposeAsyncImage(
    model = imageUrl,
    contentDescription = "Description",
    loading = {
        CircularProgressIndicator()
    },
    modifier = Modifier.size(200.dp)
)
```

### Circle Avatar

```kotlin
AsyncImage(
    model = avatarUrl,
    contentDescription = "User avatar",
    modifier = Modifier
        .size(120.dp)
        .clip(CircleShape),
    contentScale = ContentScale.Crop
)
```

### Advanced Configuration

```kotlin
AsyncImage(
    model = ImageRequest.Builder(LocalContext.current)
        .data(imageUrl)
        .crossfade(true)
        .build(),
    contentDescription = "Description",
    modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
)
```

## Internet Permission

Make sure your `AndroidManifest.xml` includes the internet permission:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

This is required for loading images from network URLs.

## Testing the Implementation

1. **Build the project**: Run Gradle sync to download dependencies
2. **Run the app**: Launch the app on an emulator or device
3. **Navigate to samples**: Tap "View Coil Image Loading Samples" button
4. **Verify image loading**: Check that images load correctly with appropriate loading states

## Architecture Integration

The Coil implementation follows Clean Architecture principles:

- **Dependency Injection**: ImageLoader provided via Hilt
- **Separation of Concerns**: Image loading logic separated into dedicated samples
- **Testability**: Can easily mock ImageLoader for testing
- **Memory Management**: Integrated with LeakCanary for monitoring

## Performance Considerations

- **Memory Cache**: Set to 10% of available memory to reduce network calls for frequently accessed images while keeping memory usage conservative
- **Disk Cache**: Fixed at 50 MB to provide offline support and faster subsequent loads without consuming excessive storage
- **Efficient Loading**: Only loads images at the required size based on the composable's dimensions
- **Automatic Cleanup**: Coil handles memory management automatically with least-recently-used (LRU) eviction
- **Okio Integration**: Uses Okio's efficient file system operations for disk caching

### Cache Configuration Rationale

The current configuration balances performance with resource constraints:

- **Memory Cache (10%)**: Conservative setting suitable for most devices, preventing out-of-memory issues on low-end devices
- **Disk Cache (50 MB)**: Fixed size provides predictable storage usage, adequate for typical image sets in mobile apps

You can adjust these values based on your app's specific needs:
- Increase memory cache percentage for image-heavy apps on high-end devices
- Increase disk cache size for apps requiring extensive offline image access

## Additional Resources

- [Coil Documentation](https://coil-kt.github.io/coil/)
- [Coil GitHub](https://github.com/coil-kt/coil)
- [Jetpack Compose Integration](https://coil-kt.github.io/coil/compose/)

## Files Modified/Created

### Modified
- `gradle/libs.versions.toml` - Added Coil dependencies
- `app/build.gradle.kts` - Included Coil libraries
- `app/src/main/java/com/adiputrastwn/cleanandroidcompose/di/AppModule.kt` - Added ImageLoader provider
- `app/src/main/java/com/adiputrastwn/cleanandroidcompose/MainActivity.kt` - Added image samples navigation

### Created
- `app/src/main/java/com/adiputrastwn/cleanandroidcompose/samples/ImageLoadingSamples.kt` - Comprehensive image loading examples
- `COIL_IMPLEMENTATION.md` - This documentation file

## Next Steps

To extend the Coil implementation:

1. Add custom image transformations (blur, grayscale, etc.)
2. Implement placeholder and error images
3. Add image caching strategies for specific use cases
4. Create custom Fetcher for special data sources
5. Implement image preloading for better UX

## Troubleshooting

### Images not loading
- Check internet permission in AndroidManifest.xml
- Verify URLs are accessible
- Check debug logs for error messages

### Out of memory errors
- Reduce memory cache percentage
- Implement image size optimization
- Use appropriate ContentScale values

### Slow loading
- Check network connection
- Verify disk cache is working
- Consider image size optimization at source