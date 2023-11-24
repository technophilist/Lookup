package com.example.lookup.ui.home

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lookup.data.repositories.landmark.LandmarkRepository
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
        coroutineScope {
            _homeScreenUiState.update { it.copy(isAnalyzing = true) }
            try {
                // fetch name and description
                val (name, description) = landmarkRepository.getNameAndDescriptionOfLandmark(
                    bitmap,
                    rotationDegreesToMakeImageUpright
                ).getOrThrow()
                // fetch some possible questions that the user might have about the landmark
                val moreInfoSuggestions = async {
                    landmarkRepository.getFAQListAboutLandmark(description)
                        .getOrThrow()
                        .map(IdentifiedLocation::MoreInfoSuggestion)
                }
                // fetch images of the identified location
                val images = async {
                    landmarkRepository.getImageUrlListForLandmark(name).getOrThrow()
                }
                // fetch the assistant description about the landmark & add it to the messages list
                val assistantMessageAboutLandmark = ConversationMessage(
                    role = ConversationMessage.Role.Assistant,
                    content = description
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
            } catch (exception: Exception) {
                if (exception is CancellationException) throw exception
                _homeScreenUiState.update {
                    it.copy(isAnalyzing = false, errorOccurredWhenAnalyzing = true)
                }
            }
        }
    }
}