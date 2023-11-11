package com.example.lookup.data.repositories.landmark

import android.graphics.Bitmap
import com.example.lookup.data.local.cache.landmarks.RecognizedLandmarkEntity
import com.example.lookup.data.local.cache.landmarks.RecognizedLandmarksDao
import com.example.lookup.data.local.classifiers.LandmarksClassifier
import com.example.lookup.data.remote.languagemodels.textgenerator.TextGeneratorClient
import com.example.lookup.data.remote.languagemodels.textgenerator.models.buildTextGenerationPromptBody
import com.example.lookup.data.remote.languagemodels.textgenerator.models.firstResponse
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

// TODO : add cache
class LookupLandmarkRepository @Inject constructor(
    private val landmarksClassifier: LandmarksClassifier,
    private val textGeneratorClient: TextGeneratorClient,
    private val recognizedLandmarksDao: RecognizedLandmarksDao
) : LandmarkRepository {

    override suspend fun getNameAndDescriptionOfLandmark(
        bitmap: Bitmap,
        rotationDegrees: Int
    ): Result<Pair<String, String>> = try {
        // check if a valid value was provided for rotationDegrees
        if (!isValidRotationDegrees(rotationDegrees)) {
            val exceptionMessage = "Please use a valid rotation constant. ie: {0,90,180 or 270}"
            throw IllegalArgumentException(exceptionMessage)
        }
        // get name of landmark
        val nameOfIdentifiedLocation = landmarksClassifier.classify(
            bitmap = bitmap,
            rotation = convertRotationDegreesToLandmarkRotation(rotationDegrees)
        ).getOrThrow().first().name
        // check cache before making network call
        val landmarkEntity = recognizedLandmarksDao
            .getRecognizedLandmarkEntityWithName(nameOfIdentifiedLocation)
        var descriptionOfLandmark = landmarkEntity?.description
        if (descriptionOfLandmark == null) {
            // if not in cache, fetch and save in cache
            descriptionOfLandmark = generateDescription(nameOfIdentifiedLocation).getOrThrow()
            val entityToSaveInCache = RecognizedLandmarkEntity(
                name = nameOfIdentifiedLocation,
                imageUrls = emptyList(), // TODO
                suggestedQueriesForLocation = emptyList(), // TODO
                description = descriptionOfLandmark,
                isBookmarked = false
            )
            recognizedLandmarksDao.insertRecognizedLandmark(entityToSaveInCache)
        }
        Result.success(Pair(nameOfIdentifiedLocation, descriptionOfLandmark))
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        Result.failure(exception)
    }

    override suspend fun getFAQListAboutLandmark(landmarkName: String): Result<List<String>> {
        val promptBody = buildTextGenerationPromptBody(
            systemPrompt = "List out 5 very short questions that a traveller might ask a guide about this place.",
            userPrompt = landmarkName
        )
        val questionsResponse = textGeneratorClient.generateTextForPrompt(promptBody)
        if (!questionsResponse.isSuccessful) {
            val exception = Exception(questionsResponse.errorBody()?.string() ?: "")
            return Result.failure(exception)
        }
        val questionsList = questionsResponse.body()!!.firstResponse.lines().map {
            // remove the numbered bullet points generated by the model
            // Eg "1. Some Text" will be mapped to "Some text"
            it.replace(regex = Regex("[0-9]\\.\\s"), replacement = "")
        }
        return Result.success(questionsList)
    }

    private suspend fun generateDescription(identifiedLocation: String): Result<String> {
        val promptBody = buildTextGenerationPromptBody(
            systemPrompt = "You are a travel guide. Give a very short summary of this landmark.",
            userPrompt = identifiedLocation,
            maxResponseTokens = 100
        )
        val textGenerationResult = textGeneratorClient.generateTextForPrompt(promptBody)
        if (!textGenerationResult.isSuccessful) {
            var errorMessage = "An error occurred when making a request to generate text"
            textGenerationResult.errorBody()?.let { errorMessage = "$errorMessage : $it" }
            val exception = Exception(errorMessage)
            return Result.failure(exception)
        }
        val description = textGenerationResult.body()!!.firstResponse
        return Result.success(description)
    }

    private fun convertRotationDegreesToLandmarkRotation(surfaceRotation: Int): LandmarksClassifier.Rotation {
        return when (surfaceRotation) {
            0 -> LandmarksClassifier.Rotation.ROTATION_0
            90 -> LandmarksClassifier.Rotation.ROTATION_90
            180 -> LandmarksClassifier.Rotation.ROTATION_180
            else -> LandmarksClassifier.Rotation.ROTATION_270
        }
    }

    private fun isValidRotationDegrees(surfaceRotation: Int): Boolean {
        val validRotations = listOf(0, 90, 180, 270)
        return surfaceRotation in validRotations
    }

}