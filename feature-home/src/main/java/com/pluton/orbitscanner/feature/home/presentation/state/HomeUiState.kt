package com.pluton.orbitscanner.feature.home.presentation.state

import com.pluton.orbitscanner.feature.home.domain.model.HomeItem

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val recentItems: List<HomeItem> = emptyList(),
        val localItems: List<HomeItem> = emptyList(),
        val folderItems: List<HomeItem> = emptyList()
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}