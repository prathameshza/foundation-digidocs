package com.pluton.orbitscanner.feature.scanner.presentation.state

import com.pluton.orbitscanner.feature.scanner.domain.model.ScannedPage

enum class ScannerStep {
    CAPTURE,
    REVIEW,
    ASSEMBLY
}

data class ScannerUiState(
    val step: ScannerStep = ScannerStep.CAPTURE,
    val capturedPages: List<ScannedPage> = emptyList(),
    val activePageIndex: Int = 0,
    val docTitle: String = "2026-06-06 21:12",
    val isSaving: Boolean = false,
    val activeMode: String = "SCAN",
    val isFlashActive: Boolean = false
)
