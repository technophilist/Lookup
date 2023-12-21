package com.example.lookup.data.local.cache.articles

import androidx.room.Entity

/**
 * Entity representing a landmark article stored in the database.
 *
 * @property nameOfLocation The name of the landmark the article is about.
 * @property oneLinerAboutLandmark A brief one-line description of the landmark.
 *  @property imageUrl The URL of an image representing the landmark.
 * @property articleContentType The type of content in the article.
 * @property content The textual content of the article.
 */
@Entity(primaryKeys = ["nameOfLocation", "articleContentType"], tableName = "LandmarkArticles")
data class LandmarkArticleEntity(
    val nameOfLocation: String,
    val oneLinerAboutLandmark: String,
    val imageUrl: String,
    val articleContentType: ArticleContentType,
    val content: String
) {
    enum class ArticleContentType { CONCISE, DEEP_DIVE, FACTUAL }
}
