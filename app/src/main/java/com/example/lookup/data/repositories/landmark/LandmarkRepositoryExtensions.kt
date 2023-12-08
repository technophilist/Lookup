package com.example.lookup.data.repositories.landmark

/**
 * An overload of [LandmarkRepository.getAnswerForQueryAboutLandmark] that returns a [defaultValue]
 * if [LandmarkRepository.getAnswerForQueryAboutLandmark] returns an instance of [Result.failure].
 */
suspend fun LandmarkRepository.getAnswerForQueryAboutLandmark(
    landmarkName: String,
    query: String,
    defaultValue: String
): String = getAnswerForQueryAboutLandmark(
    landmarkName = landmarkName,
    query = query
).getOrNull() ?: defaultValue
