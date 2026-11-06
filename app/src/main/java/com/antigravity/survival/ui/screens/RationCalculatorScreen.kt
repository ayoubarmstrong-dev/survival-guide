package com.antigravity.survival.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antigravity.survival.ui.theme.*
import com.antigravity.survival.ui.viewmodel.SurvivalViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RationCalculatorScreen(
    viewModel: SurvivalViewModel,
    onBackClick: () -> Unit
) {
    val adults by viewModel.adultsCount.collectAsState()
    val children by viewModel.childrenCount.collectAsState()
    val days by viewModel.daysCount.collectAsState()
    val result by viewModel.calculationResult.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tactical Ration & Water Calculator",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Configuration Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Group Parameters & Duration",
                            fontWeight = FontWeight.Bold,
                            color = TacticalGreen,
                            fontSize = 15.sp
                        )

                        // Adults Counter
                        CounterRow(
                            title = "Adults (3.5L Water / 2,200 kcal daily):",
                            count = adults,
                            onIncrement = { viewModel.updateRationParams(adults + 1, children, days) },
                            onDecrement = { viewModel.updateRationParams(adults - 1, children, days) }
                        )

                        // Children Counter
                        CounterRow(
                            title = "Children (2.2L Water / 1,500 kcal daily):",
                            count = children,
                            onIncrement = { viewModel.updateRationParams(adults, children + 1, days) },
                            onDecrement = { viewModel.updateRationParams(adults, children - 1, days) }
                        )

                        // Days Counter
                        CounterRow(
                            title = "Operation Duration (Days):",
                            count = days,
                            onIncrement = { viewModel.updateRationParams(adults, children, days + 1) },
                            onDecrement = { viewModel.updateRationParams(adults, children, days - 1) }
                        )
                    }
                }
            }

            // High Level Totals
            item {
                Text(
                    text = "Vital Resource Requirements",
                    fontWeight = FontWeight.Bold,
                    color = HazardAmber,
                    fontSize = 16.sp
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Potable Water Reserve",
                        value = String.format(Locale.US, "%.1f", result.totalWaterLiters),
                        unit = "Liters",
                        color = TacticalCyan,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Total Caloric Target",
                        value = String.format(Locale.US, "%,.0f", result.totalCalories),
                        unit = "kcal",
                        color = DangerRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Food Rations Breakdown
            item {
                Text(
                    text = "Dry Staple Food Breakdown (in Kilograms)",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RationRow("Polished White Rice (Long-term)", result.whiteRiceKg)
                        RationRow("Dry Beans & Lentils (Protein)", result.dryBeansLentilsKg)
                        RationRow("Whole Rolled Oats (Fiber & Carbs)", result.rolledOatsKg)
                        RationRow("Vegetable Cooking Oil (Lipids)", result.cookingOilKg)
                        RationRow("Iodized / Rock Mineral Salt", result.saltKg)
                        RationRow("Pure Cane Sugar or Honey", result.sugarOrHoneyKg)

                        HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Dry Food Weight:",
                                fontWeight = FontWeight.Bold,
                                color = TacticalGreen,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "${String.format(Locale.US, "%.2f", result.totalDryFoodKg)} kg",
                                fontWeight = FontWeight.Bold,
                                color = TacticalGreen,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun CounterRow(
    title: String,
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Column {
        Text(
            text = title,
            color = TextSecondary,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "$count",
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 20.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledIconButton(
                    onClick = onDecrement,
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = DarkBorder),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease",
                        tint = TextPrimary
                    )
                }
                FilledIconButton(
                    onClick = onIncrement,
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = TacticalOlive),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        tint = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    unit: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = title,
                color = TextSecondary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = unit,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
        }
    }
}

@Composable
fun RationRow(title: String, weightKg: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 13.sp
        )
        Text(
            text = "${String.format(Locale.US, "%.2f", weightKg)} kg",
            fontWeight = FontWeight.SemiBold,
            color = HazardAmber,
            fontSize = 14.sp
        )
    }
}
