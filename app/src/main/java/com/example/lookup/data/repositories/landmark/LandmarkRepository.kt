package com.example.lookup.data.repositories.landmark

import android.graphics.Bitmap
import android.view.Surface

/**
 * A repository for identifying and retrieving information about landmarks.
 */
interface LandmarkRepository {
    /**
     * Retrieves a brief description of the landmark in the given [bitmap].
     *
     * @param bitmap The bitmap containing the landmark.
     * @param surfaceRotation One of the rotation constants defined in [Surface].
     * @return A [Result] object containing the description of the landmark.
     */
    suspend fun getDescriptionAboutLandmark(bitmap: Bitmap, surfaceRotation: Int): Result<String>

    /**
     * Retrieves a list of frequently asked questions about a landmark.
     *
     * @param landmarkName The name of the landmark.
     * @return A [Result] object containing the list of frequently asked questions.
     */
    suspend fun getFAQListAboutLandmark(landmarkName: String): Result<List<String>>
}
