package com.adiputrastwn.cleanandroidcompose

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adiputrastwn.cleanandroidcompose.samples.ImageLoadingSamplesScreen
import com.adiputrastwn.cleanandroidcompose.samples.MemoryLeakDemoActivity
import com.adiputrastwn.cleanandroidcompose.ui.theme.CleanAndroidComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("MainActivity onCreate started")
        enableEdgeToEdge()
        setContent {
            CleanAndroidComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        onNavigateToLeakDemo = {
                            startActivity(Intent(this, MemoryLeakDemoActivity::class.java))
                        }
                    )
                }
            }
        }
        Timber.i("MainActivity UI setup completed")
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onNavigateToLeakDemo: () -> Unit = {}
) {
    var showImageSamples by remember { mutableStateOf(false) }

    if (showImageSamples) {
        ImageLoadingSamplesScreen(modifier = modifier)
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = "Clean Android Compose",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Greeting(name = "Android")

            Spacer(modifier = Modifier.height(24.dp))

            // Coil Image Loading Demo
            ElevatedButton(
                onClick = { showImageSamples = true },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("View Coil Image Loading Samples")
            }

            // LeakCanary Demo
            Button(
                onClick = onNavigateToLeakDemo,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Open LeakCanary Demo")
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CleanAndroidComposeTheme {
        Greeting("Android")
    }
}