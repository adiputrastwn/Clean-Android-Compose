package com.adiputrastwn.cleanandroidcompose.samples

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adiputrastwn.cleanandroidcompose.ui.theme.CleanAndroidComposeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import leakcanary.AppWatcher
import leakcanary.ObjectWatcher
import timber.log.Timber

/**
 * Sample Activity demonstrating LeakCanary usage and common memory leak scenarios
 *
 * LeakCanary automatically detects memory leaks in Activities, Fragments, ViewModels, etc.
 * This file shows various scenarios and how to use LeakCanary's features.
 */
class LeakCanarySampleActivity : ComponentActivity() {

    // EXAMPLE 1: Activity Context Leak (BAD - causes memory leak)
    companion object {
        // This will leak the Activity instance
        private var leakyActivityReference: Activity? = null
    }

    // EXAMPLE 2: Handler Leak (BAD - causes memory leak)
    private val handler = Handler(Looper.getMainLooper())
    private val leakyRunnable = Runnable {
        // This runnable holds implicit reference to Activity
        Timber.d("Delayed operation")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CleanAndroidComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) { LeakDemoScreen() }
            }
        }
    }

    @Composable
    fun LeakDemoScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = "LeakCanary Demo",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Navigate away to see leak detection in action",
                style = MaterialTheme.typography.bodyMedium
            )

            Button(onClick = { simulateActivityLeak() }) {
                Text("Simulate Activity Leak")
            }

            Button(onClick = { simulateHandlerLeak() }) {
                Text("Simulate Handler Leak")
            }

            Button(onClick = { watchCustomObject() }) {
                Text("Watch Custom Object")
            }

            Button(onClick = { triggerManualGC() }) {
                Text("Trigger Manual GC")
            }
        }
    }

    // EXAMPLE 3: Simulating an Activity leak
    private fun simulateActivityLeak() {
        // This creates a memory leak by storing Activity reference in companion object
        leakyActivityReference = this
        Timber.d("Activity reference leaked - LeakCanary will detect this")
    }

    // EXAMPLE 4: Simulating a Handler leak
    private fun simulateHandlerLeak() {
        // Posting delayed runnable without cleanup causes leak
        handler.postDelayed(leakyRunnable, 60000) // 1 minute delay
        Timber.d("Handler leak created - finish activity before runnable executes")
    }

    // EXAMPLE 5: Watching custom objects
    private fun watchCustomObject() {
        val customObject = CustomLeakyObject()

        // Manually watch an object that should be garbage collected
        AppWatcher.objectWatcher.watch(
            watchedObject = customObject,
            description = "CustomLeakyObject instance"
        )

        Timber.d("Custom object is now being watched by LeakCanary")
    }

    // EXAMPLE 6: Manual garbage collection trigger (for testing)
    private fun triggerManualGC() {
        Runtime.getRuntime().gc()
        System.runFinalization()
        Timber.d("Manual GC triggered")
    }

    override fun onDestroy() {
        super.onDestroy()
        // GOOD PRACTICE: Clean up handlers to prevent leaks
        // Uncomment this to fix the handler leak:
        // handler.removeCallbacks(leakyRunnable)

        Timber.d("Activity destroyed - LeakCanary will analyze for leaks")
    }
}

/**
 * EXAMPLE 7: ViewModel with potential leak
 */
class LeakyViewModel : ViewModel() {

    // BAD: Storing Activity/Context reference in ViewModel causes leak
    private var activityReference: Activity? = null

    fun leakActivity(activity: Activity) {
        activityReference = activity // This will leak!
        Timber.w("Activity reference stored in ViewModel - this is a leak!")
    }

    fun setActivity(activity: Activity) {
        activityReference = activity // This will leak!
        Timber.w("Activity reference stored in ViewModel - this is a leak!")
    }

    // GOOD: Use Application Context instead
    private var applicationContext: Context? = null

    fun setApplicationContext(context: Context) {
        applicationContext = context.applicationContext
        Timber.d("Application context stored - this is safe")
    }

