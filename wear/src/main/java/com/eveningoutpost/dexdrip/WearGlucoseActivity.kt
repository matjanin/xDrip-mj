package com.eveningoutpost.dexdrip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import com.eveningoutpost.dexdrip.models.BgReading
import com.eveningoutpost.dexdrip.models.JoH
import com.eveningoutpost.dexdrip.utilitymodels.Constants
import com.eveningoutpost.dexdrip.utilitymodels.Pref
import kotlinx.coroutines.delay

/**
 * WearGlucoseActivity — Phase C
 *
 * A simple Compose for Wear OS screen that shows:
 *   - Current glucose value + trend arrow
 *   - Delta and time-since-reading
 *   - Tiny 3-hour sparkline graph
 *
 * This activity is used as the main launcher on the watch.
 */
class WearGlucoseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                WearGlucoseScreen()
            }
        }
    }
}

// ─── Composables ─────────────────────────────────────────────────────────────

@Composable
private fun WearGlucoseScreen() {
    // Refresh every 15 seconds
    var tick by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(15_000L)
            tick++
        }
    }

    val bgReading by remember(tick) { mutableStateOf(BgReading.last(true)) }
    val history by remember(tick) {
        mutableStateOf(
            BgReading.latestForGraph(
                36,
                (System.currentTimeMillis() - Constants.HOUR_IN_MS * 3)
            ) ?: emptyList()
        )
    }

    val doMgdl = Pref.getString("units", "mgdl") == "mgdl"
    val glucoseText = bgReading?.displayValue(null) ?: "---"
    val trendArrow = bgReading?.displaySlopeArrow() ?: ""
    // Use the delta name from the reading itself (already computed during ingestion)
    val deltaText = bgReading?.dg_delta_name?.takeIf { it.isNotBlank() } ?: ""
    val ageText = bgReading?.let { JoH.niceTimeSince(it.timestamp) } ?: ""
    val glucoseColor = glucoseColor(bgReading?.calculated_value ?: 0.0, doMgdl)

    Scaffold(
        timeText = {
            TimeText()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Glucose + arrow
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = glucoseText,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = glucoseColor,
                )
                if (trendArrow.isNotBlank()) {
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = trendArrow,
                        fontSize = 20.sp,
                        color = glucoseColor,
                    )
                }
            }

            // Delta + age
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (deltaText.isNotBlank()) {
                    Text(
                        text = deltaText,
                        fontSize = 13.sp,
                        color = MaterialTheme.colors.onBackground.copy(alpha = 0.8f),
                    )
                }
                if (ageText.isNotBlank()) {
                    Text(
                        text = ageText,
                        fontSize = 13.sp,
                        color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f),
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            // Sparkline
            if (history.size >= 2) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .padding(horizontal = 4.dp),
                ) {
                    WearSparkline(
                        readings = history,
                        lineColor = glucoseColor,
                    )
                }
            }
        }
    }
}

@Composable
private fun WearSparkline(readings: List<BgReading>, lineColor: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val sorted = readings.sortedBy { it.timestamp }
        val minT = sorted.first().timestamp.toFloat()
        val maxT = sorted.last().timestamp.toFloat()
        val tRange = (maxT - minT).coerceAtLeast(1f)
        val values = sorted.map { it.calculated_value.toFloat() }
        val minV = values.minOrNull() ?: 0f
        val maxV = (values.maxOrNull() ?: 1f).coerceAtLeast(minV + 1f)
        val vRange = maxV - minV

        val pts = sorted.mapIndexed { i, r ->
            val x = ((r.timestamp.toFloat() - minT) / tRange) * size.width
            val y = size.height - ((values[i] - minV) / vRange) * size.height
            Offset(x, y)
        }
        val path = Path().apply {
            moveTo(pts.first().x, pts.first().y)
            pts.drop(1).forEach { lineTo(it.x, it.y) }
        }
        drawPath(path, color = lineColor, style = Stroke(width = 2.5f))
    }
}

private fun glucoseColor(mgdl: Double, doMgdl: Boolean): Color {
    if (mgdl <= 0) return Color.Gray
    val low = if (doMgdl) 70.0 else 3.9
    val high = if (doMgdl) 180.0 else 10.0
    val v = if (doMgdl) mgdl else mgdl * Constants.MGDL_TO_MMOLL
    return when {
        v < low || v > high -> Color(0xFFEF5350)
        else -> Color(0xFF66BB6A)
    }
}
