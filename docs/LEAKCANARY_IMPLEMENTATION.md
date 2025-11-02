# LeakCanary Implementation Summary

## Overview
LeakCanary is configured in this project for automatic memory leak detection in debug builds. It runs in the background without requiring any code changes or manual setup.

## Current Implementation

### Configuration

#### Dependencies
```kotlin
// In app/build.gradle.kts
debugImplementation(libs.leakcanary.android)

// In gradle/libs.versions.toml
leakcanaryAndroid = "2.14"
leakcanary-android = { module = "com.squareup.leakcanary:leakcanary-android", version.ref = "leakcanaryAndroid" }
```

## How LeakCanary Works

### Automatic Detection
LeakCanary automatically works in debug builds without any configuration. Once you add the dependency, it:

1. **Automatically initializes** when your app starts
2. **Watches for memory leaks** in:
   - Activities
   - Fragments
   - Fragment Views
   - ViewModels
   - Services
3. **Detects leaks** within 5-10 seconds after objects should have been garbage collected
4. **Shows notifications** when leaks are detected
5. **Provides detailed leak traces** in the LeakCanary app

### Using LeakCanary

1. **Run your app** (debug build only)
   ```bash
   ./gradlew installDebug
   ```

2. **Use your app normally**
   - Navigate between screens
   - Open and close Activities/Fragments
   - LeakCanary works automatically in the background

3. **View leak notifications**
   - When a leak is detected, you'll receive a notification
   - Tap the notification to open the LeakCanary app
   - View detailed leak trace with reference chain

## Common Memory Leak Types

Understanding common memory leak patterns helps you avoid them in your code. Here are the most frequent types:

### 1. Static Reference Leak (High Severity)
**Problem**: Storing Activity/Fragment/View in static fields
```kotlin
companion object {
    private var staticActivityReference: Activity? = null
}
```
**Fix**: Never store Activity, Fragment, or View in static/companion objects. Use Application Context for long-lived references.

### 2. Handler Callback Leak (High Severity)
**Problem**: Handler callbacks outliving Activity lifecycle
```kotlin
handler.postDelayed({ /* ... */ }, 60000)
```
**Fix**: Remove callbacks in `onDestroy()`:
```kotlin
override fun onDestroy() {
    super.onDestroy()
    handler.removeCallbacksAndMessages(null)
}
```

### 3. Singleton Listener Leak (Critical Severity)
**Problem**: Singleton objects holding Activity references
```kotlin
object MySingleton {
    private val listeners = mutableListOf<Activity>()
}
```
**Fix**: Always unregister listeners or use `WeakReference<Activity>`

### 4. ViewModel Reference Leak (High Severity)
**Problem**: ViewModel holding Activity/Fragment reference
```kotlin
class MyViewModel : ViewModel() {
    private var activity: Activity? = null // BAD!
}
```
**Fix**: Use Application Context instead of Activity Context

### 5. Thread/Coroutine Reference Leak (Medium Severity)
**Problem**: Long-running operations holding Activity references
```kotlin
// In ViewModel - using coroutines
viewModelScope.launch {
    repeat(100) { i ->
        delay(1000)
        activity?.let { /* using activity */ }
    }
}
```
**Fix**: Use `viewModelScope` (auto-cancelled) or cancel manually in `onCleared()`

## LeakCanary Features

### Automatic Detection
LeakCanary automatically watches:
- Activities
- Fragments
- Fragment Views
- ViewModels
- Services

### Manual Object Watching
For custom objects, you can manually track them:
```kotlin
import leakcanary.AppWatcher

val myObject = MyCustomObject()
AppWatcher.objectWatcher.expectWeaklyReachable(
    watchedObject = myObject,
    description = "MyCustomObject instance"
)
```

### Zero Configuration
LeakCanary is automatically initialized - no setup code needed! Just add the dependency and it works.

## Best Practices

### ✅ DO:
- Use Application Context for long-lived objects
- Clean up in `onDestroy()` and `ViewModel.onCleared()`
- Use weak references for listeners
- Cancel coroutines in ViewModels
- Remove callbacks and handlers
- Unregister all listeners/observers

### ❌ DON'T:
- Store Activity in static fields
- Keep Activity references in ViewModels
- Start threads without cleanup
- Register listeners without unregistering
- Use non-static inner classes that reference Activity
- Forget to dispose subscriptions

## Project Structure

LeakCanary is configured at the dependency level:

```
app/
├── build.gradle.kts              # LeakCanary dependency
└── src/main/java/com/adiputrastwn/cleanandroidcompose/
    └── CleanAndroidComposeApp.kt # Application class (Hilt + Timber)

gradle/
└── libs.versions.toml            # Version catalog with LeakCanary version
```

## Testing LeakCanary

### Quick Test
1. Build and install debug variant:
   ```bash
   ./gradlew installDebug
   ```

2. Use your app normally - navigate between screens, open/close features

3. If there are any memory leaks, LeakCanary will:
   - Detect them within 5-10 seconds
   - Show a notification
   - Allow you to view the leak trace in the LeakCanary app

## Additional Notes

### Debug Only
LeakCanary only runs in debug builds. No impact on release builds.

### Performance
Heap dumps happen in background. Minimal impact on app performance.

### Storage
LeakCanary stores heap dumps temporarily. Old dumps are cleaned automatically.

### Notification
LeakCanary shows notifications when leaks are detected. Tap to view details.

## Resources

- [LeakCanary Official Docs](https://square.github.io/leakcanary/)
- [Android Memory Guide](https://developer.android.com/topic/performance/memory-overview)
- [Memory Profiler](https://developer.android.com/studio/profile/memory-profiler)

## Summary

**Current Status**:
- ✓ LeakCanary 2.14 configured in `app/build.gradle.kts`
- ✓ Automatic memory leak detection enabled for debug builds
- ✓ Zero configuration required - works out of the box
- ✓ Monitors Activities, Fragments, ViewModels, and Services

**Implementation**:
- Debug-only dependency (`debugImplementation`)
- Automatic initialization - no code changes needed
- Background leak detection with notifications
- Detailed leak traces in LeakCanary app

**Usage**: Simply run your debug build and LeakCanary will automatically detect and report any memory leaks. No interactive demo is included - LeakCanary works silently in the background during normal app usage.