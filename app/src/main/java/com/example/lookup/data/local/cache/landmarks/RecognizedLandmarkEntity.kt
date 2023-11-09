package com.example.lookup.data.local.cache.landmarks

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "RecognizedLandmarks")
data class RecognizedLandmarkEntity(
    @PrimaryKey val name: String,
    val imageUrls: List<ImageUrlEntity>,
    val suggestedQueriesForLocation: List<SuggestedQueryForLocationEntity>,
    val description: String,
    val isBookmarked: Boolean
) {
    @JvmInline
    value class ImageUrlEntity(val url: String)

    @JvmInline
    value class SuggestedQueryForLocationEntity(val query: String)
}

