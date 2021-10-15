package com.unicefwinterizationplatform.winterization_android;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.logging.Logger;

/**
 * Created by Tarek on 9/27/2014.
 */
public class LocationService extends Service implements LocationListener {

    private final IBinder lBinder = new LocationBinder();
    public static final String GPS_ACCURACY = "com.unicefwinterizationplatform.winterization_android";

    private LocationManager locationManager;

    private Location location;

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {

        //Log.d(CLAZZ, "onHandleIntent", "invoked");

        if (intent.getAction().equals("startListening")) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        else {
            if (intent.getAction().equals("stopListening")) {
                locationManager.removeUpdates(this);
                locationManager = null;
            }
        }

        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(this);
        locationManager = null;
    }


    public Location getCurrentLocation(){

        if(location != null)
        {
            return location;
        }
        return null;
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return lBinder;
    }




    public void onLocationChanged(final Location location) {
        this.location = location;
        Log.d("LOCATION","LOCATION CHANGED");
        Intent intent = new Intent(GPS_ACCURACY);
        intent.putExtra("LOCATION",location);
        sendBroadcast(intent);
    }

    public void onProviderDisabled(final String provider) {
    }

    public void onProviderEnabled(final String provider) {

        Log.d("LOCATION:", "Enabled");
    }

    public void onStatusChanged(final String arg0, final int arg1, final Bundle arg2) {


        Log.d("LOCATION:", "status:" + arg0);

    }


    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    public class LocationBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }

}