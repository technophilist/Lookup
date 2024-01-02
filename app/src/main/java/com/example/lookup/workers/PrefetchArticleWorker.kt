package com.example.lookup.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.lookup.data.local.cache.articles.LandmarkArticleDao
import com.example.lookup.data.local.cache.articles.LandmarkArticleEntity
import com.example.lookup.data.repositories.article.LandmarkArticleRepository
import com.example.lookup.domain.landmarkdetail.toArticleVariationType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * A [CoroutineWorker] responsible for pre-fetching articles about a landmark, and saving
 * it to the database.
 *
 * Note: This [CoroutineWorker] uses [LandmarkArticleRepository.getArticleAboutLandmark] to get
 * the article(s) about a specific landmark & saves it to the local database by using the
 * [landmarkArticleDao]. This might cause two database calls to be made if
 * [LandmarkArticleRepository.getArticleAboutLandmark] handles caching internally.
 * This tradeoff has to be made to ensure that the codebase has a strong architecture. If the worker
 * depends on the fact that [LandmarkArticleRepository.getArticleAboutLandmark] always
 * handles caching on its own & just calls that method without making a call to the database, then
 * this expectation might cause this worker to not work properly when an implementation of
 * [LandmarkArticleRepository] doesn't handle caching on its own.
 */
@HiltWorker
class PrefetchArticleWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val landmarkArticleDao: LandmarkArticleDao,
    private val articleRepository: LandmarkArticleRepository
) : CoroutineWorker(appContext = context, params = workerParameters) {
    override suspend fun doWork(): Result {
        val nameOfLandmark =
            inputData.getString(INPUT_DATA_NAME_OF_LANDMARK) ?: return Result.failure()
        val imageUrlOfLandmark =
            inputData.getString(INPUT_DATA_IMAGE_URL) ?: return Result.failure()
        val article = articleRepository
            .getArticleAboutLandmark(nameOfLandmark, imageUrlOfLandmark)
            .getOrNull() ?: return Result.failure()
        // save the generated article
        val landmarkArticleEntities = article.availableArticleVariations.map {
            LandmarkArticleEntity(
                nameOfLocation = nameOfLandmark,
                oneLinerAboutLandmark = article.oneLinerAboutLandmark,
                imageUrl = imageUrlOfLandmark,
                articleContentType = it.variationType.toArticleVariationType(),
                content = it.content
            )
        }
        landmarkArticleDao.insertArticles(landmarkArticleEntities)
        return Result.success()
    }

    companion object {
        const val INPUT_DATA_NAME_OF_LANDMARK = "input_data_name_of_landmark"
        const val INPUT_DATA_IMAGE_URL = "input_data_image_url_of_landmark"
    }
}