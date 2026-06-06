@file:Suppress("DEPRECATION")

package com.pluton.orbitscanner.feature.pdftools.presentation.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pluton.orbitscanner.core.ui.theme.*
import com.pluton.orbitscanner.core.ui.widgets.GridBackground
import com.pluton.orbitscanner.feature.pdftools.domain.model.PdfToolType
import com.pluton.orbitscanner.feature.pdftools.presentation.state.PdfToolsStep
import com.pluton.orbitscanner.feature.pdftools.presentation.state.PdfToolsUiState
import com.pluton.orbitscanner.feature.pdftools.presentation.viewmodel.PdfToolsViewModel

@Composable
fun PdfToolsScreen(
    toolName: String,
    onNavigateBack: () -> Unit,
    viewModel: PdfToolsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(toolName) {
        viewModel.initializeTool(toolName)
    }

    Box(modifier = Modifier.fillMaxSize().background(AppBackground)) {
        when (uiState.step) {
            PdfToolsStep.CONFIG -> ConfigLayoutView(toolName, uiState, viewModel, onNavigateBack)
            PdfToolsStep.PROCESSING -> ProcessingView(toolName, uiState, viewModel)
            PdfToolsStep.SUCCESS -> CompletedOutcomeView(toolName, uiState, viewModel, onNavigateBack)
            PdfToolsStep.PREVIEW -> DocumentPreviewView(uiState, viewModel)
            PdfToolsStep.LOCKED_SHIELD -> SecurityUnlockView(uiState, viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigLayoutView(
    toolName: String,
    state: PdfToolsUiState,
    viewModel: PdfToolsViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var showAddSignerDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(toolName, fontWeight = FontWeight.Bold, color = PrimaryText) },
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
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            GridBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp, start = 16.dp, end = 16.dp, top = 16.dp)
            ) {
                // Config sections based on active PDF tools (Page 30, 32, 35, 38, 40, 44)
                when (state.activeType) {
                    PdfToolType.COMPRESS -> {
                        Text("Compress level options", fontWeight = FontWeight.Bold, color = AccentPurple, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Compression Slider Selection Card (Page 30)
                        listOf(
                            "Low" to "Low • ~90% Quality (3.2 MB)",
                            "Medium" to "Medium • ~70% Quality (2.1 MB)",
                            "High" to "High • ~50% Quality (1.2 MB)"
                        ).forEach { (level, desc) ->
                            val isSelected = state.compressLevel == level
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) CardBackground else ToolCardBackground)
                                    .border(
                                        1.5.dp,
                                        if (isSelected) AccentPurple else BorderPurple,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.updateCompressLevel(level) }
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(desc, color = PrimaryText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.updateCompressLevel(level) },
                                    colors = RadioButtonDefaults.colors(selectedColor = AccentPurple)
                                )
                            }
                        }
                    }

                    PdfToolType.PROTECT -> {
                        Text("Secure your document", fontWeight = FontWeight.Bold, color = AccentPurple, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Password Confirm input panels (Page 32)
                        var p1 by remember { mutableStateOf(state.passwordInput) }
                        var p2 by remember { mutableStateOf(state.passwordConfirm) }
                        var hidePassword by remember { mutableStateOf(true) }

                        OutlinedTextField(
                            value = p1,
                            onValueChange = { p1 = it; viewModel.updatePassword(it, p2) },
                            label = { Text("Password") },
                            visualTransformation = if (hidePassword) PasswordVisualTransformation() else VisualTransformation.None,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { hidePassword = !hidePassword }) {
                                    Icon(
                                        imageVector = if (hidePassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = MutedText
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentPurple,
                                unfocusedBorderColor = BorderPurple,
                                focusedTextColor = PrimaryText,
                                unfocusedTextColor = PrimaryText
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = p2,
                            onValueChange = { p2 = it; viewModel.updatePassword(p1, it) },
                            label = { Text("Confirm Password") },
                            visualTransformation = if (hidePassword) PasswordVisualTransformation() else VisualTransformation.None,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentPurple,
                                unfocusedBorderColor = BorderPurple,
                                focusedTextColor = PrimaryText,
                                unfocusedTextColor = PrimaryText
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    PdfToolType.MERGE, PdfToolType.IMAGE_TO_PDF -> {
                        // Reorder listing drawers (Page 40, 44)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Arrange items queue", fontWeight = FontWeight.Bold, color = AccentPurple, fontSize = 14.sp)
                            Text(
                                "Add More",
                                color = AccentPurple,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable {
                                    Toast.makeText(context, "Adding more files...", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                            itemsIndexed(state.selectedFiles) { index, item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(CardBackground)
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.DragIndicator, contentDescription = null, tint = MutedText)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Icon(
                                            imageVector = if (state.activeType == PdfToolType.MERGE) Icons.Default.Description else Icons.Default.Image,
                                            contentDescription = null,
                                            tint = AccentPurple
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(item, fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 13.sp)
                                            Text("A4 • Page ${index + 1}", color = MutedText, fontSize = 10.sp)
                                        }
                                    }
                                    IconButton(onClick = { viewModel.removeFileItem(index) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF5D5D))
                                    }
                                }
                            }
                        }
                    }

                    PdfToolType.SIGN -> {
                        // eSign form panels (Page 38)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Signers list", fontWeight = FontWeight.Bold, color = AccentPurple, fontSize = 14.sp)
                            Text(
                                "+ Add Signer",
                                color = AccentPurple,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { showAddSignerDialog = true }
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                            itemsIndexed(state.signersList) { index, signer ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(CardBackground)
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = AccentPurple)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(signer, color = PrimaryText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    PdfToolType.PDF_TO_IMAGE -> {
                        // Convert settings layouts (Page 35)
                        Text("Conversion formats", fontWeight = FontWeight.Bold, color = AccentPurple, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(CardBackground)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Select Page Range", fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 14.sp)
                                Text("Extracting all 12 pages into JPG folders", color = MutedText, fontSize = 11.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AccentPurple)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("All Pages", color = AppBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Sticky Save / Process Trigger bar
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(TopNavigation)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.runProcessingSimulation {
                            viewModel.navigateToStep(PdfToolsStep.SUCCESS)
                        }
                    },
                    enabled = if (state.activeType == PdfToolType.PROTECT) state.isPasswordValid else true,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPurple,
                        contentColor = AppBackground,
                        disabledContainerColor = MutedText
                    ),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Process Utility", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showAddSignerDialog) {
        var nameInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddSignerDialog = false },
            title = { Text("Add Signer Details", color = PrimaryText) },
            text = {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Signer Name / Email") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = PrimaryText,
                        unfocusedTextColor = PrimaryText,
                        focusedBorderColor = AccentPurple,
                        unfocusedBorderColor = BorderPurple
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nameInput.isNotBlank()) {
                            viewModel.addSigner(nameInput)
                        }
                        showAddSignerDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple, contentColor = AppBackground)
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSignerDialog = false }) {
                    Text("Cancel", color = MutedText)
                }
            },
            containerColor = TopNavigation
        )
    }
}

