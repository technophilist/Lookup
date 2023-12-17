package com.example.lookup.ui.navigation

/**
 * A sealed class hierarchy that contains the different destinations of the app.
 * @param route The associated route string of each destination.
 */
sealed class LookupDestinations(val route: String) {
    /**
     * An object representing the home screen.
     */
    object HomeScreen : LookupDestinations("lookup_home_screen")
    object BookmarksScreen : LookupDestinations("lookup_bookmarks_screen")
}