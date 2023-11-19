package com.example.lookup.domain.home

import androidx.room.TypeConverters
import com.example.lookup.ui.home.HomeScreen

/**
 * A data class representing an identified location.
 *
 * @property name The name of the location.
 * @property imageUrls A list of URLs to images of the location.
 * @property conversationMessages A list of [ConversationMessage]s that contain a list of all
 * conversations between the [ConversationMessage.Role.User] and [ConversationMessage.Role.Assistant].
 * @property moreInfoSuggestions A list of suggestions used to give an option to the user to request
 * more info about a particular location.
 * @property isBookmarked A boolean indicating whether the location has been bookmarked.
 */
data class IdentifiedLocation(
    val name: String,
    val imageUrls: List<String>,
    val conversationMessages: List<ConversationMessage>,
    val moreInfoSuggestions: List<MoreInfoSuggestion>,
    val isBookmarked: Boolean
) {
    /**
     * A data class representing a suggestion. A suggestion is used to give an option to the user
     * to request more info about a particular [IdentifiedLocation].
     */
    @JvmInline
    value class MoreInfoSuggestion(val suggestion: String)
}