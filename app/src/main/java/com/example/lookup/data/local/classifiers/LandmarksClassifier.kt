package com.example.lookup.data.local.classifiers

import android.graphics.Bitmap

/**
 * An image based landmarks classifier.
 */
interface LandmarksClassifier {
    /**
     * Data class representing a classified landmark
     * @property name The name of the landmark
     * @property score The confidence score of the classification.
     */
    data class LandmarkClassification(val name: String, val score: Float)

    /**
     * Enum representing the possible rotations of an image.
     */
    enum class Rotation { ROTATION_0, ROTATION_90, ROTATION_180, ROTATION_270 }

    /**
     * Classifies the landmark in the [bitmap], taking into consideration the [rotation]
     * of the image and returns an instance of [Result] object containing the list of classified landmarks,
     * or an [Exception] if classification failed
     */
    suspend fun classify(
        bitmap: Bitmap,
        rotation: Rotation
    ): Result<List<LandmarkClassification>>
}
