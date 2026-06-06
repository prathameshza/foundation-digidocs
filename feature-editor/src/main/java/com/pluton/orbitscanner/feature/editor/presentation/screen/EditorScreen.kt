@file:Suppress("DEPRECATION")

package com.pluton.orbitscanner.feature.editor.presentation.screen

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pluton.orbitscanner.core.ui.theme.*
import com.pluton.orbitscanner.core.ui.widgets.GridBackground
import com.pluton.orbitscanner.feature.editor.presentation.state.EditorActiveTab
import com.pluton.orbitscanner.feature.editor.presentation.state.EditorSubSheet
import com.pluton.orbitscanner.feature.editor.presentation.state.EditorUiState
import com.pluton.orbitscanner.feature.editor.presentation.viewmodel.EditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    documentId: String,
    onNavigateBack: () -> Unit,
    viewModel: EditorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var docTitle by remember { mutableStateOf(documentId) }

    LaunchedEffect(documentId) {
        viewModel.initializeDocument(documentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BasicTextField(
                            value = docTitle,
                            onValueChange = { docTitle = it },
                            textStyle = TextStyle(color = PrimaryText, fontWeight = FontWeight.Bold, fontSize = 16.sp),
                            cursorBrush = SolidColor(AccentPurple),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${uiState.activePageIndex}/${uiState.totalPages}",
                            color = MutedText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = if (uiState.activeSubSheet == EditorSubSheet.CROP_ROTATE) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = if (uiState.activeSubSheet == EditorSubSheet.CROP_ROTATE) "Done" else "Close",
                            tint = if (uiState.activeSubSheet == EditorSubSheet.CROP_ROTATE) AccentPurple else PrimaryText
                        )
                    }
                },
                actions = {
                    if (uiState.activeSubSheet == EditorSubSheet.CROP_ROTATE) {
                        // Display "Done" instead of Save (Page 76)
                        Text(
                            "Done",
                            color = AccentPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(end = 16.dp).clickable { viewModel.selectSubSheet(EditorSubSheet.NONE) }
                        )
                    } else {
                        IconButton(onClick = { Toast.makeText(context, "Saved changes!", Toast.LENGTH_SHORT).show() }) {
                            Icon(Icons.Default.Save, contentDescription = "Save", tint = PrimaryText)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TopNavigation)
            )
        },
        containerColor = AppBackground
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            GridBackground()

            // Main Preview Document Canvas
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 120.dp, top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .fillMaxHeight(0.72f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardBackground)
                        .border(1.5.dp, BorderPurple, RoundedCornerShape(12.dp))
                ) {
                    DocumentBlueprintLines()

                    // Optional Text Overlay Markup (Page 87)
                    if (uiState.textOverlayActive) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .border(1.dp, AccentPurple, RoundedCornerShape(6.dp))
                                .background(AppBackground.copy(alpha = 0.8f))
                                .padding(12.dp)
                        ) {
                            Text(
                                uiState.textOverlayText,
                                color = PrimaryText,
                                fontSize = uiState.textOverlaySize.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Optional Signature Overlay Markup (Page 90)
                    if (uiState.signaturePlaced) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 32.dp)
                                .border(1.dp, AccentPurple, RoundedCornerShape(4.dp))
                                .background(Color.Transparent)
                                .clickable { viewModel.removeSignature() }
                                .padding(10.dp)
                        ) {
                            // Signature Vector trace mockup
                            Canvas(modifier = Modifier.size(100.dp, 40.dp)) {
                                val strokeColor = AccentPurple
                                drawLine(strokeColor, Offset(10f, size.height - 10f), Offset(size.width - 10f, 10f), strokeWidth = uiState.signatureWeight)
                                drawLine(strokeColor, Offset(size.width / 2, size.height - 10f), Offset(size.width, size.height / 2), strokeWidth = uiState.signatureWeight)
                            }
                        }
                    }

                    // Interactive Corner-Crop anchors overlay (Page 76)
                    if (uiState.activeSubSheet == EditorSubSheet.CROP_ROTATE) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(0.9f)
                                .align(Alignment.Center)
                                .border(1.5.dp, AccentPurple, RoundedCornerShape(8.dp))
                        ) {
                            CornerGuideAnchors()
                        }
                    }
                }
            }

            // Persistence Bottom sheets overlaying views
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                // Secondary Sheets options (Filters, Crops, Sign options)
                AnimatedVisibility(
                    visible = uiState.activeSubSheet != EditorSubSheet.NONE,
                    enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
                    exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(TopNavigation)
                            .border(width = 1.dp, color = BorderPurple, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .padding(16.dp)
                    ) {
                        SecondarySheetContent(uiState, viewModel)
                    }
                }

                // First menu ribbon (OCR & AI tools, Edit, Sign tabs) (Page 73)
                PrimaryToolbarHub(uiState, viewModel)
            }
        }
    }
}

