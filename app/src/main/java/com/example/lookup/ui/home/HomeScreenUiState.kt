package com.example.lookup.ui.home

/**
 * A data class that represents the current UI state of the [HomeScreen].
 *  @param identifiedLocation The location identified by the app. A null value indicates
 *  that the app hasn't identified any locations yet.
 *  @param isAnalyzing Indicates whether the current camera stream is being analyzed.
 */
data class HomeScreenUiState(
    val identifiedLocation: IdentifiedLocation? = null,
    val isAnalyzing: Boolean = false,
    val errorOccurredWhenAnalyzing: Boolean = false
)
