package com.antigravity.survival.ui.screens

import android.content.Context
import android.hardware.camera2.CameraManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antigravity.survival.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyHudScreen(
    onBackClick: () -> Unit,
    onOpenArticle: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isFlashlightOn by remember { mutableStateOf(false) }
    var isSosRunning by remember { mutableStateOf(false) }

    fun setTorch(on: Boolean) {
        try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager ?: return
            val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return
            cameraManager.setTorchMode(cameraId, on)
            isFlashlightOn = on
        } catch (_: Exception) {
            isFlashlightOn = false
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            setTorch(false)
            isSosRunning = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tactical Emergency HUD",
                        fontWeight = FontWeight.Bold,
                        color = DangerRed,
                        fontSize = 18.sp
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Flashlight & SOS Control
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Immediate Lighting & Distress Unit",
                            fontWeight = FontWeight.Bold,
                            color = HazardAmber,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Large Tactical Flashlight Button
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isFlashlightOn) TacticalGreen else DarkBorder
                                )
                                .border(
                                    4.dp,
                                    if (isFlashlightOn) TacticalGreen.copy(alpha = 0.5f) else DarkSurface,
                                    CircleShape
                                )
                                .clickable {
                                    if (isSosRunning) isSosRunning = false
                                    setTorch(!isFlashlightOn)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isFlashlightOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                                contentDescription = "Tactical Flashlight",
                                tint = if (isFlashlightOn) AmoledBlack else TextSecondary,
                                modifier = Modifier.size(52.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isFlashlightOn) "Flashlight Active (ON)" else "Flashlight Inactive (OFF)",
                            fontWeight = FontWeight.Bold,
                            color = if (isFlashlightOn) TacticalGreen else TextSecondary,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // SOS Strobe Button
                        OutlinedButton(
                            onClick = {
                                if (isSosRunning) {
                                    isSosRunning = false
                                    setTorch(false)
                                } else {
                                    isSosRunning = true
                                    coroutineScope.launch {
                                        // SOS Pattern: 3 Short, 3 Long, 3 Short
                                        while (isSosRunning) {
                                            // 3 Short
                                            repeat(3) {
                                                if (!isSosRunning) return@repeat
                                                setTorch(true)
                                                delay(200)
                                                setTorch(false)
                                                delay(200)
                                            }
                                            delay(400)
                                            // 3 Long
                                            repeat(3) {
                                                if (!isSosRunning) return@repeat
                                                setTorch(true)
                                                delay(600)
                                                setTorch(false)
                                                delay(200)
                                            }
                                            delay(400)
                                            // 3 Short
                                            repeat(3) {
                                                if (!isSosRunning) return@repeat
                                                setTorch(true)
                                                delay(200)
                                                setTorch(false)
                                                delay(200)
                                            }
                                            delay(1500)
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = DangerRed
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isSosRunning) "Stop SOS Distress Signal" else "Activate Tactical SOS Morse Beacon",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Quick Critical Action Cards
            item {
                Text(
                    text = "Immediate Field Action Protocols",
                    fontWeight = FontWeight.Bold,
                    color = DangerRed,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }

            item {
                EmergencyActionCard(
                    title = "Severe Arterial Bleeding (Tourniquet Placement)",
                    description = "Rapid tactical tourniquet application to arrest hemorrhage in <60s.",
                    icon = Icons.Default.MedicalServices,
                    onClick = { onOpenArticle("art_urban_combat_earthquake") }
                )
            }

            item {
                EmergencyActionCard(
                    title = "Nuclear Blast & Radioactive Fallout Protocol",
                    description = "Crucial 7-10 radiological decay rule and shelter shielding factors.",
                    icon = Icons.Default.Shield,
                    onClick = { onOpenArticle("art_nuclear_war") }
                )
            }

            item {
                EmergencyActionCard(
                    title = "Emergency Water Procurement & Multi-Stage Filtration",
                    description = "Bio-sand filtration, rolling boil sterilization, and chemical treatment.",
                    icon = Icons.Default.WaterDrop,
                    onClick = { onOpenArticle("art_water_purification") }
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun EmergencyActionCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DangerRed.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = DangerRed,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 14.sp
                )
                Text(
                    text = description,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = DangerRed,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
