@file:Suppress("DEPRECATION")

package com.pluton.orbitscanner.feature.home.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pluton.orbitscanner.core.ui.theme.*
import com.pluton.orbitscanner.feature.home.domain.model.HomeItem
import com.pluton.orbitscanner.feature.home.presentation.component.DocumentItemCard
import com.pluton.orbitscanner.feature.home.presentation.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToFolder: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    var query by remember { mutableStateOf("") }
    var selectedExtensionFilter by remember { mutableStateOf<String?>(null) }
    val recentSearches = remember { mutableStateListOf("invoice may", "project proposal", "meeting notes", "contract") }

    val filteredResults by viewModel.searchResults.collectAsState()

    LaunchedEffect(query, selectedExtensionFilter) {
        viewModel.searchItems(query, selectedExtensionFilter)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Search files", color = MutedText, fontSize = 16.sp) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = PrimaryText,
                            unfocusedTextColor = PrimaryText
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            if (query.isNotBlank() && !recentSearches.contains(query)) {
                                recentSearches.add(0, query)
                            }
                        }),
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { query = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = PrimaryText)
                                }
                            }
                        }
                    )
                },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf("PDF" to ".pdf", "Word" to ".docx", "Image" to ".jpg", "Text" to ".txt")
                filters.forEach { (label, ext) ->
                    val isSelected = selectedExtensionFilter == ext
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) AccentPurple else CardBackground)
                            .clickable {
                                selectedExtensionFilter = if (isSelected) null else ext
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(label, color = if (isSelected) AppBackground else MutedText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (query.isEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Text("Recent searches", fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 14.sp)
                    Text(
                        "Clear all",
                        color = AccentPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { recentSearches.clear() }
                    )
                }

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(recentSearches) { search ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { query = search }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.History, contentDescription = null, tint = MutedText, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(search, color = PrimaryText, fontSize = 14.sp, modifier = Modifier.weight(1f))
                            IconButton(onClick = { recentSearches.remove(search) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Clear, contentDescription = "Remove", tint = MutedText, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            } else {
                Text(
                    "Files matching \"$query\"",
                    color = MutedText,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.SemiBold
                )

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(filteredResults) { file ->
                        if (file is HomeItem.File) {
                            DocumentItemCard(file = file, isGridView = false, onClick = {}, onActionClick = {})
                        }
                    }
                }
            }
        }
    }
}