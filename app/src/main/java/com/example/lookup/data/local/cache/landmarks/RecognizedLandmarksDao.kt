package com.example.lookup.data.local.cache.landmarks

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface RecognizedLandmarksDao {

    @Upsert
    suspend fun insertRecognizedLandmark(recognizedLandmarkEntity: RecognizedLandmarkEntity)

    @Query("SELECT * from RecognizedLandmarks where name = :name")
    suspend fun getRecognizedLandmarkEntityWithName(name: String): RecognizedLandmarkEntity?

}