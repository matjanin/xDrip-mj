package com.eveningoutpost.dexdrip.ui.minimed

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.eveningoutpost.dexdrip.R
import com.eveningoutpost.dexdrip.models.BgReading
import com.eveningoutpost.dexdrip.models.JoH
import com.eveningoutpost.dexdrip.utils.DexCollectionType
import com.eveningoutpost.dexdrip.xdrip

/**
 * MiniMed Status / Diagnostics screen.
 *
 * Shows:
 * - whether companion-app integration is active (UiBased collection type selected)
 * - notification-listener permission status
 * - last glucose reading timestamp
 * - source status summary
 */
class MiniMedStatusActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minimed_status)
        supportActionBar?.title = getString(R.string.minimed_status_title)
        refreshStatus()
    }

    override fun onResume() {
        super.onResume()
        refreshStatus()
    }

    private fun refreshStatus() {
        val enabled = DexCollectionType.getDexCollectionType() == DexCollectionType.UiBased
        val listenerGranted = isNotificationListenerEnabled()
        val lastBg = BgReading.last(true)
        val lastTimestampText = if (lastBg != null) {
            JoH.dateTimeText(lastBg.timestamp) +
                " (" + JoH.niceTimeSince(lastBg.timestamp) + " ago)"
        } else {
            getString(R.string.minimed_status_no_reading)
        }

        val integrationStatus = when {
            !enabled -> getString(R.string.minimed_status_integration_disabled)
            !listenerGranted -> getString(R.string.minimed_status_permission_missing)
            else -> getString(R.string.minimed_status_integration_active)
        }

        val tv = findViewById<android.widget.TextView>(R.id.tv_minimed_status_body)
        tv?.text = buildString {
            appendLine("⚙\u0020${getString(R.string.minimed_status_integration_label)}")
            appendLine("  $integrationStatus")
            appendLine()
            appendLine("🔔 ${getString(R.string.minimed_status_notification_label)}")
            appendLine("  ${if (listenerGranted) getString(R.string.minimed_status_granted) else getString(R.string.minimed_status_not_granted)}")
            appendLine()
            appendLine("🩸 ${getString(R.string.minimed_status_last_reading_label)}")
            appendLine("  $lastTimestampText")
            appendLine()
            appendLine("📡 ${getString(R.string.minimed_status_source_status_label)}")
            appendLine("  ${DexCollectionType.getDexCollectionType().internalName}")
        }

        val btnGrant = findViewById<android.widget.Button>(R.id.btn_grant_notification)
        btnGrant?.apply {
            visibility = if (!listenerGranted) android.view.View.VISIBLE else android.view.View.GONE
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }
        }
    }

    private fun isNotificationListenerEnabled(): Boolean {
        val pkgName = xdrip.getAppContext().packageName
        val flat = Settings.Secure.getString(
            contentResolver,
            "enabled_notification_listeners"
        )
        if (!TextUtils.isEmpty(flat)) {
            flat.split(":").forEach { name ->
                val cn = ComponentName.unflattenFromString(name)
                if (cn != null && TextUtils.equals(pkgName, cn.packageName)) return true
            }
        }
        return false
    }
}
