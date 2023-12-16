package com.example.lookup.domain.bookmarks

/**
 * A domain class representing a previously bookmarked location by the user.
 * @property name The name of the location.
 * @property imageUrl A [String] containing the url of the associated image.
 */
data class BookmarkedLocation(val name: String, val imageUrl: String)
