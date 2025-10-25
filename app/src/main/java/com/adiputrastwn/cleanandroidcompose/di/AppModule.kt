package com.adiputrastwn.cleanandroidcompose.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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

    // Add more providers here as needed
    // Example:
    // @Provides
    // @Singleton
    // fun provideRetrofit(): Retrofit {
    //     return Retrofit.Builder()
    //         .baseUrl("https://api.example.com/")
    //         .addConverterFactory(GsonConverterFactory.create())
    //         .build()
    // }
}
