package com.example.lookup.domain.bookmarks

import com.example.lookup.data.local.bookmarks.BookmarkedLocationEntity

/**
 * A domain class representing a previously bookmarked location by the user.
 * @property name The name of the location.
 * @property imageUrl A [String] containing the url of the associated image.
 */
data class BookmarkedLocation(val name: String, val imageUrl: String)


/**
 * A mapper function used to map an instance of [BookmarkedLocationEntity] to an instance of
 * [BookmarkedLocation].
 */
fun BookmarkedLocationEntity.toBookmarkedLocation(): BookmarkedLocation = BookmarkedLocation(
    name = nameOfLocation,
    imageUrl = imageUrlString
)

/**
 * A mapper function used to map an instance of [BookmarkedLocation] to an instance of
 * [BookmarkedLocationEntity].
 */
fun BookmarkedLocation.toBookmarkedLocationEntity() = BookmarkedLocationEntity(
    nameOfLocation = name,
    imageUrlString = imageUrl
)


