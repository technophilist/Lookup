package com.example.lookup.di

import android.content.Context
import androidx.room.Room
import com.example.lookup.data.local.bookmarks.BookmarkedLocationsDao
import com.example.lookup.data.local.bookmarks.BookmarkedLocationsDatabase
import com.example.lookup.data.local.cache.landmarks.RecognizedLandmarksDao
import com.example.lookup.data.local.cache.landmarks.RecognizedLandmarksDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val LANDMARKS_DATABASE_FILE_NAME = "recognized_landmarks.db"
private const val BOOKMARKS_DATABASE_FILE_NAME = "saved_bookmarks.db"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRecognizedLandmarksDao(@ApplicationContext context: Context): RecognizedLandmarksDao {
        return Room.databaseBuilder(
            context,
            RecognizedLandmarksDatabase::class.java,
            LANDMARKS_DATABASE_FILE_NAME
        ).build().getDao()
    }

    @Provides
    @Singleton
    fun provideBookmarkedLocationsDao(@ApplicationContext context: Context): BookmarkedLocationsDao {
        return Room.databaseBuilder(
            context,
            BookmarkedLocationsDatabase::class.java,
            BOOKMARKS_DATABASE_FILE_NAME
        ).build().getDao()
    }
}

