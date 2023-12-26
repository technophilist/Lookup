package com.example.lookup.ui.landmarkdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lookup.data.repositories.article.LandmarkArticleRepository
import com.example.lookup.domain.landmarkdetail.ArticleVariation
import com.example.lookup.ui.navigation.LookupDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.cache
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Base64
import javax.inject.Inject

@HiltViewModel
class LandmarkDetailViewModel @Inject constructor(
    private val landmarkArticleRepository: LandmarkArticleRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<LandmarkDetailScreenUiState>(LandmarkDetailScreenUiState.Loading)
    val uiState = _uiState as StateFlow<LandmarkDetailScreenUiState>

    private val nameOfLandmark =
        savedStateHandle.get<String>(LookupDestinations.LandmarkDetailScreen.NAV_ARG_NAME_OF_LANDMARK)!!
    private val imageUrlOfLandmark =
        savedStateHandle.get<String>(LookupDestinations.LandmarkDetailScreen.NAV_ARG_ENCODED_IMAGE_URL)
            .run {
                val decodedByteArray = Base64.getUrlDecoder().decode(this)
                String(decodedByteArray)
            }

    init {
        viewModelScope.launch { fetchArticleAndUpdateUiState() }
    }

    fun retryLoadingArticle() {
        viewModelScope.launch { fetchArticleAndUpdateUiState() }
    }

    fun changeArticleVariation(newVariation: ArticleVariation) {
        _uiState.update { currentState ->
            (currentState as? LandmarkDetailScreenUiState.ArticleLoaded)?.let {
                LandmarkDetailScreenUiState.ArticleLoaded(
                    landmarkArticle = it.landmarkArticle,
                    currentlySelectedArticleVariation = newVariation
                )
            } ?: currentState
        }
    }

    private suspend fun fetchArticleAndUpdateUiState() {
        _uiState.update { LandmarkDetailScreenUiState.Loading }
        val article = landmarkArticleRepository.getArticleAboutLandmark(
            nameOfLandmark = nameOfLandmark,
            imageUrlOfLandmark = imageUrlOfLandmark
        ).getOrNull()
        if (article == null) {
            _uiState.update { LandmarkDetailScreenUiState.Error }
            return
        }
        _uiState.update {
            LandmarkDetailScreenUiState.ArticleLoaded(
                landmarkArticle = article,
                currentlySelectedArticleVariation = article.availableArticleVariations.first()
            )
        }
    }

}