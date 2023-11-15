package com.example.lookup.data.remote.imageclient

import com.example.lookup.data.remote.imageclient.models.ImageUrlsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageClient {

    @GET(ImageClientConstants.Endpoints.SEARCH_PHOTOS)
    suspend fun getImagesForQuery(
        @Query("query") query: String,
        @Query("per_page") limit: Int = 10
    ): Response<ImageUrlsResponse>

}