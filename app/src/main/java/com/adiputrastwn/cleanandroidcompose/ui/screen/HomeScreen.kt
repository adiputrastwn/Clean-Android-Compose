package com.adiputrastwn.cleanandroidcompose.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adiputrastwn.cleanandroidcompose.ui.theme.CleanAndroidComposeTheme

/**
 * Home screen - the main landing page of the app.
 * Uses Navigation 3 to navigate to different features.
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToImageSamples: () -> Unit = {},
) {
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
            onClick = onNavigateToImageSamples,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("View Coil Image Loading Samples")
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
fun HomeScreenPreview() {
    CleanAndroidComposeTheme {
        HomeScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CleanAndroidComposeTheme {
        Greeting("Android")
    }
}