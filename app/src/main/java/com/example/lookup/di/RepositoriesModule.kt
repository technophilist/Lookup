package com.example.lookup.di

import com.example.lookup.data.repositories.bookmarks.BookmarksRepository
import com.example.lookup.data.repositories.bookmarks.LookupBookmarksRepository
import com.example.lookup.data.repositories.landmark.LandmarkRepository
import com.example.lookup.data.repositories.landmark.LookupLandmarkRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoriesModule {

    @Binds
    abstract fun bindLandmarkRepository(impl: LookupLandmarkRepository): LandmarkRepository

    @Binds
    abstract fun bindBookmarksRepository(impl: LookupBookmarksRepository): BookmarksRepository
}