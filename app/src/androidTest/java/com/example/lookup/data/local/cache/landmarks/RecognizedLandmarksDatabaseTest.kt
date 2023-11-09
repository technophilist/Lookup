package com.example.lookup.data.local.cache.landmarks

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class RecognizedLandmarksDatabaseTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var database: RecognizedLandmarksDatabase
    private lateinit var dao: RecognizedLandmarksDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(context, RecognizedLandmarksDatabase::class.java)
            .build()
        dao = database.getDao()
    }

    @Test
    fun entityInsertionTest_validEntity_getsInsertedSuccessfully() = runTest {
        val imageUrls =
            listOf(RecognizedLandmarkEntity.ImageUrlEntity("https://picsum.photos/1920/1080"))

        val suggestedQueries =
            listOf(RecognizedLandmarkEntity.SuggestedQueryForLocationEntity("Best hiking trails near Mystic Falls"))

        val testLandmarkName = "Test Landmark"
        val recognizedLandmarkEntity = RecognizedLandmarkEntity(
            name = testLandmarkName,
            imageUrls = imageUrls,
            suggestedQueriesForLocation = suggestedQueries,
            description = "A picturesque waterfall surrounded by lush greenery.",
            isBookmarked = false
        )
        dao.insertRecognizedLandmark(recognizedLandmarkEntity)
        assert(dao.getRecognizedLandmarkEntityWithName(testLandmarkName) == recognizedLandmarkEntity)
    }

    @Test
    fun getEntityTest_nameOfEntityNotInDatabase_returnsNull() = runTest {
        assert(dao.getRecognizedLandmarkEntityWithName("testLandmarkName") == null)
    }

    @After
    fun tearDown() {
        database.close()
    }
}