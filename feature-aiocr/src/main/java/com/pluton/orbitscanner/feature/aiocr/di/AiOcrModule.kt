package com.pluton.orbitscanner.feature.aiocr.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AiOcrModule {
    // Scaffold injection hook ready for third-party machine learning APIs or Cloud OCR engines.
}
