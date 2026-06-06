package com.pluton.orbitscanner.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pluton.orbitscanner.core.ui.theme.AccentPurple
import com.pluton.orbitscanner.core.ui.theme.AppBackground
import com.pluton.orbitscanner.core.ui.theme.MutedText
import com.pluton.orbitscanner.core.ui.widgets.GridBackground
import com.pluton.orbitscanner.feature.home.presentation.screen.FolderDetailScreen
import com.pluton.orbitscanner.feature.home.presentation.screen.HomeScreen
import com.pluton.orbitscanner.feature.home.presentation.screen.ProfileScreen
import com.pluton.orbitscanner.feature.home.presentation.screen.SearchScreen
import com.pluton.orbitscanner.feature.subscription.presentation.screen.SubscriptionScreen
import com.pluton.orbitscanner.feature.scanner.presentation.screen.ScannerScreen
import com.pluton.orbitscanner.feature.aiocr.presentation.screen.AiOcrScreen
import com.pluton.orbitscanner.feature.pdftools.presentation.screen.PdfToolsScreen
import com.pluton.orbitscanner.feature.editor.presentation.screen.EditorScreen

sealed interface Screen {
    object Home : Screen
    object Search : Screen
    object Profile : Screen
    object Subscription : Screen
    object Scanner : Screen
    data class AiOcr(val toolName: String) : Screen
    data class PdfTools(val toolName: String) : Screen
    data class Editor(val documentId: String) : Screen
    data class FolderDetail(val folderId: String) : Screen
    data class FutureFeaturePlaceholder(val featureName: String) : Screen
}

@Composable
fun AppNavHost() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    val backStack = remember { mutableListOf<Screen>() }

    val navigateTo: (Screen) -> Unit = { screen ->
        backStack.add(currentScreen)
        currentScreen = screen
    }

    val navigateBack: () -> Unit = {
        if (backStack.isNotEmpty()) {
            currentScreen = backStack.removeAt(backStack.size - 1)
        }
    }

    when (val screen = currentScreen) {
        is Screen.Home -> {
            HomeScreen(
                onNavigateToSearch = { navigateTo(Screen.Search) },
                onNavigateToProfile = { navigateTo(Screen.Profile) },
                onNavigateToFolder = { folderId -> navigateTo(Screen.FolderDetail(folderId)) },
                onNavigateToTool = { toolName -> 
                    when (toolName) {
                        "Camera Scan" -> navigateTo(Screen.Scanner)
                        "Extract Text (OCR)", "Layout OCR", "Enhance Doc", "Extract Tables" -> navigateTo(Screen.AiOcr(toolName))
                        "Merge PDF", "eSign PDF", "Protect PDF", "Image to PDF", "Compress PDF", "PDF to Image" -> navigateTo(Screen.PdfTools(toolName))
                        else -> navigateTo(Screen.FutureFeaturePlaceholder(toolName))
                    }
                }
            )
        }
        is Screen.Search -> {
            SearchScreen(
                onNavigateBack = navigateBack,
                onNavigateToFolder = { folderId -> navigateTo(Screen.FolderDetail(folderId)) }
            )
        }
        is Screen.Profile -> {
            ProfileScreen(
                onNavigateBack = navigateBack,
                onNavigateToSubscription = { navigateTo(Screen.Subscription) }
            )
        }
        is Screen.Subscription -> {
            SubscriptionScreen(
                onNavigateBack = navigateBack
            )
        }
        is Screen.Scanner -> {
            ScannerScreen(
                onNavigateBack = navigateBack,
                onNavigateToEditor = { documentId -> navigateTo(Screen.Editor(documentId)) }
            )
        }
        is Screen.AiOcr -> {
            AiOcrScreen(
                toolName = screen.toolName,
                onNavigateBack = navigateBack
            )
        }
        is Screen.PdfTools -> {
            PdfToolsScreen(
                toolName = screen.toolName,
                onNavigateBack = navigateBack
            )
        }
        is Screen.Editor -> {
            EditorScreen(
                documentId = screen.documentId,
                onNavigateBack = navigateBack
            )
        }
        is Screen.FolderDetail -> {
            FolderDetailScreen(
                folderId = screen.folderId,
                onNavigateBack = navigateBack
            )
        }
        is Screen.FutureFeaturePlaceholder -> {
            FutureFeaturePlaceholderScreen(
                featureName = screen.featureName,
                onNavigateBack = navigateBack
            )
        }
    }
}

@Composable
fun FutureFeaturePlaceholderScreen(featureName: String, onNavigateBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        GridBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$featureName Module",
                style = MaterialTheme.typography.headlineMedium,
                color = AccentPurple,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = "This screen belongs to a separate feature module (e.g. :feature-scanner, :feature-aiocr, :feature-pdftools, etc.).\n\nIt is decoupled from :feature-home and will be linked dynamically when implementing future roadmap sprints.",
                style = MaterialTheme.typography.bodyLarge,
                color = MutedText,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Button(
                onClick = onNavigateBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentPurple,
                    contentColor = AppBackground
                )
            ) {
                Text("Return to Home")
            }
        }
    }
}