package com.example.lookup.data.repositories.article

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.lookup.data.local.cache.articles.LandmarkArticleDatabase
import com.example.lookup.di.NetworkModule
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class LookupLandmarkArticleRepositoryTest {


    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var landmarkArticleDatabase: LandmarkArticleDatabase
    private lateinit var landmarkArticleRepository: LookupLandmarkArticleRepository

    @Before
    fun setup() {
        landmarkArticleDatabase = Room.inMemoryDatabaseBuilder(
            context = context,
            LandmarkArticleDatabase::class.java
        ).build()
        landmarkArticleRepository = LookupLandmarkArticleRepository(
            textGeneratorClient = NetworkModule.provideGeminiTextGeneratorClient(),
            landmarkArticleDao = landmarkArticleDatabase.getDao()
        )

    }

    @After
    fun tearDown() {
        landmarkArticleDatabase.close()
    }

    @Test
    fun getArticleTest_validLandmarkDetails_articleIsSuccessfullyFetched() =
        runTest(timeout = 20.seconds) {
            val article = landmarkArticleRepository.getArticleAboutLandmark(
                nameOfLandmark = "Washington Monument",
                imageUrlOfLandmark = "https://picsum.photos/1920/1080"
            ).getOrNull()
            assert(article != null)
            println(article)
        }
}