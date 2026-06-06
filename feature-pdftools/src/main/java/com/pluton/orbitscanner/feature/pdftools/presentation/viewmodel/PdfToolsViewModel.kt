package com.pluton.orbitscanner.feature.pdftools.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pluton.orbitscanner.core.database.dao.DocumentDao
import com.pluton.orbitscanner.core.database.entity.DocumentEntity
import com.pluton.orbitscanner.feature.pdftools.domain.model.PdfToolType
import com.pluton.orbitscanner.feature.pdftools.presentation.state.PdfToolsStep
import com.pluton.orbitscanner.feature.pdftools.presentation.state.PdfToolsUiState
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
class PdfToolsViewModel @Inject constructor(
    private val documentDao: DocumentDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(PdfToolsUiState())
    val uiState: StateFlow<PdfToolsUiState> = _uiState.asStateFlow()


    fun initializeTool(toolName: String) {
        val type = when (toolName) {
            "Merge PDF" -> PdfToolType.MERGE
            "eSign PDF" -> PdfToolType.SIGN
            "Protect PDF" -> PdfToolType.PROTECT
            "Image to PDF" -> PdfToolType.IMAGE_TO_PDF
            "Compress PDF" -> PdfToolType.COMPRESS
            "PDF to Image" -> PdfToolType.PDF_TO_IMAGE
            else -> PdfToolType.COMPRESS
        }

        _uiState.update { state ->
            state.copy(
                activeType = type,
                outputFileName = when (type) {
                    PdfToolType.MERGE -> "Merged_Document"
                    PdfToolType.SIGN -> "Service_Agreement_Signed"
                    PdfToolType.PROTECT -> "Project_Proposal_Protected"
                    PdfToolType.IMAGE_TO_PDF -> "Images_2026_06_06"
                    PdfToolType.COMPRESS -> "Project_Proposal_Compressed"
                    PdfToolType.PDF_TO_IMAGE -> "Project_Proposal_Images"
                }
            )
        }
    }

    fun navigateToStep(step: PdfToolsStep) {
        _uiState.update { it.copy(step = step) }
    }

    fun updateCompressLevel(level: String) {
        _uiState.update { it.copy(compressLevel = level) }
    }

    fun updatePassword(p1: String, p2: String) {
        _uiState.update { state ->
            state.copy(
                passwordInput = p1,
                passwordConfirm = p2,
                isPasswordValid = p1.isNotEmpty() && p1 == p2
            )
        }
    }

    fun updateDocTitle(title: String) {
        _uiState.update { it.copy(outputFileName = title) }
    }

    fun updateUnlockPasswordAttempt(attempt: String) {
        _uiState.update { it.copy(unlockedPasswordAttempt = attempt) }
    }

    fun runProcessingSimulation(onCompleted: () -> Unit) {
        _uiState.update { it.copy(step = PdfToolsStep.PROCESSING, progressPercent = 0f) }
        viewModelScope.launch {
            for (i in 1..10) {
                delay(120)
                _uiState.update { it.copy(progressPercent = i * 10f) }
            }
            onCompleted()
        }
    }

    fun addSigner(signer: String) {
        _uiState.update { it.copy(signersList = it.signersList + signer) }
    }

    fun removeFileItem(index: Int) {
        _uiState.update { state ->
            val updated = state.selectedFiles.toMutableList()
            if (index in updated.indices) {
                updated.removeAt(index)
            }
            state.copy(selectedFiles = updated)
        }
    }

    fun saveDocumentOutcome(onCompleted: () -> Unit) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            
            val type = _uiState.value.activeType
            val ext = if (type == PdfToolType.PDF_TO_IMAGE) "zip" else "pdf"
            val formattedName = if (state.outputFileName.endsWith(".$ext")) state.outputFileName else "${state.outputFileName}.$ext"

            val document = DocumentEntity(
                id = UUID.randomUUID().toString(),
                name = formattedName,
                isFolder = false,
                dateModified = "06 Jun 2026 09:25 PM",
                parentId = null,
                size = if (type == PdfToolType.COMPRESS) "2.6 MB" else "4.8 MB",
                pageCount = if (type == PdfToolType.PDF_TO_IMAGE) state.imagePagesCount else 24,
                isLocal = false,
                extension = ext
            )

            // Dynamic insertion directly into DB flow
            documentDao.insertDocument(document)
            delay(800) // Simulates file compilation writes
            _uiState.update { it.copy(isSaving = false) }
            onCompleted()
        }
    }
}
