package com.digidocx.feature.home.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digidocx.core.model.UserProfile
import com.digidocx.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onNavigateBack: () -> Unit) {
    val profile = UserProfile()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold, color = PrimaryText) },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBackground)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(AccentPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Text("VS", color = AppBackground, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(profile.name, fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 18.sp)
                    Text(profile.email, color = MutedText, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, AccentPurple, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("FREE PLAN", color = AccentPurple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ToolCardBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderPurple, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically

                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = AccentPurple)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Go Pro — Unlimited OCR & Cloud", fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 14.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Used ${profile.ocrUsedCount} of ${profile.ocrMaxCount} OCRs this month",
                        color = MutedText,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { profile.ocrUsedCount.toFloat() / profile.ocrMaxCount.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp)),
                        color = AccentPurple,
                        trackColor = BorderPurple,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "ACCOUNT",
                fontWeight = FontWeight.Bold,
                color = MutedText,
                fontSize = 11.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, bottom = 8.dp)
            )

            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CardBackground)) {
                Column {
                    ProfileMenuRow("Profile", Icons.Default.Person)
                    ProfileMenuRow("Security", Icons.Default.Security)
                    ProfileMenuRow("Settings", Icons.Default.Settings)
                    ProfileMenuRow("Notifications", Icons.Default.Notifications)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "MORE",
                fontWeight = FontWeight.Bold,
                color = MutedText,
                fontSize = 11.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, bottom = 8.dp)
            )

            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CardBackground)) {
                Column {
                    ProfileMenuRow("Help & Support", Icons.Default.Help)
                    ProfileMenuRow("Terms & Privacy", Icons.Default.Article)
                    ProfileMenuRow("Log Out", Icons.Default.ExitToApp, isDestructive = true)
                }
            }
        }
    }
}

@Composable
fun ProfileMenuRow(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isDestructive: Boolean = false) {
    ListItem(
        headlineContent = { Text(title, color = if (isDestructive) androidx.compose.ui.graphics.Color(0xFFFF5D5D) else PrimaryText, fontSize = 14.sp) },
        leadingContent = { Icon(icon, contentDescription = null, tint = if (isDestructive) androidx.compose.ui.graphics.Color(0xFFFF5D5D) else AccentPurple) },
        colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
        modifier = Modifier.clickable { /* No-op mock clicks */ }
    )
}