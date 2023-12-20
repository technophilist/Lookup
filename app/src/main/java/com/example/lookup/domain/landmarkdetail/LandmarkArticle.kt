package com.example.lookup.domain.landmarkdetail

/**
 * Represents a structured article about a landmark.
 * @property nameOfLandmark The name of the landmark.
 * @property oneLinerAboutLandmark A brief one-line description of the landmark.
 * @property imageUrl The URL of an image representing the landmark.
 * @property availableArticleVariations A list of available variations of the article content.
 */
data class LandmarkArticle(
    val nameOfLandmark: String,
    val oneLinerAboutLandmark: String,
    val imageUrl: String,
    val availableArticleVariations: List<ArticleVariation>,
)
