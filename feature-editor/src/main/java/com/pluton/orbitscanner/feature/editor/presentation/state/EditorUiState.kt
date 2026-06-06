package com.pluton.orbitscanner.feature.editor.presentation.state

enum class EditorActiveTab {
    NONE,
    OCR,
    EDIT,
    SIGN
}

enum class EditorSubSheet {
    NONE,
    FILTERS,
    CROP_ROTATE,
    FORMAT,
    DELETE,
    ADD_TEXT,
    SIGNATURE_DRAW,
    SIGNATURE_UPLOAD
}

data class EditorUiState(
    val documentId: String = "2026-05-07 10:44",
    val activeTab: EditorActiveTab = EditorActiveTab.NONE,
    val activeSubSheet: EditorSubSheet = EditorSubSheet.NONE,
    val activePageIndex: Int = 1,
    val totalPages: Int = 4,
    val activeFilter: String = "None", // None, Lighten, Document, B&W, Grayscale
    val activePageFormat: String = "A4", // Fit to image, A4, A5, ID Card, Letter, Legal
    val textOverlayText: String = "",
    val textOverlayActive: Boolean = false,
    val textOverlaySize: Float = 14f,
    val textOverlayColor: String = "White",
    val signaturePlaced: Boolean = false,
    val signatureScale: Float = 0.8f,
    val signatureWeight: Float = 5f,
    val signatureOpacity: Float = 62f,
    val isUploadTransparent: Boolean = true
)
