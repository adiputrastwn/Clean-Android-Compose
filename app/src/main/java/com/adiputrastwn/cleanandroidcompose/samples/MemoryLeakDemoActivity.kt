package com.adiputrastwn.cleanandroidcompose.samples

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adiputrastwn.cleanandroidcompose.ui.theme.CleanAndroidComposeTheme
import timber.log.Timber

/**
 * Activity demonstrating real memory leaks that LeakCanary will detect.
 *
 * After triggering a leak, navigate back or finish the activity.
 * LeakCanary will detect and report the leak within a few seconds.
 *
 * To use:
 * 1. Add this activity to AndroidManifest.xml
 * 2. Navigate to this activity from MainActivity
 * 3. Click leak buttons
 * 4. Press back to finish activity
 * 5. Wait for LeakCanary notification
 */
class MemoryLeakDemoActivity : ComponentActivity() {

    private val viewModel: LeakyViewModel by viewModels()
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.d("MemoryLeakDemoActivity created")

        setContent {
            CleanAndroidComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MemoryLeakDemoScreen(
                        onStaticLeakClick = { triggerStaticLeak() },
                        onHandlerLeakClick = { triggerHandlerLeak() },
                        onSingletonLeakClick = { triggerSingletonLeak() },
                        onViewModelLeakClick = { triggerViewModelLeak() },
                        onThreadLeakClick = { triggerThreadLeak() }
                    )
                }
            }
        }
    }

    /**
     * LEAK 1: Static reference to Activity
     * This stores the Activity instance in a companion object,
     * preventing it from being garbage collected.
     */
    private fun triggerStaticLeak() {
        staticActivityReference = this
        Timber.w("Static leak triggered! Activity reference stored in companion object")
        showToast("Static leak created - close activity to see LeakCanary alert")
    }

    /**
     * LEAK 2: Handler with delayed callback
     * The Handler holds an implicit reference to the Activity through the Runnable.
     * If the Activity is destroyed before the callback executes, it leaks.
     */
    private fun triggerHandlerLeak() {
        handler.postDelayed({
            Timber.d("Delayed handler callback executed")
            // This callback holds implicit reference to Activity
        }, 60000) // 60 seconds delay

        Timber.w("Handler leak triggered! Close activity within 60 seconds")
        showToast("Handler leak created - close activity now")
    }

    /**
     * LEAK 3: Singleton holding Activity reference
     * Registering the Activity as a listener in a singleton causes a leak.
     */
    private fun triggerSingletonLeak() {
        ActivityLeakSingleton.register(this)
        Timber.w("Singleton leak triggered! Activity registered as listener")
        showToast("Singleton leak created - close activity to see leak")
    }

    /**
     * LEAK 4: ViewModel holding Activity reference
     * ViewModels outlive Activities during configuration changes,
     * so storing Activity reference causes a leak.
     */
    private fun triggerViewModelLeak() {
        viewModel.leakActivity(this)
        Timber.w("ViewModel leak triggered! Activity stored in ViewModel")
        showToast("ViewModel leak created - rotate screen or close activity")
    }

    /**
     * LEAK 5: Thread holding Activity reference
     * Long-running thread with implicit Activity reference.
     */
    private fun triggerThreadLeak() {
        Thread {
            try {
                Thread.sleep(60000) // Sleep for 60 seconds
                Timber.d("Thread completed: ${this@MemoryLeakDemoActivity.javaClass.simpleName}")
            } catch (e: InterruptedException) {
                Timber.d("Thread interrupted")
            }
        }.start()

        Timber.w("Thread leak triggered! Long-running thread holds Activity reference")
        showToast("Thread leak created - close activity now")
    }

    private fun showToast(message: String) {
        // Using applicationContext to avoid leaking Activity in Toast
        android.widget.Toast.makeText(applicationContext, message, android.widget.Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("MemoryLeakDemoActivity destroyed - LeakCanary will analyze for leaks")

        // NOTE: Not cleaning up handlers/threads intentionally to demonstrate leaks
        // In production, you should always clean up:
        // handler.removeCallbacksAndMessages(null)
    }

    companion object {
        // This static reference will leak the Activity
        private var staticActivityReference: MemoryLeakDemoActivity? = null
    }
}

@Composable
private fun MemoryLeakDemoScreen(
    onStaticLeakClick: () -> Unit,
    onHandlerLeakClick: () -> Unit,
    onSingletonLeakClick: () -> Unit,
    onViewModelLeakClick: () -> Unit,
    onThreadLeakClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Memory Leak Demo",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text(
            text = "Tap a button to create a leak, then press back to close this activity. LeakCanary will detect and report the leak.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Leak buttons
        LeakButton(
            title = "Static Reference Leak",
            description = "Stores Activity in companion object",
            severity = "High",
            onClick = onStaticLeakClick
        )

        LeakButton(
            title = "Handler Callback Leak",
            description = "Handler with 60s delayed callback",
            severity = "High",
            onClick = onHandlerLeakClick
        )

        LeakButton(
            title = "Singleton Listener Leak",
            description = "Activity registered in singleton",
            severity = "Critical",
            onClick = onSingletonLeakClick
        )

        LeakButton(
            title = "ViewModel Reference Leak",
            description = "Activity stored in ViewModel",
            severity = "High",
            onClick = onViewModelLeakClick
        )

        LeakButton(
            title = "Thread Reference Leak",
            description = "Long-running thread holds Activity",
            severity = "Medium",
            onClick = onThreadLeakClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Instructions card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "How to test:",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "1. Tap any leak button above",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "2. Press back to finish this activity",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "3. Wait 5-10 seconds",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "4. Check notification for LeakCanary alert",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "5. Tap notification to see leak trace",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun LeakButton(
    title: String,
    description: String,
    severity: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            SeverityBadge(severity = severity)
        }
    }
}

@Composable
private fun SeverityBadge(severity: String) {
    val color = when (severity) {
        "Critical" -> MaterialTheme.colorScheme.error
        "High" -> MaterialTheme.colorScheme.errorContainer
        "Medium" -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Surface(
        color = color,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = severity,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

/**
 * Singleton that leaks Activity references
 */
object ActivityLeakSingleton {
    private val listeners = mutableListOf<MemoryLeakDemoActivity>()

    fun register(activity: MemoryLeakDemoActivity) {
        listeners.add(activity)
        Timber.d("Activity registered in singleton. Total: ${listeners.size}")
    }

    // This would fix the leak if called, but we intentionally don't call it
    @Suppress("unused")
    fun unregister(activity: MemoryLeakDemoActivity) {
        listeners.remove(activity)
        Timber.d("Activity unregistered from singleton")
    }
}