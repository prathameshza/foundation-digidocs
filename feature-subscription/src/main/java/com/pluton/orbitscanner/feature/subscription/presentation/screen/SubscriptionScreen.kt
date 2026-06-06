@file:Suppress("DEPRECATION")

package com.pluton.orbitscanner.feature.subscription.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.GTranslate
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pluton.orbitscanner.core.ui.theme.*
import com.pluton.orbitscanner.core.ui.widgets.GridBackground
import com.pluton.orbitscanner.feature.subscription.domain.model.SubscriptionPlan
import com.pluton.orbitscanner.feature.subscription.presentation.state.SubscriptionUiState
import com.pluton.orbitscanner.feature.subscription.presentation.viewmodel.SubscriptionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    onNavigateBack: () -> Unit,
    viewModel: SubscriptionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subscription", fontWeight = FontWeight.Bold, color = PrimaryText) },
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

            when (val state = uiState) {
                is SubscriptionUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AccentPurple)
                    }
                }
                is SubscriptionUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = Color.Red)
                    }
                }
                is SubscriptionUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Plan Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(AccentPurple.copy(alpha = 0.15f))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "Go Premium",
                                color = AccentPurple,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Header Title
                        Text(
                            text = "Unlock the full power\nof Smart Doc ✨",
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText,
                            fontSize = 24.sp,
                            lineHeight = 32.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Upgrade to Premium and enjoy unlimited\naccess, advanced features, and more.",
                            color = MutedText,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Features List Cards
                        FeatureBenefitsSection()

                        Spacer(modifier = Modifier.height(24.dp))

                        // Plan selector Section title
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Choose Your Plan",
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText,
                                fontSize = 16.sp
                            )
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CardBackground)
                                    .padding(2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Transparent)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Monthly", color = MutedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AccentPurple)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Yearly", color = AppBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dynamic Plan Rows
                        state.plans.forEach { plan ->
                            val isSelected = state.selectedPlanId == plan.id
                            PlanCardRow(
                                plan = plan,
                                isSelected = isSelected,
                                onSelect = { viewModel.selectPlan(plan.id) }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Security Badge Line
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = AccentPurple,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Secure payment. Cancel anytime.",
                                color = MutedText,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Continuous CTA Button
                        Button(
                            onClick = {
                                viewModel.executePurchase {
                                    Toast.makeText(context, "Premium active! Thank you for supporting atomic scaling.", Toast.LENGTH_LONG).show()
                                    onNavigateBack()
                                }
                            },
                            enabled = !state.isPurchasing,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentPurple,
                                contentColor = AppBackground,
                                disabledContainerColor = MutedText
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            if (state.isPurchasing) {
                                CircularProgressIndicator(color = AppBackground, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Continue to Payment", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // User Social Proof Ratings Card
                        TrustBadgeSection()

                        Spacer(modifier = Modifier.height(24.dp))

                        // Policy Footer Links
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Restore Purchase",
                                color = MutedText,
                                fontSize = 11.sp,
                                modifier = Modifier.clickable {
                                    Toast.makeText(context, "Purchase records restored.", Toast.LENGTH_SHORT).show()
                                }
                            )
                            Box(
                                modifier = Modifier
                                    .size(3.dp)
                                    .background(MutedText, CircleShape)
                            )
                            Text(
                                "Terms of Use",
                                color = MutedText,
                                fontSize = 11.sp,
                                modifier = Modifier.clickable {
                                    Toast.makeText(context, "Terms of services displayed.", Toast.LENGTH_SHORT).show()
                                }
                            )
                            Box(
                                modifier = Modifier
                                    .size(3.dp)
                                    .background(MutedText, CircleShape)
                            )
                            Text(
                                "Privacy Policy",
                                color = MutedText,
                                fontSize = 11.sp,
                                modifier = Modifier.clickable {
                                    Toast.makeText(context, "Privacy principles loaded.", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureBenefitsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) {
                BenefitItemCard(
                    title = "Unlimited Scans",
                    desc = "No daily limits",
                    icon = Icons.Default.QrCodeScanner
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                BenefitItemCard(
                    title = "Unlimited Translation",
                    desc = "100+ languages",
                    icon = Icons.Default.GTranslate
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) {
                BenefitItemCard(
                    title = "Advanced OCR Accuracy",
                    desc = "Extract with high precision",
                    icon = Icons.Default.Check
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                BenefitItemCard(
                    title = "Cloud Sync",
                    desc = "Access anywhere, anytime",
                    icon = Icons.Default.CloudSync
                )
            }
        }
    }
}

@Composable
fun BenefitItemCard(title: String, desc: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ToolCardBackground),
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .border(1.dp, BorderPurple, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AccentPurple.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = AccentPurple, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 12.sp)
                Text(desc, color = MutedText, fontSize = 10.sp, lineHeight = 12.sp)
            }
        }
    }
}

@Composable
fun PlanCardRow(
    plan: SubscriptionPlan,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) CardBackground else ToolCardBackground)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) AccentPurple else BorderPurple,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onSelect() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isSelected,
                        onClick = onSelect,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = AccentPurple,
                            unselectedColor = MutedText
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = plan.name,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText,
                                fontSize = 14.sp
                            )
                            if (plan.isBestValue) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AccentPurple)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        "Best Value",
                                        color = AppBackground,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Text("All Premium features", color = MutedText, fontSize = 11.sp)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = plan.priceLabel,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText,
                            fontSize = 18.sp
                        )
                        Text(
                            text = plan.billingCycleLabel,
                            color = MutedText,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                    if (plan.originalPriceLabel != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "/ year",
                                color = MutedText,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = plan.originalPriceLabel,
                                textDecoration = TextDecoration.LineThrough,
                                color = MutedText,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            if (plan.discountPercentLabel != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = AccentPurple,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Unlimited access", color = MutedText, fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = AccentPurple,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Priority support", color = MutedText, fontSize = 11.sp)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF4CAF50).copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            plan.discountPercentLabel,
                            color = Color(0xFF81C784),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrustBadgeSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackground)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Trusted by thousands of users",
            color = MutedText,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Social proof avatars
            Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                listOf(
                    Color(0xFFE91E63),
                    Color(0xFF3F51B5),
                    Color(0xFF009688),
                    Color(0xFFFF9800)
                ).forEachIndexed { index, color ->
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(1.5.dp, CardBackground, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (index == 3) {
                            Text("+9k", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "4.8/5 | 10,000+ reviews",
                    color = PrimaryText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
