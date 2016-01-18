package br.com.cast.ticket.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

/**
 * Manages the search of the current state of the user.
 * @author Ezequiel
 */

public class CurrentLocationTask extends AsyncTask<Integer, Double, String> {

    Context mContext;
    Location mLocation;
    ProgressDialog mProgress;

    public CurrentLocationTask(Context context, Location location) {
        mContext = context;
        mLocation = location;
    }

    @Override
    protected void onPreExecute() {
        mProgress = ProgressDialog.show(mContext, "Waiting...", "Searching Data", true, true);
    }

    @Override
    protected String doInBackground(Integer... params) {
        String currentLocation = "Acre";
        if (AppUtil.isNetworkAvailable(mContext)) {
            try {
                final Geocoder gcd = new Geocoder(mContext.getApplicationContext());
                List<Address> addresses = gcd.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 10);

                for (Address address : addresses) {
                    if (address.getPostalCode() != null) {
                        currentLocation = address.getAdminArea();
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return currentLocation;
    }

    @Override
    protected void onPostExecute(String result) {
        mProgress.dismiss();
    }
}
