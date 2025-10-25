package com.adiputrastwn.cleanandroidcompose

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adiputrastwn.cleanandroidcompose.samples.MemoryLeakDemoActivity
import com.adiputrastwn.cleanandroidcompose.ui.theme.CleanAndroidComposeTheme
import timber.log.Timber

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
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Greeting(name = "Android")

        Button(onClick = onNavigateToLeakDemo) {
            Text("Open LeakCanary Demo")
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