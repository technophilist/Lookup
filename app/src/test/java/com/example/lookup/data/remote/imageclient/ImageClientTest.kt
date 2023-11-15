package com.example.lookup.data.remote.imageclient

import com.example.lookup.di.NetworkModule
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ImageClientTest {
    private val imageClient = NetworkModule.provideImageClient()

    @Test
    fun `Given a valid search query, a list of valid image urls must be returned`() = runTest {
        val searchQuery = "Eiffel Tower"
        val imagesResponse1 = imageClient.getImagesForQuery(query = searchQuery, limit = 10)
        val imagesResponse2 = imageClient.getImagesForQuery(query = "Eiffel tower", limit = 20)
        assert(imagesResponse1.isSuccessful && imagesResponse1.body()?.imageUrls?.size == 10)
        assert(imagesResponse2.isSuccessful && imagesResponse2.body()?.imageUrls?.size == 20)
    }
}