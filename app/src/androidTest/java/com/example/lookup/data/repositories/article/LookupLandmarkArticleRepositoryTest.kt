package com.example.lookup.data.repositories.article

import com.example.lookup.di.NetworkModule
import kotlinx.coroutines.test.runTest
import org.junit.Test

class LookupLandmarkArticleRepositoryTest {

    private val landmarkArticleRepository = LookupLandmarkArticleRepository(
        NetworkModule.provideGeminiTextGeneratorClient()
    )

    @Test
    fun getArticleTest_validLandmarkDetails_articleIsSuccessfullyFetched() = runTest {
        val article = landmarkArticleRepository.getArticleAboutLandmark(
            nameOfLandmark = "Washington Monument",
            imageUrlOfLandmark = "https://picsum.photos/1920/1080"
        ).getOrNull()
        assert(article != null)
        println(article)
    }
}