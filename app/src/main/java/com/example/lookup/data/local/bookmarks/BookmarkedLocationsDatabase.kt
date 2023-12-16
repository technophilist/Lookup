package com.example.lookup.data.local.bookmarks

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BookmarkedLocationEntity::class], version = 1, exportSchema = false)
abstract class BookmarkedLocationsDatabase : RoomDatabase() {

    abstract fun getDao(): BookmarkedLocationsDao
}
