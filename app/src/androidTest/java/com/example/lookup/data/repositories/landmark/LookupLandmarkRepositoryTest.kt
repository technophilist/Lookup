package com.example.lookup.data.repositories.landmark

import android.graphics.drawable.BitmapDrawable
import android.view.Surface
import androidx.test.platform.app.InstrumentationRegistry
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.lookup.data.local.classifiers.LandmarksClassifier
import com.example.lookup.data.local.classifiers.TFLiteLandmarkClassifier
import com.example.lookup.di.NetworkModule
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class LookupLandmarkRepositoryTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val testDispatcher = StandardTestDispatcher()
    private val classifier = TFLiteLandmarkClassifier(
        context = context,
        ioDispatcher = testDispatcher,
        classifierConfiguration = LandmarksClassifier.ClassifierConfiguration()
    )
    private val landmarkRecognitionRepository = LookupLandmarkRepository(
        landmarksClassifier = classifier,
        textGeneratorClient = NetworkModule.provideTextGeneratorClient()
    )

    @Test
    fun imageAnalysisTest_GoldenGateBridge_classifiedCorrectlyWithAppropriateDescription() =
        runTest(testDispatcher) {
            val goldenGateBridgeUrl =
                "https://fastly.picsum.photos/id/392/5000/3333.jpg?hmac=vCaGuB6rQAiaofdQHatQL4DHgkyR2l-Ms9GWAL63CBQ"
            val request = ImageRequest.Builder(context)
                .data(goldenGateBridgeUrl)
                .build()

            val result = (context.imageLoader.execute(request) as SuccessResult).drawable
            val bitmap = (result as BitmapDrawable).bitmap
            val description = landmarkRecognitionRepository.getDescriptionAboutLandmark(
                bitmap,
                Surface.ROTATION_90
            ).getOrNull()
            assert(description != null)
            println(description)
        }

    @Test
    fun imageAnalysisTest_invalidRotation_returnsFailure() =
        runTest(testDispatcher) {
            val goldenGateBridgeUrl =
                "https://fastly.picsum.photos/id/392/5000/3333.jpg?hmac=vCaGuB6rQAiaofdQHatQL4DHgkyR2l-Ms9GWAL63CBQ"
            val request = ImageRequest.Builder(context)
                .data(goldenGateBridgeUrl)
                .build()

            val result = (context.imageLoader.execute(request) as SuccessResult).drawable
            val bitmap = (result as BitmapDrawable).bitmap
            val descriptionResult = landmarkRecognitionRepository.getDescriptionAboutLandmark(
                bitmap,
                Int.MIN_VALUE
            )
            assert(descriptionResult.isFailure)
        }

    @Test
    fun faqListAboutIdentifierLandmark_validLandmarkName_listOfQuestionsGeneratedSuccessfully() =
        runTest(testDispatcher) {
            val questionsList = landmarkRecognitionRepository
                .getFAQListAboutLandmark(landmarkName = "Golden Gate Bridge")
                .getOrNull()
            assert(questionsList != null)
            println(questionsList)
        }


}