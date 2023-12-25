package com.example.lookup.data.repositories.article

import com.example.lookup.data.local.cache.articles.LandmarkArticleDao
import com.example.lookup.data.local.cache.articles.LandmarkArticleEntity
import com.example.lookup.data.remote.languagemodels.textgenerator.TextGeneratorClient
import com.example.lookup.data.remote.languagemodels.textgenerator.models.buildTextGenerationPromptBody
import com.example.lookup.data.remote.languagemodels.textgenerator.models.firstResponse
import com.example.lookup.data.utils.getBodyOrThrowException
import com.example.lookup.di.GeminiClient
import com.example.lookup.domain.landmarkdetail.ArticleVariation
import com.example.lookup.domain.landmarkdetail.LandmarkArticle
import com.example.lookup.domain.landmarkdetail.toArticleVariation
import com.example.lookup.domain.landmarkdetail.toArticleVariationType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LookupLandmarkArticleRepository @Inject constructor(
    @GeminiClient private val textGeneratorClient: TextGeneratorClient,
    private val landmarkArticleDao: LandmarkArticleDao
) : LandmarkArticleRepository {
    override suspend fun getArticleAboutLandmark(
        nameOfLandmark: String,
        imageUrlOfLandmark: String,
    ): Result<LandmarkArticle> {
        // Check cache, if article exists in cache, return the cached result.
        val landmarkArticleFromCache = getLandmarkArticleFromCacheIfExists(nameOfLandmark)
        if (landmarkArticleFromCache != null) return Result.success(landmarkArticleFromCache)
        // If not in cache, generate article and add it to cache
        return try {
            val article = generateArticleAboutLandmark(
                nameOfLandmark = nameOfLandmark,
                imageUrlOfLandmark = imageUrlOfLandmark
            ).getOrThrow()
            withContext(NonCancellable) {
                // cache the generated article
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
                return@withContext Result.success(article)
            }
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure(exception)
        }
    }

    private suspend fun getLandmarkArticleFromCacheIfExists(nameOfLandmark: String): LandmarkArticle? {
        val savedArticlesForLocation =
            landmarkArticleDao.getAllSavedArticlesForLocation(nameOfLandmark)
        val (nameOfLocation, oneLinerAboutLandmark, imageUrl) = savedArticlesForLocation
            .firstOrNull() ?: return null
        val articleVariations = savedArticlesForLocation.map { it.toArticleVariation() }
        return LandmarkArticle(
            nameOfLandmark = nameOfLocation,
            oneLinerAboutLandmark = oneLinerAboutLandmark,
            imageUrl = imageUrl,
            availableArticleVariations = articleVariations
        )
    }

    private suspend fun generateArticleAboutLandmark(
        nameOfLandmark: String,
        imageUrlOfLandmark: String,
    ): Result<LandmarkArticle> {
        val oneLinerPrompt = buildTextGenerationPromptBody(
            systemPrompt = "Generate a poetic one-liner for the subtitle of article about the following location",
            userPrompt = nameOfLandmark,
        )
        return try {
            coroutineScope {
                val oneLinerAboutLandmark =
                    async { textGeneratorClient.generateTextForPrompt(oneLinerPrompt) }
                val article = LandmarkArticle(
                    nameOfLandmark = nameOfLandmark,
                    oneLinerAboutLandmark = oneLinerAboutLandmark.await()
                        .getBodyOrThrowException("An error occurred when generating response.")
                        .firstResponse,
                    imageUrl = imageUrlOfLandmark,
                    availableArticleVariations = getArticleVariationsList(nameOfLandmark).getOrThrow()
                )
                return@coroutineScope Result.success(article)
            }
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure(exception)
        }
    }

    private suspend fun getArticleVariationsList(nameOfLandmark: String): Result<List<ArticleVariation>> {
        val promptForConciseArticle = buildTextGenerationPromptBody(
            systemPrompt = "Generate a concise, two paragraph article about the following.",
            userPrompt = nameOfLandmark
        )

        val promptForDeepDiveArticle = buildTextGenerationPromptBody(
            systemPrompt = "Generate a few very detailed paragraphs about the following place.",
            userPrompt = nameOfLandmark
        )

        val promptForFactualArticle = buildTextGenerationPromptBody(
            systemPrompt = "Generate a few, very factual paragraphs about the following place",
            userPrompt = nameOfLandmark
        )
        return try {
            coroutineScope {
                val concise = async {
                    textGeneratorClient.generateTextForPrompt(promptForConciseArticle)
                        .getBodyOrThrowException("An error occurred when generating response.")
                }
                val deepDive = async {
                    textGeneratorClient.generateTextForPrompt(promptForDeepDiveArticle)
                        .getBodyOrThrowException("An error occurred when generating response.")
                }
                val factual = async {
                    textGeneratorClient.generateTextForPrompt(promptForFactualArticle)
                        .getBodyOrThrowException("An error occurred when generating response.")
                }
                val resultantList = listOf(
                    ArticleVariation(
                        variationType = ArticleVariation.VariationType.CONCISE,
                        content = concise.await().firstResponse
                    ),
                    ArticleVariation(
                        variationType = ArticleVariation.VariationType.DEEP_DIVE,
                        content = deepDive.await().firstResponse
                    ),
                    ArticleVariation(
                        variationType = ArticleVariation.VariationType.FACTUAL,
                        content = factual.await().firstResponse
                    )
                )
                return@coroutineScope Result.success(resultantList)
            }
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            Result.failure(exception)
        }
    }

}