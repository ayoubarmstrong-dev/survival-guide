package com.antigravity.survival.ui.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.antigravity.survival.R
import com.antigravity.survival.data.entity.AffiliateGearEntity
import com.antigravity.survival.data.entity.ChecklistEntity
import com.antigravity.survival.ui.theme.*
import com.antigravity.survival.ui.viewmodel.SurvivalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    articleId: String,
    viewModel: SurvivalViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(articleId) {
        viewModel.loadArticle(articleId)
    }

    val article by viewModel.currentArticle.collectAsState()
    val checklists by viewModel.articleChecklists.collectAsState()
    val gearList by viewModel.articleGear.collectAsState()
    val checkedItems by viewModel.checkedItemIds.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = article?.titleEn ?: "Tactical Field Guide",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 17.sp,
                        maxLines = 1
                    )
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface
                )
            )
        },
        containerColor = AmoledBlack
    ) { paddingValues ->
        val current = article
        if (current == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = TacticalGreen)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header & Title
                item {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val priorityColor = when (current.priorityLevel?.uppercase()) {
                                "CRITICAL" -> DangerRed
                                "HIGH" -> HazardAmber
                                else -> TacticalGreen
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(priorityColor.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Priority Level: ${current.priorityLevel ?: "NORMAL"}",
                                    color = priorityColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Offline Cached ⚡",
                                color = TacticalGreen,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = current.titleEn,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            fontSize = 22.sp,
                            lineHeight = 28.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkCard),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = current.summaryAr,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }

                // Dedicated SVG Technical Schematic Viewer
                if (!current.diagramAsset.isNullOrBlank()) {
                    item {
                        val diagramName = current.diagramAsset
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkCard),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Technical Field Schematic (SVG):",
                                        fontWeight = FontWeight.Bold,
                                        color = TacticalCyan,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = diagramName,
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(320.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AmoledBlack)
                                        .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    NativeSvgViewer(
                                        diagramName = diagramName,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }

                // Markdown Content
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Field Operational Procedures",
                                fontWeight = FontWeight.Bold,
                                color = HazardAmber,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = current.contentMarkdownAr,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }

                // Checklists
                if (checklists.isNotEmpty()) {
                    item {
                        Text(
                            text = "Interactive Field Readiness Checklist",
                            fontWeight = FontWeight.Bold,
                            color = TacticalGreen,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(checklists) { item ->
                        val isChecked = checkedItems.contains(item.id)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.toggleChecklistItem(item.id) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isChecked) DarkSurface else DarkCard
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (isChecked) TacticalGreen else TextSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.titleAr,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isChecked) TextSecondary else TextPrimary,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = item.itemAr,
                                        color = TextSecondary,
                                        fontSize = 13.sp
                                    )
                                    if (!item.quantityFormula.isNullOrBlank()) {
                                        Text(
                                            text = "Standard: ${item.quantityFormula}",
                                            color = HazardAmber,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Affiliate Gear
                if (gearList.isNotEmpty()) {
                    item {
                        Text(
                            text = "Recommended Tactical Gear",
                            fontWeight = FontWeight.Bold,
                            color = TacticalCyan,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }

                    items(gearList) { gear ->
                        GearCard(
                            gear = gear,
                            onOpenLink = { url ->
                                openUrl(context, url)
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
}

/**
 * Ultra-reliable native SVG renderer using AndroidSVG + Canvas Picture,
 * with fallback to Coil SvgDecoder and graceful schematic card.
 */
@Composable
fun NativeSvgViewer(
    diagramName: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasError by remember(diagramName) { mutableStateOf(false) }

    // 1. Primary Render: Native AndroidSVG Picture
    val picture = remember(diagramName) {
        try {
            val svg = com.caverock.androidsvg.SVG.getFromAsset(context.assets, "diagrams/$diagramName")
            val box = svg.documentViewBox
            val width = if (svg.documentWidth > 0) svg.documentWidth else if (box != null && box.width() > 0) box.width() else 700f
            val height = if (svg.documentHeight > 0) svg.documentHeight else if (box != null && box.height() > 0) box.height() else 850f
            svg.renderToPicture(width.toInt(), height.toInt())
        } catch (e: Exception) {
            Log.w("NativeSvgViewer", "AndroidSVG failed for $diagramName, will fallback to Coil", e)
            null
        }
    }

    if (picture != null) {
        Canvas(modifier = modifier) {
            drawIntoCanvas { canvas ->
                val picWidth = picture.width.toFloat()
                val picHeight = picture.height.toFloat()
                if (picWidth > 0 && picHeight > 0) {
                    val scaleX = size.width / picWidth
                    val scaleY = size.height / picHeight
                    val scale = minOf(scaleX, scaleY)
                    val dx = (size.width - picWidth * scale) / 2f
                    val dy = (size.height - picHeight * scale) / 2f

                    canvas.nativeCanvas.save()
                    canvas.nativeCanvas.translate(dx, dy)
                    canvas.nativeCanvas.scale(scale, scale)
                    canvas.nativeCanvas.drawPicture(picture)
                    canvas.nativeCanvas.restore()
                }
            }
        }
    } else if (!hasError) {
        // 2. Secondary Render: Coil with SvgDecoder
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("file:///android_asset/diagrams/$diagramName")
                .decoderFactory(SvgDecoder.Factory(useViewBoundsAsIntrinsicSize = true))
                .crossfade(true)
                .listener(
                    onError = { _, result ->
                        Log.w("NativeSvgViewer", "Coil SVG load failed for $diagramName", result.throwable)
                        hasError = true
                    }
                )
                .build(),
            contentDescription = diagramName,
            contentScale = ContentScale.Fit,
            modifier = modifier
        )
    } else {
        // 3. Fallback View
        FallbackDiagramView(diagramName)
    }
}

@Composable
fun FallbackDiagramView(diagramName: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = null,
            tint = TacticalCyan,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tactical Schematic: $diagramName",
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Full technical specifications outlined in the procedures below.",
            color = TextSecondary,
            fontSize = 11.sp
        )
    }
}

@Composable
fun GearCard(
    gear: AffiliateGearEntity,
    onOpenLink: (String) -> Unit
) {
    val context = LocalContext.current

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Product Thumbnail with Tactical Category Fallback
                val fallbackDrawableRes = getTacticalFallbackRes(gear)
                val imageModel = gear.imageUrl ?: gear.imageAsset
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageModel)
                        .crossfade(true)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = gear.nameEn,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(DarkSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = fallbackDrawableRes),
                                contentDescription = gear.nameEn,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(DarkSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = fallbackDrawableRes),
                                contentDescription = gear.nameEn,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    },
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(76.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurface)
                        .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                        .padding(4.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = gear.nameEn,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 14.5.sp,
                            modifier = Modifier.weight(1f)
                        )
                        if (gear.estimatedPriceUsd != null) {
                            Text(
                                text = "$${String.format(java.util.Locale.US, "%.2f", gear.estimatedPriceUsd)}",
                                fontWeight = FontWeight.Bold,
                                color = TacticalGreen,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "● ${gear.priorityTier.uppercase()}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (gear.priorityTier) {
                            "Life-Critical" -> DangerRed
                            "Sustainability" -> TacticalGreen
                            else -> HazardAmber
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = gear.specSummaryAr,
                color = TextSecondary,
                fontSize = 12.5.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { onOpenLink(gear.affiliateUrlTemplate) },
                colors = ButtonDefaults.buttonColors(containerColor = TacticalOlive),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
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

private fun openUrl(context: Context, url: String) {
    try {
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    } catch (_: Exception) {
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }
}
