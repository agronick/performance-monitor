package com.mqbcoding.stats;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.github.martoreto.aauto.vex.CarStatsClient;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class WheelStateMonitor implements CarStatsClient.Listener {
    public static final float WHEEL_DRIVING_THRESHOLD_KPH = 10.0f;
    public static final float WHEEL_DRIVING_THRESHOLD_ACCEL_POS = 0.3f;
    public static final float WHEEL_CENTER_THRESHOLD_DEG = 45.0f;

    public static final String PREF_ENABLED = "wheelStateMonitoringActive";

    public enum WheelState {
        WHEEL_UNKNOWN,
        WHEEL_DRIVING,
        WHEEL_CENTER,
        WHEEL_LEFT,
        WHEEL_RIGHT
    }

    private boolean mIsEnabled;
    private WheelState mWheelState = WheelState.WHEEL_UNKNOWN;
    private final Map<String, Object> mLastMeasurements = new HashMap<>();
    private final Handler mHandler;
    private final CarNotificationSoundPlayer mNotificationPlayer;

    public WheelStateMonitor(Context context, Handler handler) {
        mHandler = handler;
        mNotificationPlayer = new CarNotificationSoundPlayer(context, R.raw.beep);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.registerOnSharedPreferenceChangeListener(mPreferencesListener);
        readPreferences(sharedPreferences);
    }

    private void readPreferences(SharedPreferences preferences) {
        mIsEnabled = preferences.getBoolean(PREF_ENABLED, true);
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener mPreferencesListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            readPreferences(sharedPreferences);
        }
    };

    private void postUpdate() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                doUpdate();
            }
        });
    }

    @Override
    public void onNewMeasurements(String provider, Date timestamp, Map<String, Object> values) {
        mLastMeasurements.putAll(values);
        postUpdate();
    }

    @Override
    public void onSchemaChanged() {
        // do nothing
    }

    private void doUpdate() {
        Float lastSpeed = (Float) mLastMeasurements.get("vehicleSpeed");
        String speedUnit = (String) mLastMeasurements.get("vehicleSpeed.unit");
        if (lastSpeed != null && speedUnit != null) {
            if (speedUnit.equals("mph")) {
                lastSpeed *= 1.60934f;
            }
        }
        Float lastSpeedKmh = lastSpeed;
        Float accelPos = (Float) mLastMeasurements.get("acceleratorPosition");
        Float currentWheelAngle = (Float) mLastMeasurements.get("wheelAngle");
        Boolean reverseGear = (Boolean) mLastMeasurements.get("reverseGear.engaged");

        if (lastSpeedKmh == null || currentWheelAngle == null) {
            mWheelState = WheelState.WHEEL_UNKNOWN;
        } else {
            if (mWheelState == WheelState.WHEEL_UNKNOWN ||
                    lastSpeedKmh > WHEEL_DRIVING_THRESHOLD_KPH ||
                    (accelPos != null && accelPos > WHEEL_DRIVING_THRESHOLD_ACCEL_POS)) {
                mWheelState = WheelState.WHEEL_DRIVING;
            } else if (mWheelState == WheelState.WHEEL_DRIVING && reverseGear != null && reverseGear) {
                mWheelState = WheelState.WHEEL_CENTER;
            } else if ((mWheelState == WheelState.WHEEL_RIGHT && currentWheelAngle < 0)
                    || (mWheelState == WheelState.WHEEL_LEFT && currentWheelAngle > 0)) {
                beepWheelState();
                mWheelState = WheelState.WHEEL_CENTER;
            }
            if ((mWheelState == WheelState.WHEEL_CENTER || mWheelState == WheelState.WHEEL_RIGHT)
                    && currentWheelAngle < -WHEEL_CENTER_THRESHOLD_DEG) {
                mWheelState = WheelState.WHEEL_LEFT;
            } else if ((mWheelState == WheelState.WHEEL_CENTER || mWheelState == WheelState.WHEEL_LEFT)
                    && currentWheelAngle > WHEEL_CENTER_THRESHOLD_DEG) {
                mWheelState = WheelState.WHEEL_RIGHT;
            }
        }
    }

    public WheelState getWheelState() {
        return mWheelState;
    }

    private void beepWheelState() {
        if (mIsEnabled) {
            mNotificationPlayer.play();
        }
    }
}
