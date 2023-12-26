package com.example.lookup.ui.navigation

import java.util.Base64

/**
 * A sealed class hierarchy that contains the different destinations of the app.
 * @param route The associated route string of each destination.
 */
sealed class LookupDestinations(val route: String) {
    /**
     * An object representing the home screen.
     */
    object HomeScreen : LookupDestinations("lookup_home_screen")

    /**
     * An object representing the bookmarks screen.
     */
    object BookmarksScreen : LookupDestinations("lookup_bookmarks_screen")

    /**
     * An object representing the landmark details screen.
     */
    object LandmarkDetailScreen :
        LookupDestinations("lookup_landmark_details_screen/{nameOfLandmark}/{encodedImageUrl}") {

        const val NAV_ARG_NAME_OF_LANDMARK = "nameOfLandmark"
        const val NAV_ARG_ENCODED_IMAGE_URL = "encodedImageUrl"
        fun buildRoute(nameOfLandmark: String, imageUrl: String): String {
            val encodedImageUrl = Base64.getUrlEncoder().encodeToString(imageUrl.toByteArray())
            return "lookup_landmark_details_screen/$nameOfLandmark/$encodedImageUrl"
        }
    }
}