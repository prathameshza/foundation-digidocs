package com.digidocx.feature.home.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.digidocx.feature.home.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToFolder: (String) -> Unit,
    onNavigateToTool: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isGridView by viewModel.isGridView.collectAsState()

    HomeContent(
        uiState = uiState,
        isGridView = isGridView,
        onToggleViewMode = { viewModel.toggleViewMode() },
        onNavigateToSearch = onNavigateToSearch,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToFolder = onNavigateToFolder,
        onNavigateToTool = onNavigateToTool,
        onDeleteItem = { itemId -> viewModel.deleteItem(itemId) }
    )
}