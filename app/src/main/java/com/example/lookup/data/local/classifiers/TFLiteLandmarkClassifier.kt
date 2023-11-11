package com.example.lookup.data.local.classifiers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageAnalysis
import androidx.core.graphics.scale
import com.example.lookup.di.IODispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
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
import javax.inject.Inject

/**
 * A TensorFlow Lite [LandmarksClassifier].
 *
 * @param context an instance of [Context] to be used for initialization.
 * @param classifierConfiguration The configuration parameters for the classifier.
 */
class TFLiteLandmarkClassifier @Inject constructor(
    @ApplicationContext private val context: Context,
    private val classifierConfiguration: LandmarksClassifier.ClassifierConfiguration,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
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
            .setMaxResults(classifierConfiguration.maxResults)
            .setScoreThreshold(classifierConfiguration.classificationInclusionThreshold)
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

        val processedBitmapForAnalysis = getProcessedBitmapForAnalysis(bitmap, rotation)
        val tensorImage = TensorImage.fromBitmap(processedBitmapForAnalysis)
        try {
            val imageProcessor = ImageProcessor.Builder().build()
            val tensorImage = imageProcessor.process(tensorImage)
            val imageProcessingOptions = ImageProcessingOptions.builder()
                .setOrientation(ImageProcessingOptions.Orientation.TOP_LEFT) // http://jpegclub.org/exif_orientation.html
                .build()
            val results = classifier?.classify(tensorImage, imageProcessingOptions)
                ?.flatMap { classifications ->
                    classifications.categories.map { it.toLandMarkClassification() }
                } ?: emptyList()
            return@withContext Result.success(results)
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            // unable to create classifier
            return@withContext Result.failure(exception)
        }
    }

    /**
     * Used to convert an instance of [Category] to an instance of [LandmarksClassifier.LandmarkClassification]
     */
    private fun Category.toLandMarkClassification() = LandmarksClassifier.LandmarkClassification(
        name = this.displayName,
        score = this.score
    )

    // convert the bitmap to a bitmap that can be used by Tensorflow
    // https://stackoverflow.com/questions/62973484/tensorimage-cannot-load-bitmap
    // Also, scale down the image to 321x321.
    // https://tfhub.dev/google/on_device_vision/classifier/landmarks_classifier_north_america_V1/1
    private fun getProcessedBitmapForAnalysis(
        bitmap: Bitmap,
        rotationToMakeBitmapUpright: LandmarksClassifier.Rotation
    ): Bitmap {
        val tensorFlowCompatibleBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            .scale(width = 321, height = 321)
        val rotationToBeApplied = when (rotationToMakeBitmapUpright) {
            LandmarksClassifier.Rotation.ROTATION_0 -> return tensorFlowCompatibleBitmap
            LandmarksClassifier.Rotation.ROTATION_90 -> 90f
            LandmarksClassifier.Rotation.ROTATION_180 -> 180f
            LandmarksClassifier.Rotation.ROTATION_270 -> 270f
        }
        // rotate image to make it "upright"
        return Bitmap.createBitmap(
            tensorFlowCompatibleBitmap,
            0,
            0,
            tensorFlowCompatibleBitmap.width,
            tensorFlowCompatibleBitmap.height,
            Matrix().apply { postRotate(rotationToBeApplied) },
            false
        )
    }

    companion object {
        private const val NORTH_AMERICA_LANDMARKS_CLASSIFIER =
            "landmarks_ondevice_classifier_north_america_V1.tflite"
    }
}