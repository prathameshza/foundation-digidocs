package com.pluton.orbitscanner.feature.aiocr.presentation.state

import com.pluton.orbitscanner.feature.aiocr.domain.model.AiOcrType

enum class AiOcrStep {
    INTRO,
    SETTINGS,
    PROCESSING,
    SUCCESS,
    DETAIL_PREVIEW
}

data class AiOcrUiState(
    val step: AiOcrStep = AiOcrStep.INTRO,
    val featureType: AiOcrType = AiOcrType.TEXT_OCR,
    val selectedLanguage: String = "English (Default)",
    val selectedFormat: String = "DOCX",
    val pageRangeStart: String = "1",
    val pageRangeEnd: String = "3",
    val isToggledOn1: Boolean = true,
    val isToggledOn2: Boolean = true,
    val isToggledOn3: Boolean = false,
    val progressPercent: Float = 0f,
    val activeTaskIndex: Int = 0,
    val extractedText: String = "",
    val outputFileName: String = "",
    val outputFileSize: String = "",
    val isSaving: Boolean = false,
    val activeDetailTab: String = "After",
    val detailPageIndex: Int = 1
)
