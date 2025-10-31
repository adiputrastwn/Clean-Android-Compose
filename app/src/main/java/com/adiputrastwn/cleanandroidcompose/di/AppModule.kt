package com.adiputrastwn.cleanandroidcompose.di

import android.content.Context
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import coil3.util.DebugLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okio.Path.Companion.toOkioPath
import javax.inject.Singleton

/**
 * Hilt module for providing application-level dependencies.
 *
 * This module is installed in the SingletonComponent, meaning all provided
 * dependencies will be scoped to the application lifecycle.
 *
 * Example usage:
 * - Add @Provides functions for shared dependencies (e.g., API clients, databases)
 * - Use @Singleton annotation for single instances
 * - Use @ApplicationContext to inject application context
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Example: Provides application context
     * This is already provided by Hilt, but shown here as an example
     */
    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }

    /**
     * Provides OkHttpClient for Coil image loading.
     * Can be customized with interceptors, timeout settings, etc.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    /**
     * Provides ImageLoader for Coil image loading library.
     * Configured with:
     * - Memory cache for fast repeated access
     * - Disk cache for offline access
     * - Crossfade animation for smooth transitions
     * - Debug logging in debug builds
     * - OkHttp for network requests
     */
    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(OkHttpNetworkFetcherFactory(okHttpClient))
            }
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.10)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache").toOkioPath())
                    .maxSizeBytes(50 * 1024 * 1024) // 50 MB
                    .build()
            }
            .crossfade(true)
            .apply {
                if (com.adiputrastwn.cleanandroidcompose.BuildConfig.DEBUG) {
                    logger(DebugLogger())
                }
            }
            .build()
    }
}
