package com.example.lookup.domain.utils

import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.lookup.workers.PrefetchArticleWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Enqueues a WorkRequest to prefetch article data for a given location using the
 * [PrefetchArticleWorker].
 *
 * @param nameOfLocation The name of the location for which to prefetch data.
 * @param imageUrlOfLocation The URL of the image representing the location.
 * @return The Operation representing the enqueue request.
 *
 * @see PrefetchArticleWorker
 */
fun WorkManager.enqueuePrefetchArticleWorkerForLocation(
    nameOfLocation: String,
    imageUrlOfLocation: String
): Operation {
    val inputData = Data.Builder()
        .apply {
            putString(PrefetchArticleWorker.INPUT_DATA_NAME_OF_LANDMARK, nameOfLocation)
            putString(PrefetchArticleWorker.INPUT_DATA_IMAGE_URL, imageUrlOfLocation)
        }.build()
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
    val workRequest = OneTimeWorkRequestBuilder<PrefetchArticleWorker>()
        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
        .setConstraints(constraints)
        .setInputData(inputData)
        .build()
    return enqueueUniqueWork(
        getWorkNameForLocation(nameOfLocation),
        ExistingWorkPolicy.KEEP,
        workRequest
    )
}

/**
 * Returns a [Flow] that emits the [WorkInfo] for the prefetch worker associated with the given location.
 *
 * @param nameOfLocation The name of the location for which to retrieve the [WorkInfo].
 * @return A Flow that emits the [WorkInfo] of the associated worker.
 */
fun WorkManager.getPrefetchWorkerInfoForLocationFlow(nameOfLocation: String): Flow<WorkInfo> {
    val uniqueWorkName = getWorkNameForLocation(nameOfLocation)
    return getWorkInfosForUniqueWorkFlow(uniqueWorkName)
        .map { it.first() }
}

/**
 * Used to get the unique work name with a location.
 */
private fun getWorkNameForLocation(nameOfLocation: String) = "worker_for_$nameOfLocation"