package com.example.lookup.data.remote.imageclient

object ImageClientConstants {
    /**
     * The base URL of the [ImageClient]'s API.
     */
    const val BASE_URL = "https://api.unsplash.com/"

    object Endpoints {
        /**
         * Endpoint used to get a list of images for a specified search query.
         */
        const val SEARCH_PHOTOS = "search/photos"
    }
}