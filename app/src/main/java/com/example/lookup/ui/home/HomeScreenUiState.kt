package com.example.lookup.ui.home

import com.example.lookup.domain.home.ConversationMessage
import com.example.lookup.domain.home.ConversationMessageV2
import com.example.lookup.domain.home.IdentifiedLocation

/**
 * A data class that represents the current UI state of the [HomeScreen].
 *  @property identifiedLocation The location identified by the app. A null value indicates
 *  that the app hasn't identified any locations yet.
 *  @property conversationMessages A list of [ConversationMessage]s between the user and the
 *  assistant.
 *  @property isAnalyzing Indicates whether the current camera stream is being analyzed.
 */
data class HomeScreenUiState(
    val identifiedLocation: IdentifiedLocation? = null,
    val conversationMessages: List<ConversationMessageV2> = emptyList(),
    val isLoadingResponseForQuery: Boolean = false,
    val isAnalyzing: Boolean = false,
    val errorOccurredWhenAnalyzing: Boolean = false
)
