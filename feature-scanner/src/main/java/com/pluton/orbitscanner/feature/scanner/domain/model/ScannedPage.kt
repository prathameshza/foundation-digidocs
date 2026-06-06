package com.pluton.orbitscanner.feature.scanner.domain.model

data class ScannedPage(
    val id: String,
    val localUri: String,
    val rotationDegrees: Int = 0,
    val cropRectScale: Float = 0.9f
)
