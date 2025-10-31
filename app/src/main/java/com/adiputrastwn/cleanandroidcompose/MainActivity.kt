package com.adiputrastwn.cleanandroidcompose

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.adiputrastwn.cleanandroidcompose.navigation.HomeRoute
import com.adiputrastwn.cleanandroidcompose.navigation.ImageLoadingSamplesRoute
import com.adiputrastwn.cleanandroidcompose.samples.MemoryLeakDemoActivity
import com.adiputrastwn.cleanandroidcompose.ui.screen.HomeScreen
import com.adiputrastwn.cleanandroidcompose.ui.screen.ImageLoadingSamplesScreen
import com.adiputrastwn.cleanandroidcompose.ui.theme.CleanAndroidComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("MainActivity onCreate started with Navigation 3")
        enableEdgeToEdge()
        setContent {
            CleanAndroidComposeTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        onNavigateToLeakDemo = {
                            startActivity(Intent(this, MemoryLeakDemoActivity::class.java))
                        }
                    )
                }
            }
        }
        Timber.i("MainActivity UI setup completed with Navigation 3")
    }
}

/**
 * Main navigation host using Navigation 3 with type-safe routes.
 * This replaces the manual state management with proper navigation stack.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onNavigateToLeakDemo: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier
    ) {
        // Home screen destination
        composable<HomeRoute> {
            HomeScreen(
                onNavigateToImageSamples = {
                    navController.navigate(ImageLoadingSamplesRoute)
                },
                onNavigateToLeakDemo = onNavigateToLeakDemo
            )
        }

        // Image loading samples destination
        composable<ImageLoadingSamplesRoute> {
            ImageLoadingSamplesScreen()
        }

        // Note: LeakCanaryDemo still uses Activity-based navigation
        // as it's a separate activity for demonstration purposes
    }
}