@Composable
fun PrimaryToolbarHub(state: EditorUiState, viewModel: EditorViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TopNavigation)
            .border(1.dp, BorderPurple)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { viewModel.selectActiveTab(EditorActiveTab.OCR) }
        ) {
            Icon(
                imageVector = Icons.Default.Translate,
                contentDescription = null,
                tint = if (state.activeTab == EditorActiveTab.OCR) AccentPurple else MutedText,
                modifier = Modifier.size(20.dp) // Sized appropriately (Page 73)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("OCR & AI tools", color = if (state.activeTab == EditorActiveTab.OCR) AccentPurple else MutedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { viewModel.selectActiveTab(EditorActiveTab.EDIT) }
        ) {
            Icon(
                imageVector = Icons.Default.EditNote,
                contentDescription = null,
                tint = if (state.activeTab == EditorActiveTab.EDIT) AccentPurple else MutedText,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("Edit", color = if (state.activeTab == EditorActiveTab.EDIT) AccentPurple else MutedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { viewModel.selectActiveTab(EditorActiveTab.SIGN) }
        ) {
            Icon(
                imageVector = Icons.Default.DriveFileRenameOutline,
                contentDescription = null,
                tint = if (state.activeTab == EditorActiveTab.SIGN) AccentPurple else MutedText,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("Sign", color = if (state.activeTab == EditorActiveTab.SIGN) AccentPurple else MutedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SecondarySheetContent(state: EditorUiState, viewModel: EditorViewModel) {
    val context = LocalContext.current

    when (state.activeSubSheet) {
        EditorSubSheet.FILTERS -> {
            Column {
                Text("Select Document Filter", fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(10.dp))
                // Filters grid options: None, Lighten, Document, B&W, Grayscale (Page 75)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("None", "Lighten", "Document", "B&W", "Grayscale").forEach { filter ->
                        val isSelected = state.activeFilter == filter
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AccentPurple else CardBackground)
                                .clickable { viewModel.updateFilter(filter) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(filter, color = if (isSelected) AppBackground else PrimaryText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        EditorSubSheet.CROP_ROTATE -> {
            // Options: Auto crop, Free / No crop, Rotate (Page 76)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { Toast.makeText(context, "Auto Crop active.", Toast.LENGTH_SHORT).show() }) {
                    Icon(Icons.Default.Crop, contentDescription = null, tint = AccentPurple)
                    Text("Auto crop", color = PrimaryText, fontSize = 11.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { Toast.makeText(context, "Free cropping active.", Toast.LENGTH_SHORT).show() }) {
                    Icon(Icons.Default.AspectRatio, contentDescription = null, tint = AccentPurple)
                    Text("Free / No crop", color = PrimaryText, fontSize = 11.sp)
                }
            }
        }

        EditorSubSheet.FORMAT -> {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Page Formatter", fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 13.sp)
                    // Smaller "Apply to all" toggle switch (Page 78)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Apply to all", color = MutedText, fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Switch(
                            checked = true,
                            onCheckedChange = {},
                            modifier = Modifier.scaleScale(0.7f), // Sized appropriately (Page 78)
                            colors = SwitchDefaults.colors(checkedTrackColor = AccentPurple)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(listOf("Fit to image", "A4", "A5", "ID Card", "Letter", "Legal")) { _, format ->
                        val isSelected = state.activePageFormat == format
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AccentPurple else CardBackground)
                                .clickable { viewModel.updateFormat(format) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(format, color = if (isSelected) AppBackground else PrimaryText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        EditorSubSheet.DELETE -> {
            // Delete alert card showing transparent container (Page 79)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Delete this page?", fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("This action cannot be undone.", color = MutedText, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { viewModel.selectSubSheet(EditorSubSheet.NONE) },
                        colors = ButtonDefaults.buttonColors(containerColor = CardBackground, contentColor = PrimaryText),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { viewModel.deleteCurrentPage { onDismissBottomSheet(context) } },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5D5D), contentColor = Color.White),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Text("Delete")
                    }
                }
            }
        }

        EditorSubSheet.ADD_TEXT -> {
            // Formatting text ribbon - font, size, style, color, alignment (Page 87, 88)
            Column {
                Text("Text markup formatting", fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(10.dp))
                
                // Alignment triggers grouped together on a single row (Page 88)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { viewModel.addTextOverlay("Meeting Notes") }) {
                            Icon(Icons.Default.FormatBold, contentDescription = null, tint = AccentPurple)
                        }
                        IconButton(onClick = { viewModel.addTextOverlay("Project Proposals") }) {
                            Icon(Icons.Default.FormatItalic, contentDescription = null, tint = AccentPurple)
                        }
                    }
                    
                    // Alignment selector icons in a single continuous row
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardBackground)
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(Icons.Default.FormatAlignLeft, Icons.Default.FormatAlignCenter, Icons.Default.FormatAlignRight).forEach { alignIcon ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.Transparent)
                                    .clickable { Toast.makeText(context, "Alignment changed.", Toast.LENGTH_SHORT).show() }
                                    .padding(6.dp)
                            ) {
                                Icon(alignIcon, contentDescription = null, tint = MutedText, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                // Text size slider
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Size", color = MutedText, fontSize = 11.sp, modifier = Modifier.width(40.dp))
                    Slider(
                        value = state.textOverlaySize,
                        onValueChange = { viewModel.updateTextSize(it) },
                        valueRange = 10f..32f,
                        colors = SliderDefaults.colors(thumbColor = AccentPurple, activeTrackColor = AccentPurple),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        EditorSubSheet.SIGNATURE_DRAW -> {
            // Draw signature panel with customize weight slider (Page 90)
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Customize Signature Stroke", fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 13.sp)
                    Text("Place Signature", color = AccentPurple, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.clickable { viewModel.placeSignature() })
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Weight", color = MutedText, fontSize = 11.sp, modifier = Modifier.width(50.dp))
                    Slider(
                        value = state.signatureWeight,
                        onValueChange = { viewModel.updateSignatureWeight(it) },
                        valueRange = 2f..12f,
                        colors = SliderDefaults.colors(thumbColor = AccentPurple, activeTrackColor = AccentPurple),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${state.signatureWeight.toInt()}px", color = PrimaryText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Opacity", color = MutedText, fontSize = 11.sp, modifier = Modifier.width(50.dp))
                    Slider(
                        value = state.signatureOpacity,
                        onValueChange = { viewModel.updateSignatureOpacity(it) },
                        valueRange = 10f..100f,
                        colors = SliderDefaults.colors(thumbColor = AccentPurple, activeTrackColor = AccentPurple),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${state.signatureOpacity.toInt()}%", color = PrimaryText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        EditorSubSheet.SIGNATURE_UPLOAD -> {
            // Transparent/White background selections (Page 94)
            Column {
                Text("Import Signature Settings", fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardBackground)
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Signature Background", fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 13.sp)
                        Text(if (state.isUploadTransparent) "Transparent background" else "White background", color = MutedText, fontSize = 10.sp)
                    }
                    Row {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (state.isUploadTransparent) AccentPurple else ToolCardBackground)
                                .clickable { viewModel.toggleUploadBackground(true) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Transparent", color = if (state.isUploadTransparent) AppBackground else MutedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (!state.isUploadTransparent) AccentPurple else ToolCardBackground)
                                .clickable { viewModel.toggleUploadBackground(false) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("White", color = if (!state.isUploadTransparent) AppBackground else MutedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        else -> {}
    }
}

@Composable
fun PrimaryToolbarOverlay(state: EditorUiState, viewModel: EditorViewModel) {
    // Menu content shown when clicking one of the main tabs (Page 73, 81, 84)
    val context = LocalContext.current

    when (state.activeTab) {
        EditorActiveTab.OCR -> {
            // Horizontal list showing exactly 4 visible tools (Page 81)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TopNavigation)
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Formatting: White outer borders, with mixed AccentPurple inside (Page 81)
                val ocrTools = listOf(
                    "Text Extract" to Icons.Default.Translate,
                    "Layout OCR" to Icons.Default.Layers,
                    "Extract Table" to Icons.Default.GridOn,
                    "Translate" to Icons.Default.GTranslate
                )
                ocrTools.forEach { (label, icon) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            Toast.makeText(context, "$label launched.", Toast.LENGTH_SHORT).show()
                            viewModel.selectActiveTab(EditorActiveTab.NONE)
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .border(1.5.dp, Color.White, CircleShape) // White outer border (Page 81)
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(AccentPurple), // Purple Accent inside
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(label, color = PrimaryText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            HorizontalDivider(color = BorderPurple)
        }

        EditorActiveTab.EDIT -> {
            // Edit Sub-tools (Page 73)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TopNavigation)
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    Triple("Filters", Icons.Default.FilterVintage, EditorSubSheet.FILTERS),
                    Triple("Crop & Rotate", Icons.Default.Crop, EditorSubSheet.CROP_ROTATE),
                    Triple("Format", Icons.Default.AspectRatio, EditorSubSheet.FORMAT),
                    Triple("Delete", Icons.Default.Delete, EditorSubSheet.DELETE)
                ).forEach { (label, icon, sheet) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { viewModel.selectSubSheet(sheet) }
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (state.activeSubSheet == sheet) AccentPurple else MutedText,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(label, color = if (state.activeSubSheet == sheet) AccentPurple else MutedText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            HorizontalDivider(color = BorderPurple)
        }

        EditorActiveTab.SIGN -> {
            // Sign Sub-tools (Page 84)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TopNavigation)
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    Triple("Add text", Icons.Default.TextFields, EditorSubSheet.ADD_TEXT),
                    Triple("Create sign", Icons.Default.Gesture, EditorSubSheet.SIGNATURE_DRAW),
                    Triple("Upload sign", Icons.Default.UploadFile, EditorSubSheet.SIGNATURE_UPLOAD)
                ).forEach { (label, icon, sheet) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { viewModel.selectSubSheet(sheet) }
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (state.activeSubSheet == sheet) AccentPurple else MutedText,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(label, color = if (state.activeSubSheet == sheet) AccentPurple else MutedText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            HorizontalDivider(color = BorderPurple)
        }
        else -> {}
    }
}

@Composable
fun DocumentBlueprintLines() {
    Canvas(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        val lineColor = Color(0x06C8BCFF)
        var y = 20f
        while (y < size.height) {
            drawLine(lineColor, Offset(10f, y), Offset(size.width - 10f, y), strokeWidth = 2f)
            y += 40f
        }
    }
}

@Composable
fun CornerGuideAnchors() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val anchorColor = AccentPurple
        val radius = 14f
        drawCircle(anchorColor, radius, center = Offset(0f, 0f))
        drawCircle(anchorColor, radius, center = Offset(size.width, 0f))
        drawCircle(anchorColor, radius, center = Offset(0f, size.height))
        drawCircle(anchorColor, radius, center = Offset(size.width, size.height))
    }
}

private fun onDismissBottomSheet(context: android.content.Context) {
    Toast.makeText(context, "Page Deleted Successfully!", Toast.LENGTH_SHORT).show()
}

// Ext helper for small custom scaling
private fun Modifier.scaleScale(scale: Float): Modifier = this.graphicsLayer {
    scaleX = scale
    scaleY = scale
}
