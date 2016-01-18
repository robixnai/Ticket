package br.com.cast.ticket.util;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.concurrent.ExecutionException;

/**
 * Created by Ezequiel on 25/07/2015.
 */

public class MyLocation implements LocationListener {

    private LocationManager mLocationManager;
    private String mProvider;

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public String returnLocation(final Context context) {
        final Location location = getLocation(context);

        final CurrentLocationTask task = new CurrentLocationTask(context, location);
        String currentLocation = null;
        try {
            currentLocation = task.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return currentLocation;
    }

    public String getLatitude(final Context context) {
        final Location location = getLocation(context);
        return location == null ? "0" : String.valueOf(location.getLatitude());
    }

    public String getLongitude(final Context context) {
        final Location location = getLocation(context);
        return location == null ? "0" : String.valueOf(location.getLongitude());
    }

    private Location getLocation(Context context) {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        final Criteria criteria = new Criteria();
        mProvider = mLocationManager.getBestProvider(criteria, false);

        return mLocationManager.getLastKnownLocation(mProvider);
    }

}

