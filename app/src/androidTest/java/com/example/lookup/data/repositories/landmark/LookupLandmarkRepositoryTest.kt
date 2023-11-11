package com.example.lookup.data.repositories.landmark

import android.graphics.drawable.BitmapDrawable
import android.view.Surface
import androidx.test.platform.app.InstrumentationRegistry
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.lookup.data.local.cache.landmarks.RecognizedLandmarkEntity
import com.example.lookup.data.local.cache.landmarks.RecognizedLandmarksDao
import com.example.lookup.data.local.classifiers.LandmarksClassifier
import com.example.lookup.data.local.classifiers.TFLiteLandmarkClassifier
import com.example.lookup.di.NetworkModule
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class LookupLandmarkRepositoryTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val testDispatcher = StandardTestDispatcher()
    private val testRecognizedLandmarkDao = TestRecognizedLandmarksDao()
    private val classifier = TFLiteLandmarkClassifier(
        context = context,
        ioDispatcher = testDispatcher,
        classifierConfiguration = LandmarksClassifier.ClassifierConfiguration()
    )
    private val landmarkRecognitionRepository = LookupLandmarkRepository(
        landmarksClassifier = classifier,
        textGeneratorClient = NetworkModule.provideTextGeneratorClient(),
        recognizedLandmarksDao = testRecognizedLandmarkDao
    )

    @Test
    fun imageAnalysisTest_GoldenGateBridge_classifiedCorrectlyWithAppropriateDescription() =
        runTest(context = testDispatcher, timeout = 30.seconds) {
            val goldenGateBridgeUrl =
                "https://fastly.picsum.photos/id/392/5000/3333.jpg?hmac=vCaGuB6rQAiaofdQHatQL4DHgkyR2l-Ms9GWAL63CBQ"
            val request = ImageRequest.Builder(context)
                .data(goldenGateBridgeUrl)
                .build()

            val drawable = (context.imageLoader.execute(request) as SuccessResult).drawable
            val bitmap = (drawable as BitmapDrawable).bitmap
            val result = landmarkRecognitionRepository.getNameAndDescriptionOfLandmark(
                bitmap = bitmap,
                rotationDegrees = 90
            )
            assert(result.isSuccess)
        }

    @Test
    fun imageAnalysisTest_invalidRotation_returnsFailure() =
        runTest(testDispatcher) {
            val goldenGateBridgeUrl =
                "https://fastly.picsum.photos/id/392/5000/3333.jpg?hmac=vCaGuB6rQAiaofdQHatQL4DHgkyR2l-Ms9GWAL63CBQ"
            val request = ImageRequest.Builder(context)
                .data(goldenGateBridgeUrl)
                .build()

            val drawable = (context.imageLoader.execute(request) as SuccessResult).drawable
            val bitmap = (drawable as BitmapDrawable).bitmap
            val descriptionResult = landmarkRecognitionRepository.getNameAndDescriptionOfLandmark(
                bitmap,
                Int.MIN_VALUE
            )
            assert(descriptionResult.isFailure)
        }

    @Test
    fun faqListAboutIdentifierLandmark_validLandmarkName_listOfQuestionsGeneratedSuccessfully() =
        runTest(context = testDispatcher, timeout = 30.seconds) {
            val questionsList = landmarkRecognitionRepository
                .getFAQListAboutLandmark(landmarkName = "Golden Gate Bridge")
                .getOrNull()
            assert(questionsList != null)
            println(questionsList)
        }

    @Test
    fun cacheTest_afterASuccessfulFetch_resultMustBeCached() =
        runTest(context = testDispatcher, timeout = 30.seconds) {
            val goldenGateBridgeUrl =
                "https://fastly.picsum.photos/id/392/5000/3333.jpg?hmac=vCaGuB6rQAiaofdQHatQL4DHgkyR2l-Ms9GWAL63CBQ"
            val request = ImageRequest.Builder(context)
                .data(goldenGateBridgeUrl)
                .build()

            val drawable = (context.imageLoader.execute(request) as SuccessResult).drawable
            val bitmap = (drawable as BitmapDrawable).bitmap
            val result = landmarkRecognitionRepository.getNameAndDescriptionOfLandmark(
                bitmap = bitmap,
                rotationDegrees = 90
            )
            assert(result.isSuccess)
            // after a landmark is recognized, it must be saved in the cache.
            val (name, _) = result.getOrThrow()
            assert(
                testRecognizedLandmarkDao.getRecognizedLandmarkEntityWithName(name) != null
            )
        }


}

private class TestRecognizedLandmarksDao : RecognizedLandmarksDao {
    private val savedLandmarks = mutableListOf<RecognizedLandmarkEntity>()
    override suspend fun insertRecognizedLandmark(recognizedLandmarkEntity: RecognizedLandmarkEntity) {
        savedLandmarks.add(recognizedLandmarkEntity)
    }

    override suspend fun getRecognizedLandmarkEntityWithName(name: String): RecognizedLandmarkEntity? {
        return savedLandmarks.firstOrNull { it.name == name }
    }
}