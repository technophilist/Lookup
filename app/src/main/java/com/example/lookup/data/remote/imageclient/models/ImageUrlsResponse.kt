package com.example.lookup.data.remote.imageclient.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Represents a response containing image URLs.
 *
 * @property imageUrls List of [ImageUrls] objects containing image URLs.
 */
@JsonClass(generateAdapter = true)
data class ImageUrlsResponse(
    @Json(name = "results") val imageUrls: List<ImageUrls>,
) {
    /**
     * A response object that holds an instance of [UrlBuckets] for an image.
     *
     * @property urlBuckets Contains URLs in different sizes (full, raw, regular, small, thumb).
     */
    @JsonClass(generateAdapter = true)
    data class ImageUrls(@Json(name = "urls") val urlBuckets: UrlBuckets)

    /**
     * A class that holds different urls for different image size buckets.
     *
     * @property full url for the full-size image.
     * @property raw url for the raw image.
     * @property regular url for the regular sized image.
     * @property small url for the small sized image.
     * @property thumb url for the "thumbnail sized" image.
     */
    @JsonClass(generateAdapter = true)
    data class UrlBuckets(
        val full: String,
        val raw: String,
        val regular: String,
        val small: String,
        val thumb: String
    )
}
