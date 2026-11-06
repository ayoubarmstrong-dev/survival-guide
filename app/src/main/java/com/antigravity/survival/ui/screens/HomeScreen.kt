package com.antigravity.survival.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antigravity.survival.data.entity.CategoryEntity
import com.antigravity.survival.ui.components.FirstLaunchDisclaimerDialog
import com.antigravity.survival.ui.components.LegalAndPrivacyDialog
import com.antigravity.survival.ui.components.hasAcceptedDisclaimer
import com.antigravity.survival.ui.components.openPrivacyPolicy
import com.antigravity.survival.ui.theme.*
import com.antigravity.survival.ui.viewmodel.SurvivalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: SurvivalViewModel,
    onCategoryClick: (String, String) -> Unit,
    onEmergencyHudClick: () -> Unit,
    onCalculatorClick: () -> Unit,
    onStoreClick: () -> Unit,
    onArticleClick: (String) -> Unit
) {
    val context = LocalContext.current
    val categories by viewModel.categories.collectAsState()
    val highPriorityArticles by viewModel.highPriorityArticles.collectAsState()

    var showFirstLaunchDisclaimer by remember { mutableStateOf(!hasAcceptedDisclaimer(context)) }
    var showLegalDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Aegis Survival",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 19.sp
                        )
                        Text(
                            text = "Field Manual & Emergency Preparedness • Offline",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface
                ),
                actions = {
                    IconButton(onClick = { showLegalDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Medical & Legal Disclaimer",
                            tint = TextSecondary
                        )
                    }
                    IconButton(onClick = onStoreClick) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Emergency Gear Store",
                            tint = TacticalGreen
                        )
                    }
                    IconButton(onClick = onCalculatorClick) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = "Ration Calculator",
                            tint = TacticalCyan
                        )
                    }
                    IconButton(onClick = onEmergencyHudClick) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Emergency HUD",
                            tint = DangerRed
                        )
                    }
                }
            )
        },
        containerColor = AmoledBlack
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(2.dp))
                // Quick HUD Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEmergencyHudClick() },
                    colors = CardDefaults.cardColors(containerColor = DangerRed.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashlightOn,
                            contentDescription = null,
                            tint = DangerRed,
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Tactical Emergency HUD",
                                fontWeight = FontWeight.Bold,
                                color = DangerRed,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Distress strobe, SOS Morse beacon, and immediate triage",
                                color = TextPrimary,
                                fontSize = 12.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            tint = DangerRed,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            item {
                // Dedicated Gear Store Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStoreClick() },
                    colors = CardDefaults.cardColors(containerColor = TacticalOlive.copy(alpha = 0.25f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = TacticalGreen,
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Emergency Gear Store",
                                fontWeight = FontWeight.Bold,
                                color = TacticalGreen,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Curated offline specs for filters, CAT tourniquets & supplies",
                                color = TextPrimary,
                                fontSize = 12.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            tint = TacticalGreen,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Categories Section
            item {
                Text(
                    text = "Tactical Readiness Modules",
                    fontWeight = FontWeight.Bold,
                    color = HazardAmber,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                )
            }

            items(categories) { category ->
                CategoryCard(
                    category = category,
                    onClick = { onCategoryClick(category.id, category.nameEn) }
                )
            }

            // High priority articles section
            if (highPriorityArticles.isNotEmpty()) {
                item {
                    Text(
                        text = "Critical Action Protocols",
                        fontWeight = FontWeight.Bold,
                        color = DangerRed,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                    )
                }

                items(highPriorityArticles) { article ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onArticleClick(article.id) },
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(DangerRed)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = article.titleEn,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = article.summaryAr,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Privacy Policy",
                        color = TacticalCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { openPrivacyPolicy(context) }
                            .padding(4.dp)
                    )
                    Text(
                        text = " • ",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Medical & Legal Disclaimer",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { showLegalDialog = true }
                            .padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (showFirstLaunchDisclaimer) {
            FirstLaunchDisclaimerDialog(
                onAccepted = { showFirstLaunchDisclaimer = false }
            )
        }

        if (showLegalDialog) {
            LegalAndPrivacyDialog(
                onDismiss = { showLegalDialog = false }
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: CategoryEntity,
    onClick: () -> Unit
) {
    val icon = when (category.iconName) {
        "ic_shield_check" -> Icons.Default.Shield
        "ic_alert_triangle" -> Icons.Default.Warning
        "ic_compass" -> Icons.Default.Explore
        "ic_anvil" -> Icons.Default.Build
        "ic_flashlight" -> Icons.Default.FlashlightOn
        else -> Icons.Default.Bookmark
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(TacticalOlive.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TacticalGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.nameEn,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 15.sp
                )
                Text(
                    text = "Offline Field Manual • Complete Protocol",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}