    // Example of long-running operation
    fun startLongOperation() {
        viewModelScope.launch {
            repeat(100) { i ->
                delay(1000)
                Timber.d("Long operation: $i")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("ViewModel cleared")
        // Clean up resources
        activityReference = null
        applicationContext = null
    }
}

/**
 * EXAMPLE 8: Custom object to watch
 */
private class CustomLeakyObject {
    private val largeData = ByteArray(10 * 1024 * 1024) // 10 MB

    init {
        Timber.d("CustomLeakyObject created with large data")
    }
}

/**
 * EXAMPLE 9: Singleton that leaks Activity (BAD pattern)
 */
object LeakySingleton {
    private var activityListener: ActivityListener? = null

    interface ActivityListener {
        fun onEvent()
    }

    // BAD: This will leak Activity if you pass Activity as listener
    fun setListener(listener: ActivityListener) {
        activityListener = listener
    }

    // GOOD: Always provide cleanup method
    fun clearListener() {
        activityListener = null
    }
}

/**
 * EXAMPLE 10: Proper resource cleanup pattern
 */
class ProperCleanupExample : ComponentActivity() {

    private val resources = mutableListOf<AutoCloseable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register resources that need cleanup
        val resource = createResource()
        resources.add(resource)

        setContent {
            CleanAndroidComposeTheme {
                Text("Proper cleanup example")
            }
        }
    }

    private fun createResource(): AutoCloseable {
        return object : AutoCloseable {
            override fun close() {
                Timber.d("Resource cleaned up")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // GOOD: Proper cleanup prevents leaks
        resources.forEach { it.close() }
        resources.clear()

        Timber.d("All resources cleaned up properly")
    }
}

/**
 * EXAMPLE 11: Using ObjectWatcher directly
 */
class ObjectWatcherExample {

    fun demonstrateObjectWatcher() {
        val objectWatcher: ObjectWatcher = AppWatcher.objectWatcher

        // Watch an object
        val myObject = Any()
        objectWatcher.watch(
            watchedObject = myObject,
            description = "My custom object that should be GC'd"
        )

        // The object reference should be set to null when no longer needed
        // If it's not GC'd after that, LeakCanary will report it

        Timber.d("Object is now being watched")
    }

    fun watchWithCustomDescription() {
        val user = User("John Doe")

        AppWatcher.objectWatcher.watch(
            watchedObject = user,
            description = "User object for ${user.name}"
        )

        Timber.d("User object is being watched")
    }

    data class User(val name: String)
}

/**
 * EXAMPLE 12: Compose-specific leak scenario
 */
@Composable
fun ComposableWithPotentialLeak(context: Context) {
    // BAD: Storing Activity context in remember
    // This can cause leaks if the composition outlives the Activity
    val storedContext by remember { mutableStateOf(context) }

    // GOOD: Use LocalContext.current or pass only what you need
    // val localContext = LocalContext.current

    DisposableEffect(Unit) {
        Timber.d("Composable created")

        onDispose {
            Timber.d("Composable disposed - cleanup happens here")
            // Clean up any resources here
        }
    }

    Text("Check for leaks")
}

/**
 * EXAMPLE 13: Listener leak pattern and fix
 */
class ListenerLeakExample(private val context: Context) {

    private val listeners = mutableListOf<EventListener>()

    interface EventListener {
        fun onEvent()
    }

    // BAD: If Activities register as listeners and forget to unregister
    fun registerListener(listener: EventListener) {
        listeners.add(listener)
    }

    // GOOD: Always provide unregister method
    fun unregisterListener(listener: EventListener) {
        listeners.remove(listener)
    }

    // BETTER: Use weak references
    private val weakListeners = mutableListOf<java.lang.ref.WeakReference<EventListener>>()

    fun registerWeakListener(listener: EventListener) {
        weakListeners.add(java.lang.ref.WeakReference(listener))
    }

    fun notifyListeners() {
        // Clean up null weak references
        weakListeners.removeAll { it.get() == null }

        weakListeners.forEach { weakRef ->
            weakRef.get()?.onEvent()
        }
    }

    fun cleanup() {
        listeners.clear()
        weakListeners.clear()
        Timber.d("All listeners cleared")
    }
}

/**
 * Usage Notes:
 *
 * 1. LeakCanary is automatically initialized - no setup needed beyond adding the dependency
 *
 * 2. It automatically watches:
 *    - Activities
 *    - Fragments
 *    - Fragment Views
 *    - ViewModels
 *    - Services
 *
 * 3. To watch custom objects:
 *    AppWatcher.objectWatcher.watch(myObject, "description")
 *
 * 4. Leak detection happens after:
 *    - Object is destroyed
 *    - App goes to background
 *    - Manual GC is triggered
 *
 * 5. View leaks in:
 *    - Notification when leak is detected
 *    - LeakCanary app (installed automatically in debug builds)
 *
 * 6. Common leak sources:
 *    - Static references to Activities/Contexts
 *    - Non-static inner classes
 *    - Handlers and Runnables
 *    - Listeners not unregistered
 *    - Threads not stopped
 *    - Rx subscriptions not disposed
 *    - Coroutines not cancelled
 *
 * 7. Best practices:
 *    - Use Application Context for long-lived objects
 *    - Always cleanup in onDestroy/onCleared
 *    - Use weak references for listeners
 *    - Cancel coroutines in ViewModel.onCleared
 *    - Remove callbacks in onDestroy
 */