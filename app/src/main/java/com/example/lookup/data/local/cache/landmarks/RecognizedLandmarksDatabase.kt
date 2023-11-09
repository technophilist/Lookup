package com.example.lookup.data.local.cache.landmarks

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(entities = [RecognizedLandmarkEntity::class], version = 1, exportSchema = false)
@TypeConverters(
    ImageUrlEntityListConverter::class,
    SuggestedQueryForLocationEntityConverter::class
)
abstract class RecognizedLandmarksDatabase : RoomDatabase() {
    abstract fun getDao(): RecognizedLandmarksDao
}

private class ImageUrlEntityListConverter {

    @TypeConverter
    fun imageUrlEntityListToString(imageUrls: List<RecognizedLandmarkEntity.ImageUrlEntity>): String {
        return imageUrls.map { it.url }.joinToString { it }
    }

    @TypeConverter
    fun stringToImageUrlEntityList(string: String): List<RecognizedLandmarkEntity.ImageUrlEntity> {
        return string.split(",").map(RecognizedLandmarkEntity::ImageUrlEntity)
    }
}

private class SuggestedQueryForLocationEntityConverter {

    @TypeConverter
    fun imageUrlEntityListToString(
        suggestedQueriesForLocation: List<RecognizedLandmarkEntity.SuggestedQueryForLocationEntity>
    ): String {
        return suggestedQueriesForLocation.map { it.query }.joinToString { it }
    }

    @TypeConverter
    fun stringToImageUrlEntityList(string: String): List<RecognizedLandmarkEntity.SuggestedQueryForLocationEntity> {
        return string.split(",").map(RecognizedLandmarkEntity::SuggestedQueryForLocationEntity)
    }
}
