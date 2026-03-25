package com.eveningoutpost.dexdrip.ui.minimed

import android.content.Intent
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eveningoutpost.dexdrip.Home
import com.eveningoutpost.dexdrip.R
import com.eveningoutpost.dexdrip.models.BgReading
import com.eveningoutpost.dexdrip.models.JoH
import com.eveningoutpost.dexdrip.ui.theme.MjAppTheme
import com.eveningoutpost.dexdrip.utils.DexCollectionType
import com.eveningoutpost.dexdrip.utilitymodels.BgGraphBuilder
import com.eveningoutpost.dexdrip.utilitymodels.Constants
import com.eveningoutpost.dexdrip.utilitymodels.Pref
import kotlinx.coroutines.delay

/**
 * MjHomeActivity — Phase B
 *
 * Compose / Material-3 home screen showing:
 *   - Current glucose value, trend arrow, delta, minutes-since-reading
 *   - Simple 3-hour sparkline graph
 *   - Connection / status banner
 *   - Battery-optimization prompt (Samsung Galaxy S25 / Phase D)
 */
class MjHomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MjAppTheme {
                MjHomeScreen(
                    onOpenLegacy = { openLegacyHome() },
                    onOpenStatus = { openMiniMedStatus() },
                    onOpenSettings = { openSettings() },
                )
            }
        }
    }

    private fun openLegacyHome() {
        startActivity(Intent(this, Home::class.java))
    }

    private fun openMiniMedStatus() {
        startActivity(Intent(this, MiniMedStatusActivity::class.java))
    }

    private fun openSettings() {
        startActivity(Intent(this, MjSettingsActivity::class.java))
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Composables
// ────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MjHomeScreen(
    onOpenLegacy: () -> Unit,
    onOpenStatus: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val context = LocalContext.current

    // Refresh state every 15 seconds so the UI stays live
    var tick by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(15_000L)
            tick++
        }
    }

    // Read data (recompose on tick changes)
    val bgReading by remember(tick) { mutableStateOf(BgReading.last(true)) }
    val history by remember(tick) {
        mutableStateOf(
            BgReading.latestForGraph(
                36,
                (System.currentTimeMillis() - Constants.HOUR_IN_MS * 3)
            ) ?: emptyList<BgReading>()
        )
    }
    val isUiBased by remember(tick) {
        mutableStateOf(DexCollectionType.getDexCollectionType() == DexCollectionType.UiBased)
    }
    // batteryOptimized = true means the app IS battery-optimized (not excluded = bad for background work)
    val batteryOptimized by remember(tick) {
        mutableStateOf(!isIgnoringBatteryOptimizations(context))
    }
    val doMgdl = Pref.getString("units", "mgdl") == "mgdl"

    var showBatteryDialog by remember { mutableStateOf(batteryOptimized) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.mj_home_title)) },
                actions = {
                    IconButton(onClick = onOpenStatus) {
                        Icon(Icons.Default.MonitorHeart, contentDescription = stringResource(R.string.mj_home_status_label))
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.mj_home_settings_label))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Status banner (only shown when integration not active)
            if (!isUiBased) {
                StatusBanner(
                    message = stringResource(R.string.mj_home_companion_not_active),
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                )
            }

            // Battery-opt warning banner (Phase D)
            if (batteryOptimized && showBatteryDialog) {
                StatusBanner(
                    message = "⚠\uFE0F Battery optimization active — background collection may be interrupted.",
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    onClick = {
                        context.startActivity(
                            Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                                data = android.net.Uri.parse("package:${context.packageName}")
                            }
                        )
                    },
                    actionLabel = stringResource(R.string.mj_home_battery_opt_action),
                )
            }

            // Main glucose card
            GlucoseCard(bgReading = bgReading, doMgdl = doMgdl)

            // 3-hour graph
            GraphCard(history = history, doMgdl = doMgdl)

            Spacer(Modifier.weight(1f))

            // Open full xDrip+ (legacy)
            OutlinedButton(
                onClick = onOpenLegacy,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.OpenInNew, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.mj_home_open_legacy))
            }
        }
    }

    // Battery optimisation dialog (Phase D)
    if (showBatteryDialog && batteryOptimized) {
        AlertDialog(
            onDismissRequest = { showBatteryDialog = false },
            title = { Text(stringResource(R.string.mj_home_battery_opt_title)) },
            text = { Text(stringResource(R.string.mj_home_battery_opt_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showBatteryDialog = false
                    context.startActivity(
                        Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                            data = android.net.Uri.parse("package:${context.packageName}")
                        }
                    )
                }) {
                    Text(stringResource(R.string.mj_home_battery_opt_action))
                }
            },
            dismissButton = {
                TextButton(onClick = { showBatteryDialog = false }) {
                    Text(stringResource(R.string.mj_home_battery_opt_later))
                }
            }
        )
    }
}

