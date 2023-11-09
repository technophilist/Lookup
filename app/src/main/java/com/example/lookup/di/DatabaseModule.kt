package com.example.lookup.di

import android.content.Context
import androidx.room.Room
import com.example.lookup.data.local.cache.landmarks.RecognizedLandmarksDao
import com.example.lookup.data.local.cache.landmarks.RecognizedLandmarksDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

private const val DATABASE_FILE_NAME = "recognized_landmarks.db"

@Module
@InstallIn(ViewModelComponent::class)
object DatabaseModule {

    @Provides
    fun provideRecognizedLandmarksDao(@ApplicationContext context: Context): RecognizedLandmarksDao {
        return Room.databaseBuilder(
            context,
            RecognizedLandmarksDatabase::class.java,
            DATABASE_FILE_NAME
        ).build().getDao()
    }

}

