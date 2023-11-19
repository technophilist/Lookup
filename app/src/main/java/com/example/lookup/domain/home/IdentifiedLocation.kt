package com.example.lookup.domain.home

import com.example.lookup.ui.home.HomeScreen

/**
 * A data class representing an identified location.
 *
 * @property name The name of the location.
 * @property imageUrls A list of URLs to images of the location.
 * @property infoCardsContentList A list of [InfoCardContent] objects containing supplementary
 * information about the location.
 * @property moreInfoSuggestions A list of suggestions used to give an option to the user to request
 * more info about a particular location.
 * @property isBookmarked A boolean indicating whether the location has been bookmarked.
 */
data class IdentifiedLocation(
    val name: String,
    val imageUrls: List<String>,
    val infoCardsContentList: List<InfoCardContent>,
    val moreInfoSuggestions: List<MoreInfoSuggestion>,
    val isBookmarked: Boolean
) {

    /**
     * A data class representing a singular piece of information that will be displayed as a
     * card in the [HomeScreen].
     *
     * @property content The content of the card.
     */
    data class InfoCardContent(val content: String)

    /**
     * A data class representing a suggestion. A suggestion is used to give an option to the user
     * to request more info about a particular [IdentifiedLocation].
     */
    @JvmInline
    value class MoreInfoSuggestion(val suggestion: String)
}