@Composable
fun ProcessingView(toolName: String, state: PdfToolsUiState, viewModel: PdfToolsViewModel) {
    val desc = when (state.activeType) {
        PdfToolType.MERGE -> "Merging documents..."
        PdfToolType.SIGN -> "Signing files digitally..."
        PdfToolType.PROTECT -> "Securing with passwords..."
        PdfToolType.IMAGE_TO_PDF -> "Converting JPG folder to PDF..."
        PdfToolType.COMPRESS -> "Compressing PDF size..."
        PdfToolType.PDF_TO_IMAGE -> "Extracting pages into image folder..."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            progress = { state.progressPercent / 100f },
            modifier = Modifier.size(80.dp),
            color = AccentPurple,
            strokeWidth = 6.dp,
            trackColor = BorderPurple
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(desc, fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text("${state.progressPercent.toInt()}%", color = MutedText, fontSize = 14.sp)
    }
}

@Composable
fun CompletedOutcomeView(
    toolName: String,
    state: PdfToolsUiState,
    viewModel: PdfToolsViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val title = when (state.activeType) {
        PdfToolType.MERGE -> "PDF Merged!"
        PdfToolType.SIGN -> "Document Signed Successfully!"
        PdfToolType.PROTECT -> "PDF Protected!"
        PdfToolType.IMAGE_TO_PDF -> "PDF Created Successfully!"
        PdfToolType.COMPRESS -> "Compression Complete"
        PdfToolType.PDF_TO_IMAGE -> "Conversion completed"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(36.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 18.sp, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(16.dp))

            // Compression size outcomes comparison card (Page 31)
            if (state.activeType == PdfToolType.COMPRESS) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ToolCardBackground),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Original Size", color = MutedText, fontSize = 11.sp)
                                Text("4.8 MB", fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 18.sp)
                            }
                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = AccentPurple)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Compressed Size", color = MutedText, fontSize = 11.sp)
                                Text("2.6 MB", fontWeight = FontWeight.Bold, color = AccentPurple, fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF4CAF50).copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("45% smaller • 2.2 MB saved", color = Color(0xFF81C784), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Output document preview card (Page 31, 42, 46)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBackground)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (state.activeType == PdfToolType.PDF_TO_IMAGE) Icons.Default.FolderZip else Icons.Default.Description,
                        contentDescription = null,
                        tint = AccentPurple,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        val ext = if (state.activeType == PdfToolType.PDF_TO_IMAGE) "zip" else "pdf"
                        Text("${state.outputFileName}.$ext", fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 14.sp)
                        Text(
                            text = if (state.activeType == PdfToolType.PDF_TO_IMAGE) "${state.imagePagesCount} Pages • ZIP Folder" else "24 Pages • PDF Document",
                            color = MutedText,
                            fontSize = 11.sp
                        )
                    }
                }

                // File Preview Option (Page 31, 33)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentPurple.copy(alpha = 0.12f))
                        .clickable {
                            if (state.activeType == PdfToolType.PROTECT) {
                                // Locked verification screen triggered (Page 33)
                                viewModel.navigateToStep(PdfToolsStep.LOCKED_SHIELD)
                            } else {
                                viewModel.navigateToStep(PdfToolsStep.PREVIEW)
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Visibility, contentDescription = null, tint = AccentPurple, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Preview", color = AccentPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Action Menu Buttons (Page 31, 39, 42, 46)
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        Toast.makeText(context, "File shared successfully.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CardBackground, contentColor = PrimaryText),
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share File", fontSize = 13.sp)
                }

                Button(
                    onClick = {
                        viewModel.saveDocumentOutcome {
                            Toast.makeText(context, "Saved successfully to local storage!", Toast.LENGTH_SHORT).show()
                            onNavigateBack()
                        }
                    },
                    enabled = !state.isSaving,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple, contentColor = AppBackground),
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(color = AppBackground, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save File", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DocumentPreviewView(state: PdfToolsUiState, viewModel: PdfToolsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TopNavigation)
                .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateToStep(PdfToolsStep.SUCCESS) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryText)
            }
            Text("Document Preview", fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 16.sp)
        }

        Box(modifier = Modifier.fillMaxSize()) {
            GridBackground()

            if (state.activeType == PdfToolType.PDF_TO_IMAGE) {
                // 12 page thumbnail grid view (Page 36, 37)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(state.imagePagesCount) { index ->
                        Column(
                            modifier = Modifier.padding(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CardBackground)
                                    .border(1.dp, BorderPurple, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, tint = MutedText)
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(4.dp)
                                        .size(16.dp)
                                        .background(AccentPurple, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text((index + 1).toString(), color = AppBackground, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Page ${index + 1}", color = MutedText, fontSize = 10.sp)
                        }
                    }
                }
            } else {
                // Document mock layout lines
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardBackground)
                        .padding(24.dp)
                ) {
                    Text(
                        "PDF DOCUMENT CANVAS",
                        color = AccentPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Canvas(modifier = Modifier.fillMaxSize().weight(1f)) {
                        val lineColor = Color(0x06C8BCFF)
                        var y = 20f
                        while (y < size.height) {
                            drawLine(
                                color = lineColor,
                                start = Offset(10f, y),
                                end = Offset(size.width - 10f, y),
                                strokeWidth = 2f
                            )
                            y += 40f
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SecurityUnlockView(state: PdfToolsUiState, viewModel: PdfToolsViewModel) {
    val context = LocalContext.current
    var attempt by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Lock, contentDescription = null, tint = AccentPurple, modifier = Modifier.size(56.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("This document is password protected", fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 16.sp)
        Text("Enter the passcode to unlock and view files", color = MutedText, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = attempt,
            onValueChange = { attempt = it; viewModel.updateUnlockPasswordAttempt(it) },
            label = { Text("Enter password") },
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = PrimaryText,
                unfocusedTextColor = PrimaryText,
                focusedBorderColor = AccentPurple,
                unfocusedBorderColor = BorderPurple
            ),
            modifier = Modifier.fillMaxWidth(0.85f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (attempt == state.passwordInput) {
                    viewModel.navigateToStep(PdfToolsStep.PREVIEW)
                } else {
                    Toast.makeText(context, "Incorrect Password! Try again.", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple, contentColor = AppBackground),
            modifier = Modifier.fillMaxWidth(0.85f).height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Unlock Document", fontWeight = FontWeight.Bold)
        }
    }
}
