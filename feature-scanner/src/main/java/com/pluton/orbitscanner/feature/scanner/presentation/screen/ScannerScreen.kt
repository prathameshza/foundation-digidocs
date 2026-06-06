@file:Suppress("DEPRECATION")

package com.pluton.orbitscanner.feature.scanner.presentation.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.pluton.orbitscanner.feature.scanner.presentation.state.ScannerStep
import com.pluton.orbitscanner.feature.scanner.presentation.state.ScannerUiState
import com.pluton.orbitscanner.feature.scanner.presentation.viewmodel.ScannerViewModel

@Composable
fun ScannerScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEditor: (String) -> Unit,
    viewModel: ScannerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(AppBackground)) {
        when (uiState.step) {
            ScannerStep.CAPTURE -> CaptureView(uiState, viewModel, onNavigateBack)
            ScannerStep.REVIEW -> ReviewAndCropView(uiState, viewModel)
            ScannerStep.ASSEMBLY -> AssemblyView(uiState, viewModel, onNavigateBack, onNavigateToEditor)
        }
    }
}

@Composable
fun CaptureView(
    state: ScannerUiState,
    viewModel: ScannerViewModel,
    onNavigateBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Mock Finder Viewport Grid
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 120.dp, top = 60.dp)
                .background(Color(0xFF070913))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridColor = Color(0x22C8BCFF)
                // Boundary lines
                drawRect(color = gridColor, size = size, style = androidx.compose.ui.graphics.drawscope.Stroke(2f))
            }

            // Paper Scan Helper guide overlay
            Box(
                modifier = Modifier
                    .fillMaxSize(0.75f)
                    .align(Alignment.Center)
                    .border(1.5.dp, AccentPurple, RoundedCornerShape(12.dp))
            )
        }

        // Top Controls Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = PrimaryText)
            }
            // Rectangular Flash Card (as detailed on Page 18)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CardBackground)
                    .clickable { viewModel.toggleFlash() }
                    .border(1.dp, if (state.isFlashActive) AccentPurple else BorderPurple, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (state.isFlashActive) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flash",
                        tint = if (state.isFlashActive) AccentPurple else MutedText,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Flash",
                        color = if (state.isFlashActive) PrimaryText else MutedText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Bottom Controls Section
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(TopNavigation)
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            // Document mode selectors: SCAN, PHOTO, WHITEBOARD (Page 18)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("SCAN", "PHOTO", "WHITEBOARD").forEach { mode ->
                    val isActive = state.activeMode == mode
                    Box(
                        modifier = Modifier
                            .clickable { viewModel.updateMode(mode) }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = mode,
                            color = if (isActive) AccentPurple else MutedText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Capture Row Buttons (Page 18)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left - Gallery shortcut import button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = MutedText, modifier = Modifier.size(20.dp))
                }

                // Center - Lavender Ring Shutter Button
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .border(3.dp, AccentPurple, CircleShape)
                        .padding(6.dp)
                        .clip(CircleShape)
                        .background(AccentPurple)
                        .clickable { viewModel.triggerShutterClick() }
                )

                // Right - Pages Count Badge / Navigation Next Button
                Box(
                    modifier = Modifier.size(44.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.capturedPages.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AccentPurple)
                                .clickable { viewModel.navigateToStep(ScannerStep.REVIEW) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                state.capturedPages.size.toString(),
                                color = AppBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        // Display mock badge counts like Page 18
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(CardBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("6", color = MutedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewAndCropView(
    state: ScannerUiState,
    viewModel: ScannerViewModel
) {
    val activePage = state.capturedPages.getOrNull(state.activePageIndex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TopNavigation)
                .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateToStep(ScannerStep.CAPTURE) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryText)
            }
            Column {
                Text("Review & Edit", fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 16.sp)
                Text("Adjust the corners to frame the document", color = MutedText, fontSize = 11.sp)
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            GridBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 120.dp, top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Interactive Crop document container (Page 19)
                activePage?.let { page ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .fillMaxHeight(0.65f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardBackground)
                            .graphicsLayer {
                                rotationZ = page.rotationDegrees.toFloat()
                                scaleX = page.cropRectScale
                                scaleY = page.cropRectScale
                            }
                    ) {
                        // Drawing document layout preview
                        DocumentBlueprintGrid()

                        // Crop Anchor nodes
                        CornerAnchors()
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Adjustment Tools bar (Page 19)
                Row(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { viewModel.rotateActivePage() }
                    ) {
                        Icon(Icons.Default.RotateRight, contentDescription = "Rotate", tint = AccentPurple)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Rotate", color = PrimaryText, fontSize = 11.sp)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { viewModel.retakeActivePage() }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Retake", tint = AccentPurple)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Retake", color = PrimaryText, fontSize = 11.sp)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { viewModel.fitActivePage() }
                    ) {
                        Icon(Icons.Default.AspectRatio, contentDescription = "Fit", tint = AccentPurple)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Fit", color = PrimaryText, fontSize = 11.sp)
                    }
                }
            }

            // Bottom Carousel and Main Save Button (Page 19)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(TopNavigation)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    itemsIndexed(state.capturedPages) { index, item ->
                        val isSelected = index == state.activePageIndex
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) AccentPurple.copy(alpha = 0.2f) else CardBackground)
                                .border(
                                    1.5.dp,
                                    if (isSelected) AccentPurple else BorderPurple,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { viewModel.selectActivePage(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text((index + 1).toString(), color = PrimaryText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    item {
                        // "+" Add Page Carousel Option
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .border(1.dp, BorderPurple, RoundedCornerShape(6.dp))
                                .clickable { viewModel.navigateToStep(ScannerStep.CAPTURE) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add page", tint = AccentPurple)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.navigateToStep(ScannerStep.ASSEMBLY) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPurple,
                        contentColor = AppBackground
                    ),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Review and Edit >", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AssemblyView(
    state: ScannerUiState,
    viewModel: ScannerViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEditor: (String) -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BasicTextField(
                            value = state.docTitle,
                            onValueChange = { viewModel.updateDocTitle(it) },
                            textStyle = TextStyle(color = PrimaryText, fontWeight = FontWeight.Bold, fontSize = 16.sp),
                            cursorBrush = SolidColor(AccentPurple),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit name",
                            tint = MutedText,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateToStep(ScannerStep.REVIEW) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TopNavigation)
            )
        },
        containerColor = AppBackground
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            GridBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp, start = 16.dp, end = 16.dp, top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Assembly Pages thumb List vertical stack (Page 20)
                LazyRow(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    itemsIndexed(state.capturedPages) { index, page ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight(0.7f)
                                    .width(180.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CardBackground)
                                    .border(1.dp, BorderPurple, RoundedCornerShape(8.dp))
                            ) {
                                DocumentBlueprintGrid()
                                Box(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .align(Alignment.TopEnd)
                                        .size(24.dp)
                                        .background(AccentPurple, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text((index + 1).toString(), color = AppBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Page ${index + 1}", color = MutedText, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action assembly buttons: Edit/Annotate trigger (Page 20)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            onNavigateToEditor(state.docTitle)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CardBackground,
                            contentColor = PrimaryText
                        ),
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit pages")
                    }

                    Button(
                        onClick = { viewModel.navigateToStep(ScannerStep.CAPTURE) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CardBackground,
                            contentColor = PrimaryText
                        ),
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add page")
                    }
                }
            }

            // Save Document Sticky Bar (Page 20)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(TopNavigation)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.saveScannedDocument {
                            Toast.makeText(context, "Document Saved Successfully!", Toast.LENGTH_SHORT).show()
                            onNavigateBack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPurple,
                        contentColor = AppBackground
                    ),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(color = AppBackground, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Save Document", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DocumentBlueprintGrid() {
    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val lineColor = Color(0x11C8BCFF)
        val strokeWidth = 2f
        
        // Mocking generic document layout lines
        var y = 20f
        while (y < size.height) {
            drawLine(
                color = lineColor,
                start = Offset(10f, y),
                end = Offset(size.width - 10f, y),
                strokeWidth = strokeWidth
            )
            y += 40f
        }
    }
}

@Composable
fun CornerAnchors() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val anchorColor = AccentPurple
        val radius = 16f
        val padding = 30f
        
        // 4 crop corners
        drawCircle(color = anchorColor, radius = radius, center = Offset(padding, padding))
        drawCircle(color = anchorColor, radius = radius, center = Offset(size.width - padding, padding))
        drawCircle(color = anchorColor, radius = radius, center = Offset(padding, size.height - padding))
        drawCircle(color = anchorColor, radius = radius, center = Offset(size.width - padding, size.height - padding))
    }
}
