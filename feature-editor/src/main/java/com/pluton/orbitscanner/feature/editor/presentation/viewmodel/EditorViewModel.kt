package com.pluton.orbitscanner.feature.editor.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pluton.orbitscanner.core.database.dao.DocumentDao
import com.pluton.orbitscanner.feature.editor.presentation.state.EditorActiveTab
import com.pluton.orbitscanner.feature.editor.presentation.state.EditorSubSheet
import com.pluton.orbitscanner.feature.editor.presentation.state.EditorUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val documentDao: DocumentDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    fun initializeDocument(documentId: String) {
        _uiState.update { it.copy(documentId = documentId) }
    }

    fun selectActiveTab(tab: EditorActiveTab) {
        _uiState.update { state ->
            if (state.activeTab == tab) {
                state.copy(activeTab = EditorActiveTab.NONE, activeSubSheet = EditorSubSheet.NONE)
            } else {
                state.copy(activeTab = tab, activeSubSheet = EditorSubSheet.NONE)
            }
        }
    }

    fun selectSubSheet(sheet: EditorSubSheet) {
        _uiState.update { state ->
            if (state.activeSubSheet == sheet) {
                state.copy(activeSubSheet = EditorSubSheet.NONE)
            } else {
                state.copy(activeSubSheet = sheet)
            }
        }
    }

    fun updatePageIndex(index: Int) {
        _uiState.update { it.copy(activePageIndex = index) }
    }

    fun updateFilter(filter: String) {
        _uiState.update { it.copy(activeFilter = filter) }
    }

    fun updateFormat(format: String) {
        _uiState.update { it.copy(activePageFormat = format) }
    }

    fun addTextOverlay(text: String) {
        _uiState.update { it.copy(textOverlayText = text, textOverlayActive = text.isNotEmpty()) }
    }

    fun updateTextSize(size: Float) {
        _uiState.update { it.copy(textOverlaySize = size) }
    }

    fun placeSignature() {
        _uiState.update { it.copy(signaturePlaced = true) }
    }

    fun removeSignature() {
        _uiState.update { it.copy(signaturePlaced = false) }
    }

    fun updateSignatureWeight(weight: Float) {
        _uiState.update { it.copy(signatureWeight = weight) }
    }

    fun updateSignatureOpacity(opacity: Float) {
        _uiState.update { it.copy(signatureOpacity = opacity) }
    }

    fun toggleUploadBackground(isTransparent: Boolean) {
        _uiState.update { it.copy(isUploadTransparent = isTransparent) }
    }

    fun deleteCurrentPage(onDeleted: () -> Unit) {
        _uiState.update { state ->
            val nextTotal = (state.totalPages - 1).coerceAtLeast(1)
            val nextIndex = state.activePageIndex.coerceAtMost(nextTotal)
            state.copy(
                totalPages = nextTotal,
                activePageIndex = nextIndex,
                activeSubSheet = EditorSubSheet.NONE
            )
        }
        onDeleted()
    }
}
