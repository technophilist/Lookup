package com.example.lookup.data.repositories.article

import com.example.lookup.domain.landmarkdetail.LandmarkArticle

/**
 * Repository responsible for fetching articles about landmarks.
 */
interface LandmarkArticleRepository {

    /**
     * Retrieves an article about a specific landmark.
     *
     * @param nameOfLandmark The name of the landmark to retrieve the article for.
     * @param imageUrlOfLandmark The URL of an image representing the landmark.
     * @return A [Result] containing either a [LandmarkArticle] if successful, or an [Exception] if an error occurred.
     */
    suspend fun getArticleAboutLandmark(
        nameOfLandmark: String,
        imageUrlOfLandmark: String
    ): Result<LandmarkArticle>
}