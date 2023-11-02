package com.example.lookup.di

import com.example.lookup.data.local.classifiers.LandmarksClassifier
import com.example.lookup.data.local.classifiers.TFLiteLandmarkClassifier
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ClassifierModule {

    @Binds
    @Singleton
    abstract fun bindLandmarksClassifier(impl: TFLiteLandmarkClassifier): LandmarksClassifier
}

@Module
@InstallIn(SingletonComponent::class)
object ClassifierConfigurationModule {
    @Provides
    fun provideLandmarksClassifierConfiguration(): LandmarksClassifier.ClassifierConfiguration =
        LandmarksClassifier.ClassifierConfiguration(
            classificationInclusionThreshold = 0.5f,
            maxResults = 1
        )
}