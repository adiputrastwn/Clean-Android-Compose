# LeakCanary Demo

This directory contains sample implementations demonstrating how to use LeakCanary for memory leak detection in Android applications.

## Files

### 1. MemoryLeakDemoActivity.kt
An interactive activity that allows you to trigger different types of memory leaks and see LeakCanary in action.

**Features:**
- 5 different types of memory leaks to demonstrate
- User-friendly UI with severity indicators
- Step-by-step instructions
- Real-time leak detection

### 2. LeakCanarySamples.kt
Comprehensive documentation and code examples showing:
- 13 different memory leak scenarios
- Best practices for preventing leaks
- LeakCanary API usage examples
- Common anti-patterns and their fixes

## How to Use

### Quick Start

1. **Run the app** in debug mode (LeakCanary only works in debug builds)

2. **Open the demo** by tapping "Open LeakCanary Demo" button on the main screen

3. **Trigger a leak:**
   - Tap any of the leak buttons (e.g., "Static Reference Leak")
   - You'll see a toast notification confirming the leak was created

4. **Navigate back** to finish the activity
   - Press the back button to destroy the activity

5. **Wait for detection:**
   - LeakCanary will analyze the heap dump (5-10 seconds)
   - You'll receive a notification when a leak is detected

6. **View the leak trace:**
   - Tap the LeakCanary notification
   - Open the LeakCanary app (automatically installed in debug builds)
   - View detailed information about the leak, including the reference chain

## Types of Memory Leaks Demonstrated

### 1. Static Reference Leak (High Severity)
**What it does:** Stores Activity instance in a companion object (static field)

**Why it leaks:** Static references live for the entire app lifecycle, preventing garbage collection of the Activity

**Example:**
```kotlin
companion object {
    private var staticActivityReference: Activity? = null
}
```

### 2. Handler Callback Leak (High Severity)
**What it does:** Posts a delayed callback (60 seconds) without cleanup

**Why it leaks:** Handler holds implicit reference to Activity through the Runnable. If Activity is destroyed before callback executes, it leaks.

**Example:**
```kotlin
handler.postDelayed({
    // Implicit Activity reference
}, 60000)
```

**Fix:**
```kotlin
override fun onDestroy() {
    super.onDestroy()
    handler.removeCallbacksAndMessages(null)
}
```

### 3. Singleton Listener Leak (Critical Severity)
**What it does:** Registers Activity as a listener in an application-scoped singleton

**Why it leaks:** Singleton outlives Activity, keeping strong reference to it

**Example:**
```kotlin
object LeakySingleton {
    private val listeners = mutableListOf<Activity>()

    fun register(activity: Activity) {
        listeners.add(activity)
    }
}
```

**Fix:**
```kotlin
// Always unregister in onDestroy
override fun onDestroy() {
    super.onDestroy()
    LeakySingleton.unregister(this)
}

// Or use weak references
private val listeners = mutableListOf<WeakReference<Listener>>()
```

### 4. ViewModel Reference Leak (High Severity)
**What it does:** Stores Activity reference in ViewModel

**Why it leaks:** ViewModels survive configuration changes. Storing Activity causes leak during rotation.

**Example:**
```kotlin
class MyViewModel : ViewModel() {
    private var activity: Activity? = null // BAD!

    fun setActivity(activity: Activity) {
        this.activity = activity
    }
}
```

**Fix:**
```kotlin
// Use Application Context instead
private var appContext: Context? = null

fun setContext(context: Context) {
    this.appContext = context.applicationContext
}
```

### 5. Thread Reference Leak (Medium Severity)
**What it does:** Starts long-running thread with implicit Activity reference

**Why it leaks:** Thread keeps running after Activity is destroyed, holding reference

**Example:**
```kotlin
Thread {
    Thread.sleep(60000)
    // Implicit Activity reference through 'this@Activity'
}.start()
```

**Fix:**
```kotlin
class MyActivity : ComponentActivity() {
    private var backgroundThread: Thread? = null

    override fun onDestroy() {
        super.onDestroy()
        backgroundThread?.interrupt()
        backgroundThread = null
    }
}
```

## LeakCanary Features

### Automatic Detection
LeakCanary automatically watches for leaks in:
- Activities
- Fragments
- Fragment Views
- ViewModels
- Services

