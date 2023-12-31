package com.example.lookup.di

import com.example.lookup.data.repositories.article.LandmarkArticleRepository
import com.example.lookup.data.repositories.article.LookupLandmarkArticleRepository
import com.example.lookup.data.repositories.bookmarks.BookmarksRepository
import com.example.lookup.data.repositories.bookmarks.LookupBookmarksRepository
import com.example.lookup.data.repositories.landmark.LandmarkRepository
import com.example.lookup.data.repositories.landmark.LookupLandmarkRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoriesModule {

    @Binds
    abstract fun bindLandmarkRepository(impl: LookupLandmarkRepository): LandmarkRepository

    @Binds
    abstract fun bindBookmarksRepository(impl: LookupBookmarksRepository): BookmarksRepository

    @Binds
    abstract fun bindLandmarkArticleRepository(impl: LookupLandmarkArticleRepository): LandmarkArticleRepository
}