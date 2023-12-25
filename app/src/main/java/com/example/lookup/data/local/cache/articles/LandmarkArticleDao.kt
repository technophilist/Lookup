package com.example.lookup.data.local.cache.articles

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface LandmarkArticleDao {
    @Insert
    suspend fun insertArticles(articleEntities: List<LandmarkArticleEntity>)

    @Query("SELECT * FROM LandmarkArticles where nameOfLocation = :nameOfLocation")
    suspend fun getAllSavedArticlesForLocation(nameOfLocation: String): List<LandmarkArticleEntity>

    @Query("DELETE FROM LandmarkArticles where nameOfLocation = :nameOfLocation")
    suspend fun deleteArticleForLocation(nameOfLocation: String)
}