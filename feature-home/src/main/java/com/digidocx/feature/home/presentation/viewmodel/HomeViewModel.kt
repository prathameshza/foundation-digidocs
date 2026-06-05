package com.digidocx.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digidocx.feature.home.domain.model.HomeItem
import com.digidocx.feature.home.domain.repository.HomeRepository
import com.digidocx.feature.home.presentation.state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _isGridView = MutableStateFlow(false)
    val isGridView: StateFlow<Boolean> = _isGridView.asStateFlow()

    private val _folderItems = MutableStateFlow<List<HomeItem>>(emptyList())
    val folderItems: StateFlow<List<HomeItem>> = _folderItems.asStateFlow()

    private val _searchResults = MutableStateFlow<List<HomeItem>>(emptyList())
    val searchResults: StateFlow<List<HomeItem>> = _searchResults.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            combine(
                repository.getRecentItems(),
                repository.getLocalItems()
            ) { recents, locals ->
                HomeUiState.Success(
                    recentItems = recents,
                    localItems = locals
                )
            }.catch { e ->
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown Error occurred")
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun loadFolderItems(folderId: String) {
        viewModelScope.launch {
            repository.getItemsInFolder(folderId)
                .catch { _folderItems.value = emptyList() }
                .collect { items ->
                    _folderItems.value = items
                }
        }
    }

    fun searchItems(query: String, extensionFilter: String?) {
        viewModelScope.launch {
            combine(
                repository.getRecentItems(),
                repository.getLocalItems()
            ) { recents, locals ->
                val allFiles = (recents + locals).filterIsInstance<HomeItem.File>()
                allFiles.filter { file ->
                    val matchesQuery = file.name.contains(query, ignoreCase = true)
                    val matchesExtension = if (extensionFilter != null) {
                        file.name.endsWith(extensionFilter, ignoreCase = true)
                    } else true
                    matchesQuery && matchesExtension
                }
            }.collect { results ->
                _searchResults.value = results
            }
        }
    }

    fun toggleViewMode() {
        _isGridView.value = !_isGridView.value
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            repository.deleteItem(itemId)
        }
    }
}