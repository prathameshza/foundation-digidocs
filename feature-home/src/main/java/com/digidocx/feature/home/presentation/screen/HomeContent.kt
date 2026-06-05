package com.digidocx.feature.home.presentation.screen

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digidocx.core.ui.theme.*
import com.digidocx.core.ui.widgets.GridBackground
import com.digidocx.feature.home.domain.model.HomeItem
import com.digidocx.feature.home.presentation.component.DocumentItemCard
import com.digidocx.feature.home.presentation.component.FolderItemCard
import com.digidocx.feature.home.presentation.state.HomeUiState

enum class HomeTab { MY_FILES, TOOLS }
enum class FileSubTab { RECENTS, LOCAL }
enum class ViewMode { LIST, GRID }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    uiState: HomeUiState,
    isGridView: Boolean,
    onToggleViewMode: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToFolder: (String) -> Unit,
    onNavigateToTool: (String) -> Unit,
    onDeleteItem: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(HomeTab.MY_FILES) }
    var selectedSubTab by remember { mutableStateOf(FileSubTab.RECENTS) }
    var fabExpanded by remember { mutableStateOf(false) }
    var activeActionFile by remember { mutableStateOf<HomeItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DigiDocX", fontWeight = FontWeight.Bold, color = PrimaryText) },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 12.dp, end = 8.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AccentPurple)
                            .clickable { onNavigateToProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("VS", color = AppBackground, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = PrimaryText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TopNavigation)
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                AnimatedVisibility(
                    visible = fabExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = TopNavigation),
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .border(1.dp, BorderPurple, RoundedCornerShape(24.dp))
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            IconButton(onClick = {
                                fabExpanded = false
                                onNavigateToTool("Camera Scan")
                            }) {
                                Icon(Icons.Default.CameraAlt, contentDescription = "Scan", tint = AccentPurple)
                            }
                            IconButton(onClick = {
                                fabExpanded = false
                                Toast.makeText(context, "Gallery Import selected", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = AccentPurple)
                            }
                            IconButton(onClick = {
                                fabExpanded = false
                                Toast.makeText(context, "Cloud Upload selected", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.UploadFile, contentDescription = "Upload", tint = AccentPurple)
                            }
                        }
                    }
                }
                FloatingActionButton(
                    onClick = { fabExpanded = !fabExpanded },
                    containerColor = AccentPurple,
                    contentColor = AppBackground,
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = if (fabExpanded) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "Expand Dock"
                    )
                }
            }
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
                HomeHeaderToggles(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                when (selectedTab) {
                    HomeTab.MY_FILES -> {
                        when (uiState) {
                            is HomeUiState.Loading -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = AccentPurple)
                                }
                            }
                            is HomeUiState.Error -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(uiState.message, color = Color.Red, modifier = Modifier.padding(16.dp))
                                }
                            }
                            is HomeUiState.Success -> {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                            Text(
                                                text = "Recents",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = if (selectedSubTab == FileSubTab.RECENTS) PrimaryText else MutedText,
                                                modifier = Modifier.clickable { selectedSubTab = FileSubTab.RECENTS }
                                            )
                                            Text(
                                                text = "Local",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = if (selectedSubTab == FileSubTab.LOCAL) PrimaryText else MutedText,
                                                modifier = Modifier.clickable { selectedSubTab = FileSubTab.LOCAL }
                                            )
                                        }
                                        IconButton(onClick = onToggleViewMode) {
                                            Icon(
                                                imageVector = if (isGridView) Icons.Default.List else Icons.Default.GridView,
                                                contentDescription = "Toggle View",
                                                tint = AccentPurple
                                            )
                                        }
                                    }

                                    val activeItems = if (selectedSubTab == FileSubTab.RECENTS) uiState.recentItems else uiState.localItems

                                    if (isGridView) {
                                        LazyVerticalGrid(
                                            columns = GridCells.Fixed(2),
                                            modifier = Modifier.fillMaxSize(),
                                            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 80.dp)
                                        ) {

                                            items(activeItems) { item ->
                                                when (item) {
                                                    is HomeItem.Folder -> FolderItemCard(
                                                        folder = item,
                                                        isGridView = true,
                                                        onClick = { onNavigateToFolder(item.id) },
                                                        onActionClick = { activeActionFile = item }
                                                    )
                                                    is HomeItem.File -> DocumentItemCard(
                                                        file = item,
                                                        isGridView = true,
                                                        onClick = {},
                                                        onActionClick = { activeActionFile = item }
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        LazyColumn(
                                            modifier = Modifier.fillMaxSize(),
                                            contentPadding = PaddingValues(bottom = 80.dp)
                                        ) {
                                            items(activeItems) { item ->
                                                when (item) {
                                                    is HomeItem.Folder -> FolderItemCard(
                                                        folder = item,
                                                        isGridView = false,
                                                        onClick = { onNavigateToFolder(item.id) },
                                                        onActionClick = { activeActionFile = item }
                                                    )
                                                    is HomeItem.File -> DocumentItemCard(
                                                        file = item,
                                                        isGridView = false,
                                                        onClick = {},
                                                        onActionClick = { activeActionFile = item }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    HomeTab.TOOLS -> {
                        ToolsLanding(onNavigateToTool = onNavigateToTool)
                    }
                }
            }
        }
    }

    activeActionFile?.let { item ->
        val fileMapped = remember(item) {
            com.digidocx.core.model.DocumentItem(
                id = item.id,
                name = item.name,
                type = if (item is HomeItem.Folder) com.digidocx.core.model.DocumentType.FOLDER else com.digidocx.core.model.DocumentType.PDF,
                dateModified = item.dateModified
            )
        }
        FileOptionsBottomSheet(
            file = fileMapped,
            onDismiss = { activeActionFile = null }
        )
    }
}

@Composable
fun HomeHeaderToggles(selectedTab: HomeTab, onTabSelected: (HomeTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TopNavigation)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(CardBackground)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (selectedTab == HomeTab.MY_FILES) AccentPurple else Color.Transparent)
                    .clickable { onTabSelected(HomeTab.MY_FILES) }
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Text(
                    "My Files",
                    color = if (selectedTab == HomeTab.MY_FILES) AppBackground else MutedText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (selectedTab == HomeTab.TOOLS) AccentPurple else Color.Transparent)
                    .clickable { onTabSelected(HomeTab.TOOLS) }
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Text(
                    "Tools",
                    color = if (selectedTab == HomeTab.TOOLS) AppBackground else MutedText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
    HorizontalDivider(color = BorderPurple, thickness = 1.dp)
}

@Composable
fun ToolsLanding(onNavigateToTool: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, bottom = 80.dp)
    ) {
        item {
            Text("AI & OCR Tools", fontWeight = FontWeight.Bold, color = AccentPurple, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) {
                    ToolItemCard("Extract Text (OCR)", "Extract text from images & scans", onNavigateToTool)
                }
                Box(modifier = Modifier.weight(1f)) {
                    ToolItemCard("Layout OCR", "Recreate document structure layout", onNavigateToTool)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) {
                    ToolItemCard("Enhance Doc", "Improve document clarity & quality", onNavigateToTool)
                }
                Box(modifier = Modifier.weight(1f)) {
                    ToolItemCard("Extract Tables", "Import structured sheets/tables", onNavigateToTool)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text("PDF Utilities", fontWeight = FontWeight.Bold, color = AccentPurple, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) {
                    ToolItemCard("Merge PDF", "Combine multiple PDFs into one", onNavigateToTool)
                }
                Box(modifier = Modifier.weight(1f)) {
                    ToolItemCard("eSign PDF", "Sign your PDF files digitally", onNavigateToTool)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) {
                    ToolItemCard("Protect PDF", "Encrypt documents with passwords", onNavigateToTool)
                }
                Box(modifier = Modifier.weight(1f)) {
                    ToolItemCard("Image to PDF", "Convert JPG/PNG to PDF file", onNavigateToTool)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) {
                    ToolItemCard("Compress PDF", "Reduce size with quality preserved", onNavigateToTool)
                }
                Box(modifier = Modifier.weight(1f)) {
                    ToolItemCard("PDF to Image", "Extract PDF pages into JPG grids", onNavigateToTool)
                }
            }
        }
    }
}

@Composable
fun ToolItemCard(title: String, desc: String, onNavigateToTool: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ToolCardBackground),
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable { onNavigateToTool(title) }
            .border(1.dp, BorderPurple, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(desc, color = MutedText, fontSize = 11.sp, lineHeight = 14.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileOptionsBottomSheet(file: com.digidocx.core.model.DocumentItem, onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = TopNavigation
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = file.name,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                modifier = Modifier.padding(16.dp),
                fontSize = 16.sp
            )
            HorizontalDivider(color = BorderPurple)

            ListItem(
                headlineContent = { Text("Share", color = PrimaryText) },
                leadingContent = { Icon(Icons.Default.Share, contentDescription = null, tint = AccentPurple) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                modifier = Modifier.clickable { onDismiss() }
            )
            ListItem(
                headlineContent = { Text("Rename", color = PrimaryText) },
                leadingContent = { Icon(Icons.Default.Edit, contentDescription = null, tint = AccentPurple) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                modifier = Modifier.clickable { onDismiss() }
            )
            ListItem(
                headlineContent = { Text("Compress", color = PrimaryText) },
                leadingContent = { Icon(Icons.Default.Settings, contentDescription = null, tint = AccentPurple) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                modifier = Modifier.clickable { onDismiss() }
            )
            ListItem(
                headlineContent = { Text("Delete", color = Color(0xFFFF5D5D)) },
                leadingContent = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFFF5D5D)) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                modifier = Modifier.clickable { onDismiss() }
            )
        }
    }
}