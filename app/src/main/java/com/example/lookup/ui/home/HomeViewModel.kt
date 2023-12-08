package com.example.lookup.ui.home

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lookup.data.repositories.landmark.LandmarkRepository
import com.example.lookup.data.repositories.landmark.getAnswerForQueryAboutLandmark
import com.example.lookup.domain.home.ConversationMessage
import com.example.lookup.domain.home.IdentifiedLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val landmarkRepository: LandmarkRepository
) : ViewModel() {

    private val _homeScreenUiState = MutableStateFlow(HomeScreenUiState())
    val homeScreenUiState = _homeScreenUiState.asStateFlow()

    fun analyzeImage(imageProxy: ImageProxy) {
        viewModelScope.launch {
            val rotationDegreesToMakeImageUpright = imageProxy.imageInfo.rotationDegrees
            analyzeBitmap(
                bitmap = imageProxy.toBitmap(),
                rotationDegreesToMakeImageUpright = rotationDegreesToMakeImageUpright
            )
            imageProxy.close()
        }
    }

    private suspend fun analyzeBitmap(bitmap: Bitmap, rotationDegreesToMakeImageUpright: Int) {
        try {
            coroutineScope {
                _homeScreenUiState.update { it.copy(isAnalyzing = true) }
                // fetch name and description
                val (name, description) = landmarkRepository.getNameAndDescriptionOfLandmark(
                    bitmap,
                    rotationDegreesToMakeImageUpright
                ).getOrThrow()
                // fetch some possible questions that the user might have about the landmark
                val moreInfoSuggestions = async {
                    // Note: Errors that happen in this, or any async blocks, will not be thrown in
                    // each respective calls to await() because this/they is/are a child of a
                    // coroutine scope.
                    landmarkRepository.getFAQListAboutLandmark(description)
                        .getOrThrow()
                        .map(IdentifiedLocation::MoreInfoSuggestion)
                }
                // fetch images of the identified location
                val images = async {
                    landmarkRepository.getImageUrlListForLandmark(name).getOrThrow()
                }
                // fetch the assistant description about the landmark & add it to the messages list
                val assistantMessageAboutLandmark = ConversationMessage.AssistantMessage(
                    content = ConversationMessage.AssistantMessage.Content.Immediate(description)
                )
                // create the identified location object based on the fetched information
                val identifiedLocation = IdentifiedLocation(
                    name = name,
                    imageUrls = images.await(),
                    moreInfoSuggestions = moreInfoSuggestions.await(),
                    isBookmarked = false
                )
                _homeScreenUiState.update {
                    it.copy(
                        identifiedLocation = identifiedLocation,
                        isAnalyzing = false,
                        conversationMessages = listOf(assistantMessageAboutLandmark)
                    )
                }
            }
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            _homeScreenUiState.update {
                it.copy(isAnalyzing = false, errorOccurredWhenAnalyzing = true)
            }
        }
    }

    fun onErrorDismissed() {
        _homeScreenUiState.update { it.copy(errorOccurredWhenAnalyzing = false) }
    }

    fun onIdentifiedLocationDismissed() {
        // reset state to prevent inconsistencies
        _homeScreenUiState.update { HomeScreenUiState() }
    }

    // todo: error handling
    // todo: add additional suggestions as suggestions are being removed from list
    fun onQuerySuggestionClick(index: Int) {
        val identifiedLocation = _homeScreenUiState.value.identifiedLocation ?: return
        val clickedSuggestion = identifiedLocation.moreInfoSuggestions.getOrNull(index) ?: return
        val suggestionConversationMessage = ConversationMessage.UserMessage(
            content = clickedSuggestion.suggestion
        )
        viewModelScope.launch {
            // remove suggestion from ui
            removeSuggestionAtIndex(index)
            // add suggestion to list of conversation messages & set loading state to true
            _homeScreenUiState.update {
                it.copy(conversationMessages = it.conversationMessages + suggestionConversationMessage)
            }
            // get answer for selected query
            val answerToQuery = async {
                landmarkRepository.getAnswerForQueryAboutLandmark(
                    landmarkName = identifiedLocation.name,
                    query = clickedSuggestion.suggestion,
                    defaultValue = "Oops! Sorry, I had trouble responding. Please try again."
                )
            }
            val answerToQueryConversationMessage = ConversationMessage.AssistantMessage(
                content = ConversationMessage.AssistantMessage.Content.DeferredContent(answerToQuery)
            )
            // add answer generated for the query to list of conversation message & set loading to false
            _homeScreenUiState.update {
                it.copy(conversationMessages = it.conversationMessages + answerToQueryConversationMessage)
            }
        }
    }

    private fun removeSuggestionAtIndex(index: Int) {
        _homeScreenUiState.update { oldHomeScreenState ->
            if (oldHomeScreenState.identifiedLocation == null) return
            val updatedSuggestions =
                oldHomeScreenState.identifiedLocation.moreInfoSuggestions.toMutableList()
                    .apply { removeAt(index) }
            val updatedIdentifiedLocation =
                oldHomeScreenState.identifiedLocation.copy(moreInfoSuggestions = updatedSuggestions)
            oldHomeScreenState.copy(identifiedLocation = updatedIdentifiedLocation)
        }
    }

}