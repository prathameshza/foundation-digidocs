package com.pluton.orbitscanner.feature.scanner.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ScannerModule {
    // Scaffold hook ready for injecting camera/edge-detection engines in upcoming stages.
}
