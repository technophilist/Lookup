package com.example.lookup.data.local.cache.articles

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(entities = [LandmarkArticleEntity::class], version = 1, exportSchema = false)
@TypeConverters(ArticleContentTypeConverter::class)
abstract class LandmarkArticleDatabase : RoomDatabase() {

    abstract fun getDao(): LandmarkArticleDao
}

class ArticleContentTypeConverter {

    @TypeConverter
    fun fromArticleContentType(contentType: LandmarkArticleEntity.ArticleContentType): Int {
        return contentType.ordinal
    }

    @TypeConverter
    fun toArticleContentType(ordinal: Int): LandmarkArticleEntity.ArticleContentType {
        return LandmarkArticleEntity.ArticleContentType.values()[ordinal]
    }

}