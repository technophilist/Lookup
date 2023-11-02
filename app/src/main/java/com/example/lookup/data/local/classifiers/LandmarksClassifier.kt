package com.example.lookup.data.local.classifiers

import android.graphics.Bitmap

/**
 * An image based landmarks classifier.
 */
interface LandmarksClassifier {

    /**
     * Classifies the landmark in the [bitmap], taking into consideration the [rotation]
     * of the image and returns an instance of [Result] object containing the list of classified landmarks,
     * or an [Exception] if classification failed
     */
    suspend fun classify(
        bitmap: Bitmap,
        rotation: Rotation
    ): Result<List<LandmarkClassification>>

    /**
     * Data class representing a classified landmark
     * @property name The name of the landmark
     * @property score The confidence score of the classification.
     */
    data class LandmarkClassification(val name: String, val score: Float)

    /**
     * A data class that holds the configuration parameters for a concrete implementation of
     * [LandmarksClassifier].
     *
     * @property classificationInclusionThreshold The minimum confidence score required for a classification
     * to be included in the results. Default value is 0.5f.
     * @property maxResults The maximum number of classifications to return. Default value is 1.
     */
    data class ClassifierConfiguration(
        val classificationInclusionThreshold: Float = 0.5f,
        val maxResults: Int = 1
    )

    /**
     * Enum representing the possible rotations of an image.
     */
    enum class Rotation { ROTATION_0, ROTATION_90, ROTATION_180, ROTATION_270 }

}
