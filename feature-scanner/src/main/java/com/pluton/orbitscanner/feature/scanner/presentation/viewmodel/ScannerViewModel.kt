package com.pluton.orbitscanner.feature.scanner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pluton.orbitscanner.core.database.dao.DocumentDao
import com.pluton.orbitscanner.core.database.entity.DocumentEntity
import com.pluton.orbitscanner.feature.scanner.domain.model.ScannedPage
import com.pluton.orbitscanner.feature.scanner.presentation.state.ScannerStep
import com.pluton.orbitscanner.feature.scanner.presentation.state.ScannerUiState
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
class ScannerViewModel @Inject constructor(
    private val documentDao: DocumentDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    fun toggleFlash() {
        _uiState.update { it.copy(isFlashActive = !it.isFlashActive) }
    }

    fun updateMode(mode: String) {
        _uiState.update { it.copy(activeMode = mode) }
    }

    fun triggerShutterClick() {
        val newPage = ScannedPage(
            id = UUID.randomUUID().toString(),
            localUri = "mock_captured_image_${_uiState.value.capturedPages.size + 1}"
        )
        _uiState.update { state ->
            val updatedList = state.capturedPages + newPage
            state.copy(
                capturedPages = updatedList,
                activePageIndex = updatedList.size - 1
            )
        }
    }

    fun navigateToStep(step: ScannerStep) {
        _uiState.update { it.copy(step = step) }
    }

    fun rotateActivePage() {
        _uiState.update { state ->
            val activeIndex = state.activePageIndex
            if (activeIndex in state.capturedPages.indices) {
                val current = state.capturedPages[activeIndex]
                val updatedPages = state.capturedPages.toMutableList()
                updatedPages[activeIndex] = current.copy(
                    rotationDegrees = (current.rotationDegrees + 90) % 360
                )
                state.copy(capturedPages = updatedPages)
            } else state
        }
    }

    fun retakeActivePage() {
        _uiState.update { state ->
            val activeIndex = state.activePageIndex
            if (activeIndex in state.capturedPages.indices) {
                val updatedPages = state.capturedPages.toMutableList()
                updatedPages.removeAt(activeIndex)
                val newActiveIndex = if (updatedPages.isNotEmpty()) {
                    (activeIndex - 1).coerceAtLeast(0)
                } else 0
                state.copy(
                    capturedPages = updatedPages,
                    activePageIndex = newActiveIndex,
                    step = if (updatedPages.isEmpty()) ScannerStep.CAPTURE else state.step
                )
            } else state
        }
    }

    fun fitActivePage() {
        _uiState.update { state ->
            val activeIndex = state.activePageIndex
            if (activeIndex in state.capturedPages.indices) {
                val current = state.capturedPages[activeIndex]
                val updatedPages = state.capturedPages.toMutableList()
                updatedPages[activeIndex] = current.copy(
                    cropRectScale = if (current.cropRectScale == 0.9f) 1.0f else 0.9f
                )
                state.copy(capturedPages = updatedPages)
            } else state
        }
    }

    fun selectActivePage(index: Int) {
        if (index in _uiState.value.capturedPages.indices) {
            _uiState.update { it.copy(activePageIndex = index) }
        }
    }

    fun updateDocTitle(title: String) {
        _uiState.update { it.copy(docTitle = title) }
    }

    fun saveScannedDocument(onCompleted: () -> Unit) {
        val state = _uiState.value
        if (state.capturedPages.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            
            // Format name as requested (defaults to PDF extension)
            val extension = "pdf"
            val fullName = if (state.docTitle.endsWith(".$extension")) state.docTitle else "${state.docTitle}.$extension"

            val simulatedDocument = DocumentEntity(
                id = UUID.randomUUID().toString(),
                name = fullName,
                isFolder = false,
                dateModified = "06 Jun 2026 09:12 PM",
                parentId = null,
                size = "${(state.capturedPages.size * 0.4).coerceAtLeast(0.3)} MB",
                pageCount = state.capturedPages.size,
                isLocal = false,
                extension = extension
            )

            // Dynamic insertion directly into DB flow
            documentDao.insertDocument(simulatedDocument)
            delay(800) // Aesthetic saving delay
            _uiState.update { it.copy(isSaving = false) }
            onCompleted()
        }
    }
}
