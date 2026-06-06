package com.pluton.orbitscanner.feature.aiocr.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pluton.orbitscanner.core.database.dao.DocumentDao
import com.pluton.orbitscanner.core.database.entity.DocumentEntity
import com.pluton.orbitscanner.feature.aiocr.domain.model.AiOcrType
import com.pluton.orbitscanner.feature.aiocr.presentation.state.AiOcrStep
import com.pluton.orbitscanner.feature.aiocr.presentation.state.AiOcrUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AiOcrViewModel @Inject constructor(
    private val documentDao: DocumentDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiOcrUiState())
    val uiState: StateFlow<AiOcrUiState> = _uiState.asStateFlow()


    fun initializeFeature(toolName: String) {
        val type = when (toolName) {
            "Extract Text (OCR)" -> AiOcrType.TEXT_OCR
            "Layout OCR" -> AiOcrType.LAYOUT_OCR
            "Enhance Doc" -> AiOcrType.ENHANCE_DOC
            "Extract Tables" -> AiOcrType.EXTRACT_TABLES
            else -> AiOcrType.TEXT_OCR
        }
        
        _uiState.update { state ->
            state.copy(
                featureType = type,
                outputFileName = when (type) {
                    AiOcrType.TEXT_OCR -> "Invoice_May_2024_text"
                    AiOcrType.LAYOUT_OCR -> "Invoice_May_2024"
                    AiOcrType.ENHANCE_DOC -> "Scanned_Invoice_Enhanced"
                    AiOcrType.EXTRACT_TABLES -> "Sales_Data_Table"
                },
                selectedFormat = when (type) {
                    AiOcrType.TEXT_OCR -> "Plain Text (.txt)"
                    AiOcrType.LAYOUT_OCR -> "DOCX"
                    AiOcrType.ENHANCE_DOC -> "PDF"
                    AiOcrType.EXTRACT_TABLES -> "Excel (.xlsx)"
                },
                extractedText = """
                    INVOICE
                    #INV-2024-0315
                    
                    ABC CORPORATION
                    123 Business Street
                    New York, NY 10001
                    (555) 123-4567
                    info@abccorp.com
                    
                    Bill To:
                    John Doe
                    456 Client Avenue
                    New York, NY 10002
                    
                    Description       Qty      Unit Price      Amount
                    Website Design     1       $1,200.00      $1,200.00
                    Development       20         $150.00      $3,000.00
                    SEO Optimization   1         $500.00        $500.00
                    
                    Subtotal                               $4,700.00
                    Tax (8.875%)                            $417.63
                    Total                                  $5,117.63
                    
                    Thank you for your business!
                """.trimIndent()
            )
        }
    }

    fun navigateToStep(step: AiOcrStep) {
        _uiState.update { it.copy(step = step) }
    }

    fun updateLanguage(lang: String) {
        _uiState.update { it.copy(selectedLanguage = lang) }
    }

    fun updateFormat(format: String) {
        _uiState.update { it.copy(selectedFormat = format) }
    }

    fun toggleSwitch1(checked: Boolean) {
        _uiState.update { it.copy(isToggledOn1 = checked) }
    }

    fun toggleSwitch2(checked: Boolean) {
        _uiState.update { it.copy(isToggledOn2 = checked) }
    }

    fun toggleSwitch3(checked: Boolean) {
        _uiState.update { it.copy(isToggledOn3 = checked) }
    }

    fun updateDetailTab(tab: String) {
        _uiState.update { it.copy(activeDetailTab = tab) }
    }

    fun updatePageIndex(index: Int) {
        _uiState.update { it.copy(detailPageIndex = index) }
    }

    fun simulateProgress(onCompleted: () -> Unit) {
        _uiState.update { it.copy(step = AiOcrStep.PROCESSING, progressPercent = 0f, activeTaskIndex = 0) }
        viewModelScope.launch {
            for (i in 1..10) {
                delay(120)
                _uiState.update { state ->
                    val nextProgress = i * 10f
                    val nextTaskIndex = when {
                        nextProgress < 30f -> 0
                        nextProgress < 60f -> 1
                        nextProgress < 85f -> 2
                        else -> 3
                    }
                    state.copy(
                        progressPercent = nextProgress,
                        activeTaskIndex = nextTaskIndex
                    )
                }
            }
            onCompleted()
        }
    }

    fun saveOutputDocument(onCompleted: () -> Unit) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            
            val ext = when (_uiState.value.featureType) {
                AiOcrType.TEXT_OCR -> "txt"
                AiOcrType.LAYOUT_OCR -> "docx"
                AiOcrType.ENHANCE_DOC -> "pdf"
                AiOcrType.EXTRACT_TABLES -> "xlsx"
            }
            val formattedName = if (state.outputFileName.endsWith(".$ext")) state.outputFileName else "${state.outputFileName}.$ext"

            val document = DocumentEntity(
                id = UUID.randomUUID().toString(),
                name = formattedName,
                isFolder = false,
                dateModified = "06 Jun 2026 09:15 PM",
                parentId = null,
                size = "1.2 MB",
                pageCount = 3,
                isLocal = false,
                extension = ext
            )

            documentDao.insertDocument(document)
            delay(800) // Simulates file writes
            _uiState.update { it.copy(isSaving = false) }
            onCompleted()
        }
    }
}
