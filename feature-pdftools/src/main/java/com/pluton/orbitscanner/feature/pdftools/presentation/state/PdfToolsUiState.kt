package com.pluton.orbitscanner.feature.pdftools.presentation.state

import com.pluton.orbitscanner.feature.pdftools.domain.model.PdfToolType

enum class PdfToolsStep {
    CONFIG,
    PROCESSING,
    SUCCESS,
    PREVIEW,
    LOCKED_SHIELD
}

data class PdfToolsUiState(
    val step: PdfToolsStep = PdfToolsStep.CONFIG,
    val activeType: PdfToolType = PdfToolType.COMPRESS,
    val selectedFiles: List<String> = listOf("Project_Proposal.pdf", "Marketing_Report.pdf", "User_Guide.pdf"),
    val passwordInput: String = "",
    val passwordConfirm: String = "",
    val unlockedPasswordAttempt: String = "",
    val compressLevel: String = "Medium",
    val isToggledOn: Boolean = true,
    val isProgressRunning: Boolean = false,
    val progressPercent: Float = 0f,
    val outputFileName: String = "Merged_Document",
    val isSaving: Boolean = false,
    val isPasswordValid: Boolean = false,
    val signersList: List<String> = listOf("John Doe (john.doe@example.com)"),
    val imagePagesCount: Int = 12
)
