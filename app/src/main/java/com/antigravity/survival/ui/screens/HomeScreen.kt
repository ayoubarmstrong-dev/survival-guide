package com.antigravity.survival.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antigravity.survival.R
import com.antigravity.survival.data.entity.CategoryEntity
import com.antigravity.survival.ui.components.FirstLaunchDisclaimerDialog
import com.antigravity.survival.ui.components.LegalAndPrivacyDialog
import com.antigravity.survival.ui.components.hasAcceptedDisclaimer
import com.antigravity.survival.ui.components.openPrivacyPolicy
import com.antigravity.survival.ui.theme.*
import com.antigravity.survival.ui.viewmodel.SurvivalViewModel

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
            Surface(
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 10.dp)
                ) {
                    // Top Row: Tactical Emblem + Title & Subtitle + Top Bar Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left: Insignia + Brand Title with zero crowding
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_aegis_emblem),
                                contentDescription = "Aegis Tactical Insignia",
                                tint = Color.Unspecified,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Aegis Survival",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextPrimary,
                                        fontSize = 19.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(TacticalGreen.copy(alpha = 0.15f))
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "OFFLINE",
                                            color = TacticalGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Field Manual & Emergency Preparedness • Offline",
                                    color = TextSecondary,
                                    fontSize = 11.5.sp,
                                    lineHeight = 15.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        // Right: Primary Top Actions (Compact, cleanly padded)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = onStoreClick,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Emergency Gear Store",
                                    tint = TacticalGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(
                                onClick = { showLegalDialog = true },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Medical & Legal Disclaimer",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Secondary Quick-Command Action Row (All 4 features accessible with 1 tap, zero crowding!)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickActionPill(
                            label = "Emergency HUD",
                            icon = Icons.Default.Warning,
                            accentColor = DangerRed,
                            onClick = onEmergencyHudClick
                        )
                        QuickActionPill(
                            label = "Gear Store (100)",
                            icon = Icons.Default.ShoppingCart,
                            accentColor = TacticalGreen,
                            onClick = onStoreClick
                        )
                        QuickActionPill(
                            label = "Ration Calculator",
                            icon = Icons.Default.Calculate,
                            accentColor = TacticalCyan,
                            onClick = onCalculatorClick
                        )
                        QuickActionPill(
                            label = "Doctrine & Legal",
                            icon = Icons.Default.Gavel,
                            accentColor = HazardAmber,
                            onClick = { showLegalDialog = true }
                        )
                    }
                }
            }
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

@Composable
fun QuickActionPill(
    label: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = DarkCard,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}