@Composable
private fun GlucoseCard(bgReading: BgReading?, doMgdl: Boolean) {
    val context = LocalContext.current
    val glucoseText = bgReading?.displayValue(context) ?: stringResource(R.string.mj_home_no_data)
    val trendArrow = bgReading?.displaySlopeArrow() ?: "–"
    val deltaText = if (bgReading != null) {
        BgGraphBuilder.unitizedDeltaString(true, false, true, doMgdl)
    } else "–"
    val ageText = if (bgReading != null) {
        JoH.niceTimeSince(bgReading.timestamp) + " ago"
    } else "–"

    val glucoseColor = glucoseColor(bgReading?.calculated_value ?: 0.0, doMgdl)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = glucoseText,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    color = glucoseColor,
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = trendArrow,
                    fontSize = 36.sp,
                    color = glucoseColor,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                LabeledValue(label = "Delta", value = deltaText)
                LabeledValue(label = "Updated", value = ageText)
                LabeledValue(
                    label = "Units",
                    value = if (doMgdl) "mg/dL" else "mmol/L"
                )
            }
        }
    }
}

@Composable
private fun LabeledValue(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun GraphCard(history: List<BgReading>, doMgdl: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Box(Modifier.fillMaxSize().padding(12.dp)) {
            if (history.isEmpty()) {
                Text(
                    text = stringResource(R.string.mj_home_graph_label),
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                GlucoseSparkline(readings = history, doMgdl = doMgdl)
                Text(
                    text = stringResource(R.string.mj_home_graph_label),
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.BottomEnd),
                )
            }
        }
    }
}

@Composable
private fun GlucoseSparkline(readings: List<BgReading>, doMgdl: Boolean) {
    val lineColor = MaterialTheme.colorScheme.primary
    Canvas(modifier = Modifier.fillMaxSize()) {
        if (readings.size < 2) return@Canvas
        val sorted = readings.sortedBy { it.timestamp }
        val minT = sorted.first().timestamp.toFloat()
        val maxT = sorted.last().timestamp.toFloat()
        val tRange = (maxT - minT).coerceAtLeast(1f)

        val values = sorted.map {
            if (doMgdl) it.calculated_value.toFloat() else (it.calculated_value * 0.0555f)
        }
        val minV = values.minOrNull() ?: 0f
        val maxV = (values.maxOrNull() ?: 1f).coerceAtLeast(minV + 1f)
        val vRange = maxV - minV

        val pts = sorted.mapIndexed { i, r ->
            val x = ((r.timestamp.toFloat() - minT) / tRange) * size.width
            val v = values[i]
            val y = size.height - ((v - minV) / vRange) * size.height
            Offset(x, y)
        }

        val path = Path().apply {
            moveTo(pts.first().x, pts.first().y)
            pts.drop(1).forEach { lineTo(it.x, it.y) }
        }
        drawPath(path, color = lineColor, style = Stroke(width = 3f))
        pts.forEach { drawCircle(color = lineColor, radius = 4f, center = it) }
    }
}

@Composable
private fun StatusBanner(
    message: String,
    containerColor: Color,
    contentColor: Color,
    onClick: (() -> Unit)? = null,
    actionLabel: String? = null,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = message,
                color = contentColor,
                modifier = Modifier.weight(1f),
                fontSize = 13.sp,
            )
            if (onClick != null && actionLabel != null) {
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = onClick) {
                    Text(actionLabel, color = contentColor)
                }
            }
        }
    }
}

private fun isIgnoringBatteryOptimizations(context: android.content.Context): Boolean {
    val pm = context.getSystemService(android.content.Context.POWER_SERVICE) as? PowerManager
    return pm?.isIgnoringBatteryOptimizations(context.packageName) ?: true
}

/** Returns a colour coding the glucose value: green = in-range, red = high/low, orange = near */
private fun glucoseColor(mgdl: Double, doMgdl: Boolean): Color {
    if (mgdl <= 0) return Color.Gray
    val low = if (doMgdl) 70.0 else 3.9
    val high = if (doMgdl) 180.0 else 10.0
    val warnLow = if (doMgdl) 80.0 else 4.4
    val warnHigh = if (doMgdl) 160.0 else 8.9
    val v = if (doMgdl) mgdl else mgdl * 0.0555
    return when {
        v < low || v > high -> Color(0xFFD32F2F)
        v < warnLow || v > warnHigh -> Color(0xFFF57C00)
        else -> Color(0xFF2E7D32)
    }
}
