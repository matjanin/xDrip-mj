package com.eveningoutpost.dexdrip;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.eveningoutpost.dexdrip.models.BgReading;
import com.eveningoutpost.dexdrip.utilitymodels.Pref;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A lightweight Wear OS activity that surfaces the most recent glucose reading
 * and basic status information when the user taps the xDrip-style watch face.
 *
 * <p>Data is read directly from the on-watch {@link BgReading} database which is
 * kept up-to-date by {@link ListenerService} via the phone-to-watch Data Layer.</p>
 *
 * <p>Tap anywhere on this screen to dismiss.</p>
 */
public class WearGlucoseActivity extends Activity {

    private static final String TAG = WearGlucoseActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context ctx = this;

        // ---- Fetch the latest reading from the local watch DB ---------------
        BgReading latest = null;
        try {
            latest = BgReading.last();
        } catch (Exception e) {
            // DB may not be ready on first launch; leave latest == null
        }

        // ---- Build the UI programmatically (no layout XML needed) -----------
        final ScrollView scroll = new ScrollView(ctx);
        scroll.setBackgroundColor(Color.BLACK);

        final LinearLayout container = new LinearLayout(ctx);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER_HORIZONTAL);
        container.setPadding(dp(16), dp(28), dp(16), dp(16));

        if (latest != null) {
            // BG value + trend arrow
            final String sgv   = latest.displayValue(ctx);
            final String arrow = latest.slopeArrow();
            container.addView(makeBigText(sgv + " " + arrow, colorForReading(latest), 50));

            // Delta (approximate from last two readings)
            final String deltaStr = computeDeltaString();
            if (!TextUtils.isEmpty(deltaStr)) {
                container.addView(makeText(deltaStr, Color.WHITE, 18));
            }

            // Age of reading
            final long ageMs  = System.currentTimeMillis() - latest.timestamp;
            final int  minAgo = (int) (ageMs / 60_000L);
            final String ageStr = minAgo == 1
                    ? "1 minute ago"
                    : minAgo + " minutes ago";
            container.addView(makeText(ageStr, Color.LTGRAY, 13));

        } else {
            container.addView(makeBigText("--", Color.WHITE, 50));
            container.addView(makeText("No data yet", Color.GRAY, 14));
        }

        // Current watch time
        final String now = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        final TextView tvTime = makeText(now, Color.DKGRAY, 12);
        tvTime.setPadding(0, dp(16), 0, 0);
        container.addView(tvTime);

        // Dismiss hint
        final TextView tvHint = makeText("Tap to close", Color.DKGRAY, 11);
        tvHint.setPadding(0, dp(6), 0, 0);
        container.addView(tvHint);

        scroll.addView(container);
        setContentView(scroll);

        // Tap anywhere → close
        scroll.setOnClickListener(v -> finish());
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /** Compute a simple delta string from the last two readings. */
    private String computeDeltaString() {
        try {
            final List<BgReading> last2 = BgReading.latest(2);
            if (last2 == null || last2.size() < 2) return "";
            final double d = last2.get(0).getDg_mgdl() - last2.get(1).getDg_mgdl();
            final boolean mgdl = Pref.getString("units", "mgdl").equals("mgdl");
            if (mgdl) {
                return String.format(Locale.US, "%+.0f mg/dL", d);
            } else {
                return String.format(Locale.US, "%+.1f mmol/L", d / 18.0);
            }
        } catch (Exception e) {
            return "";
        }
    }

    /** Choose a colour based on whether the reading is high / normal / low. */
    private int colorForReading(BgReading reading) {
        try {
            final double highMark = Pref.getStringToDouble("highValue",  180);
            final double lowMark  = Pref.getStringToDouble("lowValue",   80);
            final double value    = reading.getDg_mgdl();
            if (value >= highMark) return Color.YELLOW;
            if (value <= lowMark)  return Color.RED;
        } catch (Exception ignored) { }
        return Color.WHITE;
    }

    private int dp(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private TextView makeBigText(String text, int color, int sizeSp) {
        final TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(color);
        tv.setTextSize(sizeSp);
        tv.setGravity(Gravity.CENTER);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        return tv;
    }

    private TextView makeText(String text, int color, int sizeSp) {
        final TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(color);
        tv.setTextSize(sizeSp);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }
}

