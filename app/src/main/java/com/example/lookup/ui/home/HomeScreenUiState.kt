package com.example.lookup.ui.home

import com.example.lookup.domain.home.IdentifiedLocation

/**
 * A data class that represents the current UI state of the [HomeScreen].
 *  @param identifiedLocation The location identified by the app. A null value indicates
 *  that the app hasn't identified any locations yet.
 *  @param isAnalyzing Indicates whether the current camera stream is being analyzed.
 *  @param currentlyLoadingSuggestionIndex The index of the currently displayed suggestions
 *  for which the results are being generated.
 */
data class HomeScreenUiState(
    val identifiedLocation: IdentifiedLocation? = null,
    val isAnalyzing: Boolean = false,
    val errorOccurredWhenAnalyzing: Boolean = false,
    val currentlyLoadingSuggestionIndex: Int? = null,
)
