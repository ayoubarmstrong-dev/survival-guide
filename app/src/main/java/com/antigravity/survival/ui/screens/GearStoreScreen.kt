package com.antigravity.survival.ui.screens

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antigravity.survival.data.entity.AffiliateGearEntity
import com.antigravity.survival.ui.components.openPrivacyPolicy
import com.antigravity.survival.ui.theme.*
import com.antigravity.survival.ui.viewmodel.SurvivalViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GearStoreScreen(
    viewModel: SurvivalViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val gearList by viewModel.allGear.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    // Derive unique categories dynamically
    val categories = remember(gearList) {
        listOf("All Categories") + gearList.map { it.category }.distinct().sorted()
    }

    val priorities = listOf(
        "All Priorities" to null,
        "Life-Critical" to "Life-Critical",
        "Field Ops" to "Field Ops",
        "Sustainability" to "Sustainability"
    )

    // Filtered items
    val filteredGear = remember(gearList, searchQuery, selectedPriority, selectedCategory) {
        gearList.filter { gear ->
            val matchesSearch = searchQuery.isBlank() ||
                    gear.nameEn.contains(searchQuery, ignoreCase = true) ||
                    gear.specSummaryAr.contains(searchQuery, ignoreCase = true) ||
                    (gear.amazonAsin?.contains(searchQuery, ignoreCase = true) == true) ||
                    gear.category.contains(searchQuery, ignoreCase = true)

            val matchesPriority = selectedPriority == null ||
                    gear.priorityTier.equals(selectedPriority, ignoreCase = true)

            val matchesCategory = selectedCategory == null ||
                    selectedCategory == "All Categories" ||
                    gear.category.equals(selectedCategory, ignoreCase = true)

            matchesSearch && matchesPriority && matchesCategory
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = DarkCard,
                    contentColor = TextPrimary,
                    actionContentColor = TacticalGreen,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, HazardAmber, RoundedCornerShape(8.dp))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WifiOff,
                            contentDescription = null,
                            tint = HazardAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = data.visuals.message, fontSize = 13.sp)
                    }
                }
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Tactical Gear Catalog (100 Core)",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "12 Mission Tiers • Offline-Ready Field Hardware",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { openPrivacyPolicy(context) }) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Privacy Policy & Disclosure",
                            tint = TacticalCyan
                        )
                    }
                    IconButton(onClick = {
                        viewModel.syncGearRemote()
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (isOnline(context)) "Syncing 100-item catalog with remote GitHub endpoint..."
                                else "Offline: Serving cached hardware specifications"
                            )
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sync Catalog",
                            tint = TacticalGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface
                )
            )
        },
        containerColor = AmoledBlack
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Bar & Filter Header
            item {
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    placeholder = {
                        Text(
                            "Search 100 tactical items, ASIN, specs...",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TacticalOlive
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TextSecondary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkCard,
                        unfocusedContainerColor = DarkCard,
                        focusedBorderColor = TacticalGreen,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }

            // Priority Tier Filter Chips (Life-Critical, Field Ops, Sustainability)
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "MISSION PRIORITY TIER:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        priorities.forEach { (label, value) ->
                            val isSelected = selectedPriority == value
                            val pillBg = when (value) {
                                "Life-Critical" -> if (isSelected) DangerRed else DangerRed.copy(alpha = 0.15f)
                                "Field Ops" -> if (isSelected) HazardAmber else HazardAmber.copy(alpha = 0.15f)
                                "Sustainability" -> if (isSelected) TacticalGreen else TacticalGreen.copy(alpha = 0.15f)
                                else -> if (isSelected) TacticalCyan else DarkCard
                            }
                            val pillTextColor = if (isSelected) AmoledBlack else TextPrimary
                            val borderColor = when (value) {
                                "Life-Critical" -> DangerRed
                                "Field Ops" -> HazardAmber
                                "Sustainability" -> TacticalGreen
                                else -> DarkBorder
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(pillBg)
                                    .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                                    .clickable { selectedPriority = value }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = pillTextColor
                                )
                            }
                        }
                    }
                }
            }

            // Category Chips Row (12 Mission-Critical Tiers)
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "12 MISSION CATEGORIES:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { catName ->
                            val isSelected = (selectedCategory == catName) || (selectedCategory == null && catName == "All Categories")
                            val chipBg = if (isSelected) TacticalOlive else DarkCard
                            val chipTextColor = if (isSelected) TextPrimary else TextSecondary

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(chipBg)
                                    .border(1.dp, if (isSelected) TacticalGreen else DarkBorder, RoundedCornerShape(6.dp))
                                    .clickable {
                                        selectedCategory = if (catName == "All Categories") null else catName
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = catName,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = chipTextColor
                                )
                            }
                        }
                    }
                }
            }

            // Results count badge
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Showing ${filteredGear.size} of ${gearList.size} Tactical Items",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    if (selectedPriority != null || selectedCategory != null || searchQuery.isNotBlank()) {
                        Text(
                            text = "Reset Filters",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TacticalGreen,
                            modifier = Modifier.clickable {
                                selectedPriority = null
                                selectedCategory = null
                                searchQuery = ""
                            }
                        )
                    }
                }
            }

            // Empty state or list
            if (filteredGear.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No survival gear matches the current filters",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredGear, key = { it.id }) { gear ->
                    EnhancedProductCard(
                        gear = gear,
                        onOrderClick = { url ->
                            if (isOnline(context)) {
                                openStoreUrl(context, url)
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Saved for later: Internet connection required to purchase"
                                    )
                                }
                            }
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun EnhancedProductCard(
    gear: AffiliateGearEntity,
    onOrderClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(10.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Top Row: Category Pill & Priority Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Pill
                Text(
                    text = gear.category.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TacticalCyan,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(TacticalCyan.copy(alpha = 0.12f))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                )

                // Priority Badge
                val (prioBg, prioColor) = when (gear.priorityTier) {
                    "Life-Critical" -> DangerRed.copy(alpha = 0.2f) to DangerRed
                    "Sustainability" -> TacticalGreen.copy(alpha = 0.2f) to TacticalGreen
                    else -> HazardAmber.copy(alpha = 0.2f) to HazardAmber
                }

                Text(
                    text = "● ${gear.priorityTier.uppercase()}",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = prioColor,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(prioBg)
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Name and Price Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = gear.nameEn,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                if (gear.estimatedPriceUsd != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(TacticalGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$${String.format(java.util.Locale.US, "%.2f", gear.estimatedPriceUsd)}",
                            fontWeight = FontWeight.ExtraBold,
                            color = TacticalGreen,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Rating Stars & ASIN
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(gear.importanceRating.coerceIn(1, 5)) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = HazardAmber,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${gear.importanceRating}.0)",
                        fontSize = 11.sp,
                        color = HazardAmber,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (!gear.amazonAsin.isNullOrBlank()) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "ASIN: ${gear.amazonAsin}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Spec Summary
            Text(
                text = gear.specSummaryAr,
                color = TextSecondary,
                fontSize = 12.5.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Button: Order on Amazon
            Button(
                onClick = { onOrderClick(gear.affiliateUrlTemplate) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TacticalOlive,
                    contentColor = TextPrimary
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Check Price on Amazon",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun isOnline(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
    val network = cm.activeNetwork ?: return false
    val caps = cm.getNetworkCapabilities(network) ?: return false
    return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

private fun openStoreUrl(context: Context, url: String) {
    try {
        val uri = Uri.parse(url)
        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
        customTabsIntent.launchUrl(context, uri)
    } catch (e: Exception) {
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }
}
