package com.eveningoutpost.dexdrip.ui.minimed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.eveningoutpost.dexdrip.R
import com.eveningoutpost.dexdrip.ui.theme.MjAppTheme
import com.eveningoutpost.dexdrip.utilitymodels.Pref

/**
 * MjSettingsActivity — Phase B
 *
 * Compose-based settings screen exposing only the relevant MiniMed 780G options.
 */
class MjSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MjAppTheme {
                MjSettingsScreen(onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MjSettingsScreen(onBack: () -> Unit) {
    // Read current prefs
    var usesMgdl by remember {
        mutableStateOf(Pref.getString("units", "mgdl") == "mgdl")
    }
    var highAlertsEnabled by remember {
        mutableStateOf(Pref.getBooleanDefaultFalse("bg_alert_high_active"))
    }
    var lowAlertsEnabled by remember {
        mutableStateOf(Pref.getBooleanDefaultFalse("bg_alert_low_active"))
    }
    var highThreshold by remember {
        mutableStateOf(Pref.getString("highValue", "180"))
    }
    var lowThreshold by remember {
        mutableStateOf(Pref.getString("lowValue", "70"))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.mj_settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // ── Companion App section ──────────────────────────────────────
            SettingsSectionHeader(stringResource(R.string.mj_settings_companion_section))

            SettingsRow(label = stringResource(R.string.mj_settings_data_source)) {
                Text(
                    text = "MiniMed 780G Companion App",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            HorizontalDivider()

            // ── Units section ─────────────────────────────────────────────
            SettingsSectionHeader(stringResource(R.string.mj_settings_units))

            SettingsRow(label = stringResource(R.string.mj_settings_units)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = usesMgdl,
                        onClick = {
                            usesMgdl = true
                            Pref.setString("units", "mgdl")
                        }
                    )
                    Text(
                        text = stringResource(R.string.mj_settings_units_mgdl),
                        modifier = Modifier.padding(end = 16.dp),
                    )
                    RadioButton(
                        selected = !usesMgdl,
                        onClick = {
                            usesMgdl = false
                            Pref.setString("units", "mmol")
                        }
                    )
                    Text(stringResource(R.string.mj_settings_units_mmol))
                }
            }

            HorizontalDivider()

            // ── Alerts section ────────────────────────────────────────────
            SettingsSectionHeader(stringResource(R.string.mj_settings_notifications))

            SettingsRow(label = stringResource(R.string.mj_settings_high_alert)) {
                Switch(
                    checked = highAlertsEnabled,
                    onCheckedChange = { enabled ->
                        highAlertsEnabled = enabled
                        Pref.setBoolean("bg_alert_high_active", enabled)
                    }
                )
            }

            if (highAlertsEnabled) {
                SettingsRow(label = stringResource(R.string.mj_settings_high_threshold)) {
                    OutlinedTextField(
                        value = highThreshold,
                        onValueChange = { v ->
                            highThreshold = v
                            Pref.setString("highValue", v)
                        },
                        modifier = Modifier.width(100.dp),
                        singleLine = true,
                    )
                }
            }

            SettingsRow(label = stringResource(R.string.mj_settings_low_alert)) {
                Switch(
                    checked = lowAlertsEnabled,
                    onCheckedChange = { enabled ->
                        lowAlertsEnabled = enabled
                        Pref.setBoolean("bg_alert_low_active", enabled)
                    }
                )
            }

            if (lowAlertsEnabled) {
                SettingsRow(label = stringResource(R.string.mj_settings_low_threshold)) {
                    OutlinedTextField(
                        value = lowThreshold,
                        onValueChange = { v ->
                            lowThreshold = v
                            Pref.setString("lowValue", v)
                        },
                        modifier = Modifier.width(100.dp),
                        singleLine = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
    )
}

@Composable
private fun SettingsRow(
    label: String,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        content()
    }
}
