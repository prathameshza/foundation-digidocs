@file:Suppress("DEPRECATION")

package com.pluton.orbitscanner.feature.home.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pluton.orbitscanner.core.ui.theme.*
import com.pluton.orbitscanner.core.ui.widgets.GridBackground
import com.pluton.orbitscanner.feature.home.domain.model.HomeItem
import com.pluton.orbitscanner.feature.home.presentation.component.DocumentItemCard
import com.pluton.orbitscanner.feature.home.presentation.component.FolderItemCard
import com.pluton.orbitscanner.feature.home.presentation.viewmodel.HomeViewModel

enum class SortType { DATE_MODIFIED, NAME, SIZE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderDetailScreen(
    folderId: String,
    onNavigateBack: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val rawFiles by viewModel.folderItems.collectAsState()
    var viewMode by remember { mutableStateOf(ViewMode.LIST) }
    var sortType by remember { mutableStateOf(SortType.DATE_MODIFIED) }
    var showSortDialog by remember { mutableStateOf(false) }

    LaunchedEffect(folderId) {
        viewModel.loadFolderItems(folderId)
    }

    val sortedFiles = remember(sortType, rawFiles) {
        when (sortType) {
            SortType.NAME -> rawFiles.sortedBy { it.name }
            SortType.DATE_MODIFIED -> rawFiles.sortedByDescending { it.dateModified }
            SortType.SIZE -> rawFiles.sortedBy { if (it is HomeItem.File) it.size else "" }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Folder details", fontWeight = FontWeight.Bold, color = PrimaryText) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TopNavigation)
            )
        },
        containerColor = AppBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            GridBackground()

            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        "${rawFiles.size} items",
                        fontSize = 14.sp,
                        color = MutedText,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { showSortDialog = true }) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort", tint = AccentPurple)
                        }
                        IconButton(onClick = {
                            viewMode = if (viewMode == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST
                        }) {
                            Icon(
                                imageVector = if (viewMode == ViewMode.LIST) Icons.Default.GridView else Icons.Default.List,
                                contentDescription = "Toggle Grid/List",
                                tint = AccentPurple
                            )
                        }
                    }
                }

                if (viewMode == ViewMode.LIST) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(sortedFiles) { file ->
                            when (file) {
                                is HomeItem.Folder -> FolderItemCard(folder = file, isGridView = false, onClick = {}, onActionClick = {})
                                is HomeItem.File -> DocumentItemCard(file = file, isGridView = false, onClick = {}, onActionClick = {})
                            }
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        items(sortedFiles) { file ->
                            when (file) {
                                is HomeItem.Folder -> FolderItemCard(folder = file, isGridView = true, onClick = {}, onActionClick = {})
                                is HomeItem.File -> DocumentItemCard(file = file, isGridView = true, onClick = {}, onActionClick = {})
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSortDialog) {
        ModalBottomSheet(
            onDismissRequest = { showSortDialog = false },
            containerColor = TopNavigation
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                Text(
                    "Sort By",
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                ListItem(
                    headlineContent = { Text("Date Modified", color = PrimaryText) },
                    modifier = Modifier.clickable {
                        sortType = SortType.DATE_MODIFIED
                        showSortDialog = false
                    },
                    colors = ListItemDefaults.colors(containerColor = if (sortType == SortType.DATE_MODIFIED) CardBackground else Color.Transparent)
                )
                ListItem(
                    headlineContent = { Text("Name", color = PrimaryText) },
                    modifier = Modifier.clickable {
                        sortType = SortType.NAME
                        showSortDialog = false
                    },
                    colors = ListItemDefaults.colors(containerColor = if (sortType == SortType.NAME) CardBackground else Color.Transparent)
                )
                ListItem(
                    headlineContent = { Text("Size", color = PrimaryText) },
                    modifier = Modifier.clickable {
                        sortType = SortType.SIZE
                        showSortDialog = false
                    },
                    colors = ListItemDefaults.colors(containerColor = if (sortType == SortType.SIZE) CardBackground else Color.Transparent)
                )
            }
        }
    }
}