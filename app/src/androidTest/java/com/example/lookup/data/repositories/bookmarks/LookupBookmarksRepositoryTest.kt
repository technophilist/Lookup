package com.example.lookup.data.repositories.bookmarks

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.lookup.data.local.bookmarks.BookmarkedLocationsDatabase
import com.example.lookup.domain.bookmarks.BookmarkedLocation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class LookupBookmarksRepositoryTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private lateinit var database: BookmarkedLocationsDatabase
    private lateinit var lookupBookmarksRepository: LookupBookmarksRepository

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            context,
            BookmarkedLocationsDatabase::class.java
        ).build()
        lookupBookmarksRepository = LookupBookmarksRepository(database.getDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addToBookmarksTest_validBookmark_isInsertedSuccessfully() = runTest {
        val testBookmarkedLocation = BookmarkedLocation(
            name = "Test Location",
            imageUrl = "http://example.com/test_location.jpg"
        )
        lookupBookmarksRepository.addLocationToBookmarks(testBookmarkedLocation)
        assert(
            lookupBookmarksRepository.getBookmarksRepositoryStream()
                .first()
                .first() == testBookmarkedLocation
        )
    }

    @Test
    fun deleteBookmarkTest_validExistingBookmark_isDeletedSuccessfully() = runTest {
        val testBookmarkedLocation = BookmarkedLocation(
            name = "Test Location",
            imageUrl = "http://example.com/test_location.jpg"
        )
        lookupBookmarksRepository.addLocationToBookmarks(testBookmarkedLocation)
        assert(
            lookupBookmarksRepository.getBookmarksRepositoryStream()
                .first()
                .first() == testBookmarkedLocation
        )

        lookupBookmarksRepository.deleteLocationFromBookmarks(testBookmarkedLocation)
        assert(lookupBookmarksRepository.getBookmarksRepositoryStream().first().isEmpty())
    }
}
