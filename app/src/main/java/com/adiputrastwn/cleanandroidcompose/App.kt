package com.adiputrastwn.cleanandroidcompose

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), SingletonImageLoader.Factory {

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber
        if (BuildConfig.DEBUG) {
            // Plant Debug tree for debug builds
            Timber.plant(Timber.DebugTree())
        } else {
            // For production, you can plant a custom tree that logs to a crash reporting service
            // Example: Timber.plant(CrashReportingTree())
        }

        Timber.d("Application initialized")
    }

    /**
     * Factory method for Coil's SingletonImageLoader.
     * Returns the Hilt-injected ImageLoader configured in AppModule.
     *
     * This makes the optimized ImageLoader (with memory cache, disk cache,
     * OkHttp integration, and crossfade) available to all AsyncImage composables
     * throughout the app without explicit injection.
     */
    override fun newImageLoader(context: PlatformContext) = imageLoader
}
