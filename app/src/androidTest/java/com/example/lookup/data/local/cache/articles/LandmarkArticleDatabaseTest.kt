package com.example.lookup.data.local.cache.articles

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class LandmarkArticleDatabaseTest {

    private lateinit var database: LandmarkArticleDatabase
    private lateinit var dao: LandmarkArticleDao
    private val context = InstrumentationRegistry.getInstrumentation().targetContext


    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            context = context,
            LandmarkArticleDatabase::class.java
        ).build()
        dao = database.getDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertArticleTest_validArticle_isInsertedSuccessfully() = runTest {
        val articleEntity = LandmarkArticleEntity(
            nameOfLocation = "Eiffel Tower",
            articleContentType = LandmarkArticleEntity.ArticleContentType.CONCISE,
            content = "The Eiffel Tower is a wrought-iron lattice tower on the Champ de Mars in Paris, France."
        )

        dao.insertArticle(articleEntity)

        val allArticles = dao.getAllSavedArticles()
        assert(allArticles.size == 1)
        assert(articleEntity == allArticles[0])
    }

    @Test
    fun deleteArticleForLocationTest_validExistingArticle_isDeletedSuccessfully() = runTest {
        val articleEntity1 = LandmarkArticleEntity(
            nameOfLocation = "Eiffel Tower",
            articleContentType = LandmarkArticleEntity.ArticleContentType.CONCISE,
            content = "..."
        )
        val articleEntity1DifferentVariant = LandmarkArticleEntity(
            nameOfLocation = "Eiffel Tower",
            articleContentType = LandmarkArticleEntity.ArticleContentType.FACTUAL,
            content = "..."
        )
        val articleEntity2 = LandmarkArticleEntity(
            nameOfLocation = "Louvre Museum",
            articleContentType = LandmarkArticleEntity.ArticleContentType.DEEP_DIVE,
            content = "..."
        )
        dao.insertArticle(articleEntity1)
        dao.insertArticle(articleEntity1DifferentVariant)
        dao.insertArticle(articleEntity2)

        dao.deleteArticleForLocation("Eiffel Tower")

        val remainingArticles = dao.getAllSavedArticles()
        assert(remainingArticles.none { it.nameOfLocation == "Eiffel Tower" })
    }


}