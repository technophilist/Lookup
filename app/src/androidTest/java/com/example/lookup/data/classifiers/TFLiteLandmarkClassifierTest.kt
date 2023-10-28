package com.example.lookup.data.classifiers

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.test.platform.app.InstrumentationRegistry
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.lookup.data.local.classifiers.LandmarksClassifier
import com.example.lookup.data.local.classifiers.TFLiteLandmarkClassifier
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class TFLiteLandmarkClassifierTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val testDispatcher = StandardTestDispatcher()
    private val classifier = TFLiteLandmarkClassifier(
        context = context,
        ioDispatcher = testDispatcher
    )

    @Test
    fun imageAnalysisTest_GoldenGateBridge_classifiedCorrectly() = runTest(testDispatcher) {
        val goldenGateBridgeUrl =
            "https://fastly.picsum.photos/id/392/5000/3333.jpg?hmac=vCaGuB6rQAiaofdQHatQL4DHgkyR2l-Ms9GWAL63CBQ"
        val request = ImageRequest.Builder(context)
            .data(goldenGateBridgeUrl)
            .build()

        val result = (context.imageLoader.execute(request) as SuccessResult).drawable
        // https://stackoverflow.com/questions/62973484/tensorimage-cannot-load-bitmap
        val bitmap = (result as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val classificationName = classifier.classify(
            bitmap,
            LandmarksClassifier.Rotation.ROTATION_90
        ).getOrNull()?.first()?.name

        assert(classificationName != null)
        assert(classificationName == "Golden Gate Bridge")
    }

}