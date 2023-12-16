package com.example.lookup.data.repositories.bookmarks

import com.example.lookup.data.local.bookmarks.BookmarkedLocationsDao
import com.example.lookup.domain.bookmarks.BookmarkedLocation
import com.example.lookup.domain.bookmarks.toBookmarkedLocation
import com.example.lookup.domain.bookmarks.toBookmarkedLocationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LookupBookmarksRepository @Inject constructor(
    private val bookmarkedLocationsDao: BookmarkedLocationsDao
) : BookmarksRepository {
    
    override suspend fun getBookmarksRepositoryStream(): Flow<List<BookmarkedLocation>> {
        return bookmarkedLocationsDao.getSavedBookmarksStream().map {
            it.map { bookmarkedLocationEntity -> bookmarkedLocationEntity.toBookmarkedLocation() }
        }
    }

    override suspend fun addLocationToBookmarks(bookmarkedLocation: BookmarkedLocation) {
        bookmarkedLocationsDao.insertBookmarkedLocation(bookmarkedLocation.toBookmarkedLocationEntity())
    }

    override suspend fun deleteLocationFromBookmarks(bookmarkedLocation: BookmarkedLocation) {
        bookmarkedLocationsDao.deleteBookmarkedLocation(bookmarkedLocation.name)
    }
}