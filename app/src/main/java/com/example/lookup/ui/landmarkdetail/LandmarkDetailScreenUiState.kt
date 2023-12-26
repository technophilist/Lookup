package com.example.lookup.ui.landmarkdetail

import com.example.lookup.domain.landmarkdetail.ArticleVariation
import com.example.lookup.domain.landmarkdetail.LandmarkArticle

/**
 * A sealed hierarchy containing different ui states of the [LandmarkDetailScreen].
 */
sealed interface LandmarkDetailScreenUiState {
    /**
     * Indicates that the article has been loaded successfully.
     * @param landmarkArticle The loaded landmark article.
     * @param currentlySelectedArticleVariation The currently selected variation of the article.
     */
    data class ArticleLoaded(
        val landmarkArticle: LandmarkArticle,
        val currentlySelectedArticleVariation: ArticleVariation
    ) : LandmarkDetailScreenUiState

    /**
     * Indicates that the article is currently loading.
     */
    object Loading : LandmarkDetailScreenUiState

    /**
     * Indicates that an error occurred when loading the article.
     */
    object Error : LandmarkDetailScreenUiState
}
