package com.example.lookup.ui.landmarkdetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkInfo.State.*
import androidx.work.WorkManager
import com.example.lookup.data.repositories.article.LandmarkArticleRepository
import com.example.lookup.di.LookupApplication
import com.example.lookup.domain.landmarkdetail.ArticleVariation
import com.example.lookup.domain.utils.getPrefetchWorkerInfoForLocationFlow
import com.example.lookup.ui.navigation.LookupDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.cache
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Base64
import javax.inject.Inject

@HiltViewModel
class LandmarkDetailViewModel @Inject constructor(
    private val landmarkArticleRepository: LandmarkArticleRepository,
    savedStateHandle: SavedStateHandle,
    application: Application,
) : AndroidViewModel(application) {

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
        // todo: all locations are intially appearing as already bookmarked
        //    Edit mode is not working wrll and requires long tap egven if already in edit mode. if not, it navigates to detail screen.gi
        _uiState.update { LandmarkDetailScreenUiState.Loading }
        WorkManager
            .getInstance(getApplication<LookupApplication>().applicationContext)
            .getPrefetchWorkerInfoForLocationFlow(nameOfLandmark)
            .onEach {workInfo ->
                val state = workInfo?.state
                if (state == null || state.isFinished) fetchArticleAndUpdateUiState()
            }
            .launchIn(viewModelScope)
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