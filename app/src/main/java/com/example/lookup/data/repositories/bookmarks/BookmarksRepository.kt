package com.example.lookup.data.repositories.bookmarks

import com.example.lookup.domain.bookmarks.BookmarkedLocation
import kotlinx.coroutines.flow.Flow

/**
 *
 * A repository for managing saved bookmarks.
 */
interface BookmarksRepository {
    /**
     * Retrieves a [Flow] of [BookmarkedLocation]s.
     */
    fun getBookmarksRepositoryStream(): Flow<List<BookmarkedLocation>>

    /**
     * Adds a location to the bookmarks.
     *
     * @param bookmarkedLocation The location to be added to the bookmarks.
     */
    suspend fun addLocationToBookmarks(bookmarkedLocation: BookmarkedLocation)

    /**
     * Deletes a location from the bookmarks.
     *
     * @param bookmarkedLocation The location to be deleted from the bookmarks.
     */
    suspend fun deleteLocationFromBookmarks(bookmarkedLocation: BookmarkedLocation)
}
