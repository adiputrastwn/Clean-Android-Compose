package com.adiputrastwn.cleanandroidcompose

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class CleanAndroidComposeApp : Application() {

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
}