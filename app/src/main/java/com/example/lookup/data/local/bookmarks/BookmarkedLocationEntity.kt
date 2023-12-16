package com.example.lookup.data.local.bookmarks

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "BookmarkedLocations")
data class BookmarkedLocationEntity(
    @PrimaryKey val nameOfLocation: String,
    val imageUrlString: String
)
