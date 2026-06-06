@file:Suppress("DEPRECATION")

package com.pluton.orbitscanner.feature.aiocr.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pluton.orbitscanner.core.ui.theme.*
import com.pluton.orbitscanner.core.ui.widgets.GridBackground
import com.pluton.orbitscanner.feature.aiocr.domain.model.AiOcrType
import com.pluton.orbitscanner.feature.aiocr.presentation.state.AiOcrStep
import com.pluton.orbitscanner.feature.aiocr.presentation.state.AiOcrUiState
import com.pluton.orbitscanner.feature.aiocr.presentation.viewmodel.AiOcrViewModel

@Composable
fun AiOcrScreen(
    toolName: String,
    onNavigateBack: () -> Unit,
    viewModel: AiOcrViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(toolName) {
        viewModel.initializeFeature(toolName)
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
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

            when (uiState.step) {
                AiOcrStep.INTRO -> IntroOnboardingView(uiState, viewModel)
                AiOcrStep.SETTINGS -> SettingsConfigView(uiState, viewModel)
                AiOcrStep.PROCESSING -> ProcessingAnimationView(uiState, viewModel)
                AiOcrStep.SUCCESS -> SuccessCompletedView(uiState, viewModel, onNavigateBack)
                AiOcrStep.DETAIL_PREVIEW -> FullDetailPreviewView(uiState, viewModel)
            }
        }
    }
}

