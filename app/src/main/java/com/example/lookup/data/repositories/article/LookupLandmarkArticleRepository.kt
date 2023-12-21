package com.example.lookup.data.repositories.article

import com.example.lookup.data.remote.languagemodels.textgenerator.TextGeneratorClient
import com.example.lookup.data.remote.languagemodels.textgenerator.models.buildTextGenerationPromptBody
import com.example.lookup.data.remote.languagemodels.textgenerator.models.firstResponse
import com.example.lookup.data.utils.getBodyOrThrowException
import com.example.lookup.domain.landmarkdetail.ArticleVariation
import com.example.lookup.domain.landmarkdetail.LandmarkArticle
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

// todo: add cache
class LookupLandmarkArticleRepository @Inject constructor(
    private val textGeneratorClient: TextGeneratorClient
) : LandmarkArticleRepository {
    
    override suspend fun getArticleAboutLandmark(
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