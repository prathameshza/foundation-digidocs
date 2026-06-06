package com.pluton.orbitscanner.feature.pdftools.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object PdfToolsModule {
    // Scaffold injection hook ready for third-party libraries (such as iText/PdfBox integrations) in subsequent sprints.
}