@Composable
fun IntroOnboardingView(state: AiOcrUiState, viewModel: AiOcrViewModel) {
    val title = when (state.featureType) {
        AiOcrType.TEXT_OCR -> "Extract Text (OCR)"
        AiOcrType.LAYOUT_OCR -> "Layout OCR"
        AiOcrType.ENHANCE_DOC -> "Enhance Document"
        AiOcrType.EXTRACT_TABLES -> "Extract Tables"
    }

    val desc = when (state.featureType) {
        AiOcrType.TEXT_OCR -> "Extracts all text from images using advanced AI algorithms and converts them into editable text paragraphs."
        AiOcrType.LAYOUT_OCR -> "Recreate documents, preserving columns, formatting, and structural boundaries flawlessly."
        AiOcrType.ENHANCE_DOC -> "Optimizes scan quality, corrects curves, sharpens blur, and enhances contrast instantly."
        AiOcrType.EXTRACT_TABLES -> "Instantly convert structured table scans into clean editable excel sheets."
    }

    val benefits = when (state.featureType) {
        AiOcrType.TEXT_OCR -> listOf(
            "High accuracy text recognition" to Icons.Default.Verified,
            "Auto-detect multiple languages" to Icons.Default.Translate,
            "Fast and safe local scanning" to Icons.Default.OfflineBolt,
            "Your files always remain secure" to Icons.Default.Lock
        )
        AiOcrType.LAYOUT_OCR -> listOf(
            "Detect layout with high precision" to Icons.Default.AspectRatio,
            "Keep tables and columns intact" to Icons.Default.TableChart,
            "Recreate formatting structure" to Icons.Default.WrapText,
            "Save directly as DOCX files" to Icons.Default.Description
        )
        AiOcrType.ENHANCE_DOC -> listOf(
            "Sharpen low-quality text" to Icons.Default.AutoAwesome,
            "Remove background grain and noise" to Icons.Default.FilterVintage,
            "Straighten tilted documents (deskew)" to Icons.Default.CropRotate,
            "Optimal high contrast filters" to Icons.Default.LightMode
        )
        AiOcrType.EXTRACT_TABLES -> listOf(
            "Recognize structural tables" to Icons.Default.GridOn,
            "Preserve cells and spacing" to Icons.Default.CalendarViewMonth,
            "Convert to XLSX worksheets" to Icons.Default.TrendingUp,
            "Fast structured scanning" to Icons.Default.OfflineBolt
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(AccentPurple.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (state.featureType) {
                        AiOcrType.TEXT_OCR -> Icons.Default.Translate
                        AiOcrType.LAYOUT_OCR -> Icons.Default.TableChart
                        AiOcrType.ENHANCE_DOC -> Icons.Default.AutoAwesome
                        AiOcrType.EXTRACT_TABLES -> Icons.Default.GridOn
                    },
                    contentDescription = null,
                    tint = AccentPurple,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(desc, color = MutedText, fontSize = 13.sp, textAlign = TextAlign.Center, lineHeight = 18.sp)
            Spacer(modifier = Modifier.height(32.dp))

            benefits.forEach { (label, icon) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardBackground)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AccentPurple.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = AccentPurple, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(label, color = PrimaryText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Button(
            onClick = { viewModel.navigateToStep(AiOcrStep.SETTINGS) },
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentPurple,
                contentColor = AppBackground
            ),
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Continue", fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsConfigView(state: AiOcrUiState, viewModel: AiOcrViewModel) {
    val context = LocalContext.current
    var showLangSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Output Options", fontWeight = FontWeight.Bold, color = AccentPurple, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBackground)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Description, contentDescription = null, tint = AccentPurple, modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Invoice_May_2024.pdf", fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 14.sp)
                    Text("2.4 MB • 3 pages", color = MutedText, fontSize = 12.sp)
                }
                Text(
                    "Change",
                    color = AccentPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        Toast.makeText(context, "Opening file picker...", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Configurations", fontWeight = FontWeight.Bold, color = AccentPurple, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBackground)
                    .clickable {
                        Toast.makeText(context, "Alternative formats loaded.", Toast.LENGTH_SHORT).show()
                    }
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Output Format", color = MutedText, fontSize = 11.sp)
                    Text(state.selectedFormat, color = PrimaryText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = MutedText)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBackground)
                    .clickable { showLangSheet = true }
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Document Language", color = MutedText, fontSize = 11.sp)
                    Text(state.selectedLanguage, color = PrimaryText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = MutedText)
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (state.featureType) {
                AiOcrType.TEXT_OCR -> {
                    ToggleRow(
                        title = "Improve accuracy",
                        desc = "Performs slower deep recognition cycles",
                        checked = state.isToggledOn1,
                        onCheckedChange = { viewModel.toggleSwitch1(it) }
                    )
                }
                AiOcrType.LAYOUT_OCR -> {
                    ToggleRow(
                        title = "Detect Tables",
                        desc = "Preserves tables and row alignments",
                        checked = state.isToggledOn1,
                        onCheckedChange = { viewModel.toggleSwitch1(it) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ToggleRow(
                        title = "Keep Images",
                        desc = "Extracts and scales original graphics",
                        checked = state.isToggledOn2,
                        onCheckedChange = { viewModel.toggleSwitch2(it) }
                    )
                }
                AiOcrType.ENHANCE_DOC -> {
                    ToggleRow(
                        title = "Improve Text Clarity",
                        desc = "Bolds thin lines for legibility",
                        checked = state.isToggledOn1,
                        onCheckedChange = { viewModel.toggleSwitch1(it) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ToggleRow(
                        title = "Remove noise",
                        desc = "Reduces grain and scans shadow lines",
                        checked = state.isToggledOn2,
                        onCheckedChange = { viewModel.toggleSwitch2(it) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ToggleRow(
                        title = "Deskew Document",
                        desc = "Straightens slightly tilted rotations",
                        checked = state.isToggledOn3,
                        onCheckedChange = { viewModel.toggleSwitch3(it) }
                    )
                }
                AiOcrType.EXTRACT_TABLES -> {
                    ToggleRow(
                        title = "Keep grid boundaries",
                        desc = "Adds standard sheet margins",
                        checked = state.isToggledOn1,
                        onCheckedChange = { viewModel.toggleSwitch1(it) }
                    )
                }
            }
        }

        Button(
            onClick = {
                viewModel.simulateProgress {
                    viewModel.navigateToStep(AiOcrStep.SUCCESS)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentPurple,
                contentColor = AppBackground
            ),
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Process Document", fontWeight = FontWeight.Bold)
        }
    }

    if (showLangSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLangSheet = false },
            containerColor = TopNavigation
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                Text("Select Language", fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 16.sp, modifier = Modifier.padding(bottom = 12.dp))
                listOf("English (Default)", "Spanish (Español)", "German (Deutsch)", "French (Français)").forEach { lang ->
                    ListItem(
                        headlineContent = { Text(lang, color = PrimaryText) },
                        modifier = Modifier.clickable {
                            viewModel.updateLanguage(lang)
                            showLangSheet = false
                        },
                        colors = ListItemDefaults.colors(containerColor = if (state.selectedLanguage == lang) CardBackground else Color.Transparent)
                    )
                }
            }
        }
    }
}

@Composable
fun ToggleRow(title: String, desc: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackground)
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 13.sp)
            Text(desc, color = MutedText, fontSize = 10.sp, lineHeight = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = AppBackground,
                checkedTrackColor = AccentPurple,
                uncheckedThumbColor = MutedText,
                uncheckedTrackColor = ToolCardBackground
            )
        )
    }
}

@Composable
fun ProcessingAnimationView(state: AiOcrUiState, viewModel: AiOcrViewModel) {
    val tasks = when (state.featureType) {
        AiOcrType.TEXT_OCR -> listOf("Analyzing document text", "Extracting characters", "Applying corrections", "Finalizing plain text")
        AiOcrType.LAYOUT_OCR -> listOf("Analyzing margins", "Detecting structure blocks", "Formatting paragraphs", "Creating document layers")
        AiOcrType.ENHANCE_DOC -> listOf("Deskewing corners", "Applying filters", "Sharpening text clarity", "Compiling high-quality PDF")
        AiOcrType.EXTRACT_TABLES -> listOf("Identifying grids", "Isolating columns", "Converting cells to spreadsheets", "Writing workbooks")
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
        Text("Processing... ${state.progressPercent.toInt()}%", fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .padding(16.dp)
        ) {
            tasks.forEachIndexed { index, task ->
                val isCompleted = index < state.activeTaskIndex
                val isActive = index == state.activeTaskIndex
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.CheckCircle else if (isActive) Icons.Default.HourglassEmpty else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (isCompleted) AccentPurple else if (isActive) AccentPurple.copy(alpha = 0.5f) else MutedText,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        task,
                        color = if (isCompleted) PrimaryText else if (isActive) PrimaryText.copy(alpha = 0.6f) else MutedText,
                        fontSize = 12.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun SuccessCompletedView(state: AiOcrUiState, viewModel: AiOcrViewModel, onNavigateBack: () -> Unit) {
    val context = LocalContext.current

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
            Spacer(modifier = Modifier.height(16.dp))
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
            Text("Extraction Completed!", fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            when (state.featureType) {
                AiOcrType.TEXT_OCR -> {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.5f)
                    ) {
                        LazyColumn(modifier = Modifier.padding(12.dp)) {
                            item {
                                Text(state.extractedText, color = PrimaryText, fontSize = 11.sp, lineHeight = 16.sp)
                            }
                        }
                    }
                }
                AiOcrType.LAYOUT_OCR, AiOcrType.EXTRACT_TABLES -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardBackground)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (state.featureType == AiOcrType.LAYOUT_OCR) Icons.Default.Description else Icons.Default.GridOn,
                            contentDescription = null,
                            tint = AccentPurple,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            val ext = if (state.featureType == AiOcrType.LAYOUT_OCR) "docx" else "xlsx"
                            Text("${state.outputFileName}.$ext", fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 14.sp)
                            Text("1.2 MB • Ready to save", color = MutedText, fontSize = 12.sp)
                        }
                    }
                }
                AiOcrType.ENHANCE_DOC -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.6f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(CardBackground)
                                .padding(2.dp)
                        ) {
                            listOf("Before", "After").forEach { tab ->
                                val isActive = state.activeDetailTab == tab
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isActive) AccentPurple else Color.Transparent)
                                        .clickable { viewModel.updateDetailTab(tab) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(tab, color = if (isActive) AppBackground else MutedText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CardBackground)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawRect(color = BorderPurple, size = size, style = androidx.compose.ui.graphics.drawscope.Stroke(2f))
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (state.activeDetailTab == "After") "HIGH-QUALITY ENHANCED TEXT" else "original blurry scanned lines",
                                    color = if (state.activeDetailTab == "After") PrimaryText else MutedText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "This view illustrates the contrast comparisons as requested in the design roadmap.",
                                    color = MutedText,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            if (state.featureType == AiOcrType.TEXT_OCR) {
                Button(
                    onClick = { viewModel.navigateToStep(AiOcrStep.DETAIL_PREVIEW) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CardBackground,
                        contentColor = PrimaryText
                    ),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Extracted Text")
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Button(
                onClick = {
                    viewModel.saveOutputDocument {
                        Toast.makeText(context, "Saved directly to device files!", Toast.LENGTH_SHORT).show()
                        onNavigateBack()
                    }
                },
                enabled = !state.isSaving,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentPurple,
                    contentColor = AppBackground,
                    disabledContainerColor = MutedText
                ),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(color = AppBackground, modifier = Modifier.size(24.dp))
                } else {
                    Text("Save to Device", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FullDetailPreviewView(state: AiOcrUiState, viewModel: AiOcrViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackground)
                .padding(16.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Text(
                        "PAGE ${state.detailPageIndex} OF 4",
                        color = AccentPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(state.extractedText, color = PrimaryText, fontSize = 11.sp, lineHeight = 16.sp)
                }
            }
        }

        // Horizontal index indicators representing pages (reduced sizes by 30-40% as requested on Page 67)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(TopNavigation)
                .padding(16.dp)
        ) {
            Text("Document Pages navigator", color = MutedText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                itemsIndexed(listOf(1, 2, 3, 4)) { index, pageNum ->
                    val isSelected = state.detailPageIndex == pageNum
                    // Card thumbnails sized smaller as requested on Page 67
                    Box(
                        modifier = Modifier
                            .size(36.dp) // Sized compactly to optimize layout
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) AccentPurple.copy(alpha = 0.2f) else CardBackground)
                            .border(
                                1.5.dp,
                                if (isSelected) AccentPurple else BorderPurple,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { viewModel.updatePageIndex(pageNum) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(pageNum.toString(), color = PrimaryText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { viewModel.navigateToStep(AiOcrStep.SUCCESS) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentPurple,
                    contentColor = AppBackground
                ),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Return to Options", fontWeight = FontWeight.Bold)
            }
        }
    }
}
