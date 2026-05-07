package com.eveningoutpost.dexdrip;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.core.content.ContextCompat;

import com.ustwo.clockwise.common.WatchMode;

/**
 * xDrip-style watch face for Galaxy Watch 8 (Wear OS).
 *
 * Displays:
 *  - Current BG value (large)
 *  - Trend arrow
 *  - Delta (change since last reading)
 *  - Age (minutes since last reading)
 *  - Sparkline graph of last ~3h
 *
 * Tap anywhere on the watch face to open {@link WearGlucoseActivity} for detailed view.
 *
 * Data arrives via the phone→watch DataLayer and is handled by the base class
 * {@link BaseWatchFace} / {@link ListenerService}.  No additional polling is needed
 * beyond what the base class already provides.
 */
public class XDripStyleWatchFace extends BaseWatchFace {

    private static final String TAG = XDripStyleWatchFace.class.getSimpleName();

    // -----------------------------------------------------------------------
    // Lifecycle
    // -----------------------------------------------------------------------

    @Override
    public void onCreate() {
        super.onCreate();
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        layoutView = inflater.inflate(R.layout.activity_xdrip_style, null);
        performViewSetup();
    }

    // -----------------------------------------------------------------------
    // Tap interaction – open detail activity
    // -----------------------------------------------------------------------

    @Override
    protected void onTapCommand(int tapType, int x, int y, long eventTime) {
        if (tapType == TAP_TYPE_TAP) {
            try {
                final Intent intent = new Intent(this, WearGlucoseActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Could not start WearGlucoseActivity: " + e.getMessage());
                // Fallback: open preferences
                try {
                    final Intent intent = new Intent(this, NWPreferences.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception ex) {
                    Log.e(TAG, "Could not start NWPreferences: " + ex.getMessage());
                }
            }
        }
    }

    // -----------------------------------------------------------------------
    // Color / watch-mode handling
    // -----------------------------------------------------------------------

    @Override
    protected void setColorDark() {
        try {
            mTime.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_mTime));
            mDate.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_mTime));
            mRelativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_background));
            applyBgColor(R.color.dark_highColor, R.color.dark_midColor, R.color.dark_lowColor);
            mTimestamp.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_Timestamp));
            mLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.xdrip_style_status_bg));
        } catch (NullPointerException e) {
            Log.e(TAG, "NPE in setColorDark: " + e);
        }
    }

    @Override
    protected void setColorBright() {
        try {
            mTime.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_midColor));
            mDate.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_midColor));
            mRelativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_background));
            applyBgColor(R.color.light_highColor, R.color.light_midColor, R.color.light_lowColor);
            mTimestamp.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_mTimestamp1));
            mLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_stripe_background));
        } catch (NullPointerException e) {
            Log.e(TAG, "NPE in setColorBright: " + e);
        }
    }

    @Override
    protected void setColorLowRes() {
        try {
            mTime.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.grey_300));
            mDate.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.grey_300));
            mRelativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_background));
            applyBgColor(R.color.grey_300, R.color.grey_300, R.color.grey_300);
            mTimestamp.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.grey_500));
            mLinearLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_background));
        } catch (NullPointerException e) {
            Log.e(TAG, "NPE in setColorLowRes: " + e);
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /**
     * Apply BG-level–dependent colours (high / normal / low) to the BG value,
     * trend direction and delta views.
     */
    private void applyBgColor(int highColorRes, int midColorRes, int lowColorRes) {
        final int colorRes;
        if (sgvLevel == 1) {
            colorRes = highColorRes;
        } else if (sgvLevel == -1) {
            colorRes = lowColorRes;
        } else {
            colorRes = midColorRes;
        }
        final int color = ContextCompat.getColor(getApplicationContext(), colorRes);
        mSgv.setTextColor(color);
        mDirection.setTextColor(color);
        mDelta.setTextColor(color);
    }

    @Override
    protected void onWatchModeChanged(WatchMode watchMode) {
        super.onWatchModeChanged(watchMode);
    }
}
