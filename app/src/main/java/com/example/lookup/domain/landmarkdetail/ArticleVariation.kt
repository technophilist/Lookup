package com.example.lookup.domain.landmarkdetail

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
