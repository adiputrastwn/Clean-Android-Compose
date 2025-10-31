package com.adiputrastwn.cleanandroidcompose.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes using Kotlin Serialization.
 * These data classes define the navigation destinations in the app.
 *
 * Navigation 3 uses @Serializable to create type-safe routes that
 * replace string-based navigation with compile-time checked routes.
 */

/**
 * Home screen route - the main entry point of the app
 */
@Serializable
object HomeRoute

/**
 * Coil image loading samples screen route
 */
@Serializable
object ImageLoadingSamplesRoute

/**
 * LeakCanary demo screen route
 */
@Serializable
object LeakCanaryDemoRoute