package com.mqbcoding.stats;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class GeocodeLocationService extends Service {

    private static final String TAG = "Geocode";

    // Minimum Distance threshold in meters above which we will query geocoding (and update client)
    private static final float DISTANCE_THRESHOLD = 13f;
    // Geocoding thread sleep time in ms, which means minimum interval time between geocoding query
    // It looks like minimum is 1 second
    private static final int GEOCODING_INTERVAL = 1000;
    // Start delay in ms of geocoding thread,
    // I think it's not needed to start thread just after starting service
    private static final int GEOCODING_DELAY = 500;

    private final IBinder mBinder = new LocalBinder();
    private IGeocodeResult mListener;
    private LocationManager mLocationManager = null;
    private Geocoder geocoder;
    private Timer geocodingTimer;
    private Location mLastDecodedLocation;

    public interface IGeocodeResult {
        void onNewGeocodeResult(Address result);
    }

    class LocalBinder extends Binder {
        GeocodeLocationService getService() {
            return GeocodeLocationService.this;
        }
    }

    public GeocodeLocationService() {
        super();
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        mLastDecodedLocation = new Location(LocationManager.GPS_PROVIDER);
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        initializeTimerTask();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        geocodingTimer.cancel();
        geocodingTimer = null;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager)
                    getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private void initializeTimerTask() {
        if (geocodingTimer == null) {
            geocodingTimer = new Timer();
            geocodingTimer.scheduleAtFixedRate(geocodingTimerTask, GEOCODING_DELAY, GEOCODING_INTERVAL);
        }
    }


    // I know, that we can use requestLocationUpdates here to receive location in a event-based system
    // But problem is that on some phones locationUpdates are getting stopped at random time after start
    // With no info on logcat why actually phone stops delivering location updates
    // I couldn't find why it behaves in a such way, so i decided to go that way.
    private final TimerTask geocodingTimerTask = new TimerTask() {
        @Override
        public void run() {
            Location lastLocation = null;
            int lastAltitude = 0;
            try {
                lastLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                lastAltitude = (int) lastLocation.getAltitude();
                Log.d(TAG,"Received location: " + lastLocation);
                Log.d(TAG,"Received Altitude: " + lastAltitude);
            } catch (SecurityException ex) {
                Log.e(TAG, "Security Exception while getting last known location?");
            }
            if (lastLocation != null
                    && mLastDecodedLocation.distanceTo(lastLocation) > DISTANCE_THRESHOLD) {
                try {
                    List<Address> addresses = geocoder.getFromLocation(
                            lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
                    if (mListener != null && addresses != null && addresses.size() > 0) {
                        addresses.set(0,addresses.get(0)).setUrl(lastAltitude +" m");
                        mListener.onNewGeocodeResult(addresses.get(0));
                        mLastDecodedLocation.set(lastLocation);
                        Log.d(TAG, "Sended location to client: " + mLastDecodedLocation);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Service Not Available");
                }
            }
        }
    };

    public void setOnNewGeocodeListener(IGeocodeResult listener) {
        mListener = listener;
    }
}