### Manual Object Watching
You can watch custom objects:

```kotlin
import leakcanary.AppWatcher

val myObject = MyCustomObject()
AppWatcher.objectWatcher.watch(
    watchedObject = myObject,
    description = "MyCustomObject instance"
)
```

### Configuration
LeakCanary is already configured in `app/build.gradle.kts`:

```kotlin
dependencies {
    debugImplementation(libs.leakcanary.android)
}
```

No additional setup needed! It's automatically initialized.

## Best Practices

### ✅ DO:
- Use Application Context for long-lived objects
- Always cleanup in `onDestroy()` and `ViewModel.onCleared()`
- Use weak references for listeners
- Cancel coroutines in `ViewModel.onCleared()`
- Remove callbacks in `onDestroy()`
- Unregister listeners, receivers, observers

### ❌ DON'T:
- Store Activity/Context in static fields
- Store Activity references in ViewModels
- Keep long-running threads without cleanup
- Register listeners without unregistering
- Use non-static inner classes that reference outer Activity
- Forget to dispose RxJava subscriptions

## Compose-Specific Tips

### Memory Leaks in Compose
```kotlin
@Composable
fun MyComposable(context: Context) {
    // BAD: Storing context in remember can leak
    val storedContext by remember { mutableStateOf(context) }

    // GOOD: Use LocalContext or dispose properly
    val localContext = LocalContext.current

    DisposableEffect(Unit) {
        // Setup
        onDispose {
            // Cleanup happens here
        }
    }
}
```

## Viewing Leak Reports

### Via Notification
1. Wait for LeakCanary notification
2. Tap notification
3. View leak trace with reference chain

### Via LeakCanary App
1. Open app drawer
2. Find "LeakCanary" app (installed automatically in debug)
3. View all detected leaks
4. Share heap dump for analysis

### Understanding Leak Traces
LeakCanary shows:
- **Leak trace**: Chain of references keeping object alive
- **Reference type**: Strong, weak, soft references
- **Retained size**: Memory held by leaked object
- **GC root**: What's holding the reference

Example trace:
```
┬───
│ GC Root: System class
├─ com.example.LeakySingleton class
│    Leaking: NO (it's a class)
│    ↓ static LeakySingleton.listeners
├─ java.util.ArrayList instance
│    Leaking: UNKNOWN
│    ↓ ArrayList[0]
├─ com.example.MainActivity instance
│    Leaking: YES (Activity#mDestroyed is true)
╰→ com.example.MainActivity
```

## Testing Strategy

1. **Development**: Enable LeakCanary in debug builds (default)
2. **Testing**: Trigger leaks intentionally to verify detection
3. **CI/CD**: Run instrumented tests with leak detection
4. **Production**: LeakCanary is debug-only, no impact on release builds

## Troubleshooting

### Leaks not detected?
- Ensure you're running a **debug build**
- Wait 5-10 seconds after closing activity
- Try putting app in background
- Check if heap dump is being analyzed

### Too many false positives?
- Update LeakCanary to latest version
- Check if objects are actually leaking
- Some Android framework leaks are expected

### Performance impact?
- LeakCanary only runs in debug builds
- No impact on release/production builds
- Heap dumps happen in background

## Additional Resources

- [LeakCanary Documentation](https://square.github.io/leakcanary/)
- [Understanding Memory Leaks](https://developer.android.com/topic/performance/memory-overview)
- [Android Memory Profiler](https://developer.android.com/studio/profile/memory-profiler)

## Implementation Checklist

When implementing LeakCanary in your project:

- [x] Add LeakCanary dependency to `build.gradle.kts`
- [x] Run app in debug mode
- [x] Trigger potential leaks
- [x] Verify leak detection works
- [ ] Fix identified leaks
- [ ] Add unit tests for leak-prone code
- [ ] Document common leak patterns in your codebase
- [ ] Train team on memory leak prevention

## Summary

This demo shows you how to:
1. ✅ Identify common memory leak patterns
2. ✅ Use LeakCanary to detect leaks automatically
3. ✅ Understand leak traces and fix them
4. ✅ Prevent leaks with best practices

Happy leak hunting! 🔍