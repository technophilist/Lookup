package com.example.lookup.data.local.bookmarks

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.lookup.domain.bookmarks.BookmarkedLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkedLocationsDao {

    @Insert
    suspend fun insertBookmarkedLocation(bookmarkedLocationEntity: BookmarkedLocationEntity)

    @Query("DELETE FROM BookmarkedLocations WHERE nameOfLocation = :locationName")
    suspend fun deleteBookmarkedLocation(locationName: String)

    fun getSavedBookmarksStream(): Flow<List<BookmarkedLocationEntity>>
}