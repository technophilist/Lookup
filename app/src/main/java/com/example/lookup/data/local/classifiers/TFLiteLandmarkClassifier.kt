package com.example.lookup.data.local.classifiers

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

/**
 * A TensorFlow Lite [LandmarksClassifier].
 *
 * @param context an instance of [Context] to be used for initialization.
 * @param classificationInclusionThreshold The minimum score that a classification must have
 * to be included in the results.
 * @param maxResults The maximum number of results to return.
 */
class TFLiteLandmarkClassifier(
    private val context: Context,
    private val classificationInclusionThreshold: Float = 0.5f,
    private val maxResults: Int = 1,
    private val ioDispatcher: CoroutineDispatcher,
) : LandmarksClassifier {

    private var classifier: ImageClassifier? = null

    private fun createClassifier(): ImageClassifier {
        val compatibilityList = CompatibilityList()
        val baseOptions = BaseOptions.builder()
            .setNumThreads(2)
            .run {
                if (compatibilityList.isDelegateSupportedOnThisDevice) this.useGpu()
                else this
            }
            .build()
        val classifierOptions = ImageClassifier.ImageClassifierOptions.builder()
            .setBaseOptions(baseOptions)
            .setMaxResults(maxResults)
            .setScoreThreshold(classificationInclusionThreshold)
            .build()

        return ImageClassifier.createFromFileAndOptions(
            context,
            NORTH_AMERICA_LANDMARKS_CLASSIFIER,
            classifierOptions
        )
    }

    /**
     * Classifies the landmark in the [bitmap], taking into consideration the [rotation]
     * of the image and returns an instance of [Result] object containing the list of classified landmarks,
     * or an [Exception] if classification failed
     */
    override suspend fun classify(
        bitmap: Bitmap,
        rotation: LandmarksClassifier.Rotation
    ): Result<List<LandmarksClassifier.LandmarkClassification>> = withContext(ioDispatcher) {
        if (classifier == null) {
            try {
                classifier = createClassifier()
            } catch (exception: Exception) {
                if (exception is CancellationException) throw exception
                // unable to create classifier
                return@withContext Result.failure(exception)
            }
        }
        val imageProcessor = ImageProcessor.Builder().build()
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))
        val imageProcessingOptions = ImageProcessingOptions.builder()
            .setOrientation(rotation.toOrientation())
            .build()
        val results = classifier?.classify(tensorImage, imageProcessingOptions)
            ?.flatMap { classifications ->
                classifications.categories.map { it.toLandMarkClassification() }
            } ?: emptyList()
        Result.success(results)
    }

    /**
     * Used to convert an instance of [Category] to an instance of [LandmarksClassifier.LandmarkClassification]
     */
    private fun Category.toLandMarkClassification() = LandmarksClassifier.LandmarkClassification(
        name = this.displayName,
        score = this.score
    )

    /**
     * Used to convert [LandmarksClassifier.Rotation] to [ImageProcessingOptions.Orientation],
     * Remember that [ImageProcessingOptions.Orientation] is based off of the EXIF orientation
     * standard - http://jpegclub.org/exif_orientation.html. The default, portrait orientation is
     * 90 degrees (checked using [ImageAnalysis.Analyzer]).
     */
    private fun LandmarksClassifier.Rotation.toOrientation(): ImageProcessingOptions.Orientation {
        return when (this) {
            LandmarksClassifier.Rotation.ROTATION_0 -> ImageProcessingOptions.Orientation.LEFT_BOTTOM
            LandmarksClassifier.Rotation.ROTATION_90 -> ImageProcessingOptions.Orientation.TOP_LEFT
            LandmarksClassifier.Rotation.ROTATION_180 -> ImageProcessingOptions.Orientation.RIGHT_TOP
            LandmarksClassifier.Rotation.ROTATION_270 -> ImageProcessingOptions.Orientation.BOTTOM_RIGHT
        }
    }

    companion object {
        private const val NORTH_AMERICA_LANDMARKS_CLASSIFIER =
            "landmarks_ondevice_classifier_north_america_V1.tflite"
    }
}