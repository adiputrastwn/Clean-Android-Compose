package com.adiputrastwn.cleanandroidcompose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.adiputrastwn.cleanandroidcompose.navigation.AppRoutes
import com.adiputrastwn.cleanandroidcompose.ui.screen.HomeScreen
import com.adiputrastwn.cleanandroidcompose.ui.screen.ImageLoadingSamplesScreen
import com.adiputrastwn.cleanandroidcompose.ui.screen.leakcanary.MemoryLeakDemoScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.HomeRoute,
        modifier = modifier
    ) {
        // Home screen destination
        composable<AppRoutes.HomeRoute> {
            HomeScreen(
                onNavigateToImageSamples = {
                    navController.navigate(AppRoutes.ImageLoadingSamplesRoute)
                },
                onNavigateToLeakDemo = {
                    navController.navigate(AppRoutes.LeakCanaryDemoRoute)
                }
            )
        }

        // Image loading samples destination
        composable<AppRoutes.ImageLoadingSamplesRoute> {
            ImageLoadingSamplesScreen()
        }

        // LeakCanaryDemo screen destination
        composable<AppRoutes.LeakCanaryDemoRoute> {
            MemoryLeakDemoScreen()
        }
    }
}