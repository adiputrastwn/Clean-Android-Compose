package com.adiputrastwn.cleanandroidcompose.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoutes {
    /**
     * Home screen route - the main entry point of the app
     */
    @Serializable
    data object HomeRoute : AppRoutes

    /**
     * Coil image loading samples screen route
     */
    @Serializable
    data object ImageLoadingSamplesRoute : AppRoutes

}