package com.example.lookup.domain.landmarkdetail

import com.example.lookup.data.local.cache.articles.LandmarkArticleEntity

/**
 * Represents a specific variation of an article.
 *
 * @property variationType The type of variation, specifying how the content is presented.
 * @property content The actual text content of the article.
 */
data class ArticleVariation(val variationType: VariationType, val content: String) {
    /**
     * Represents the different types of variations that an article can have.
     *
     * @property label The label that indicates the variation type.
     */
    enum class VariationType(val label: String) {
        /**
         * A concise variation of an article, providing a brief overview of the content.
         */
        CONCISE("Concise"),

        /**
         * A deep dive variation of an article, exploring a topic in greater detail.
         */
        DEEP_DIVE("Deep Dive"),

        /**
         * A factual variation of an article, focusing on objective information and data.
         */
        FACTUAL("Factual (Beta)")
    }
}

/**
 * Used to convert an instance of [LandmarkArticleEntity] to an instance of [ArticleVariation]
 */
fun LandmarkArticleEntity.toArticleVariation(): ArticleVariation {
    return ArticleVariation(
        variationType = this.articleContentType.toArticleVariationType(),
        content = this.content
    )
}

/**
 * Converts an enum of type [LandmarkArticleEntity.ArticleContentType] to an enum of type
 * [ArticleVariation.VariationType].
 */
fun LandmarkArticleEntity.ArticleContentType.toArticleVariationType(): ArticleVariation.VariationType {
    return when (this) {
        LandmarkArticleEntity.ArticleContentType.CONCISE -> ArticleVariation.VariationType.CONCISE
        LandmarkArticleEntity.ArticleContentType.DEEP_DIVE -> ArticleVariation.VariationType.DEEP_DIVE
        LandmarkArticleEntity.ArticleContentType.FACTUAL -> ArticleVariation.VariationType.FACTUAL
    }
}

/**
 * Converts an enum of type [ArticleVariation.VariationType]  to an enum of type
 * [LandmarkArticleEntity.ArticleContentType]
 */
fun ArticleVariation.VariationType.toArticleVariationType(): LandmarkArticleEntity.ArticleContentType {
    return when (this) {
        ArticleVariation.VariationType.CONCISE -> LandmarkArticleEntity.ArticleContentType.CONCISE
        ArticleVariation.VariationType.DEEP_DIVE -> LandmarkArticleEntity.ArticleContentType.DEEP_DIVE
        ArticleVariation.VariationType.FACTUAL -> LandmarkArticleEntity.ArticleContentType.FACTUAL
    }
}

