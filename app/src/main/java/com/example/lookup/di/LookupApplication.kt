package com.example.lookup.di

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class LookupApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory
    override val workManagerConfiguration: Configuration = Configuration.Builder()
        .setWorkerFactory(hiltWorkerFactory)
        .build()
}