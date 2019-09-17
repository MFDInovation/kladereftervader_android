package se.mfd.kladervader;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * This class handles the GPS and is used for gathering the coordinates of the smartphone that is used
 * when doing a API call to SMHI.
 */

class GPS implements GoogleApiClient.ConnectionCallbacks,LocationListener,
        GoogleApiClient.OnConnectionFailedListener,ResultCallback<LocationSettingsResult> {

    private static final String TAG = "GPS";
    private GoogleApiClient mGoogleApiClient;
    private Activity mActivity;
    private Location mLastLocation;
    private static double latitude; // latitude
    private static double longitude; // longitude
    private LocationRequest mLocationRequest;
    private HasLocationListener mHasLocationListener;

    private static final int UPDATE_INTERVAL = 10000;
    private static final int FASTEST_INTERVAL = 5000;

    GPS(AppCompatActivity activity, HasLocationListener listener) {
        mActivity = activity;
        mHasLocationListener = listener;
        checkPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    interface HasLocationListener {
        void hasLocation(double latitude,double longitude);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    void stop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    void doWhenPermissionIsGranted() {
        Log.d(TAG, "doWhenPermissionIsGranted: ");
//        setupTimeoutHandler();
        buildGoogleApiClient(mActivity);
        enableGPS();
        mGoogleApiClient.connect();
    }

    private void checkPermission(AppCompatActivity activity, final String locationPermission) {
        if (mActivity != null) {
            Log.d(TAG, "checkPermission: ");
            if (PermissionUtil.checkPermission(activity, locationPermission)) {
                doWhenPermissionIsGranted();
            }
        }
    }


    private Location updateLocation(){
        try{
            Log.d(TAG, "updateLocation: updating");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }catch (SecurityException e){
            Log.d(TAG, "onRequestPermissionsResult: location requestion is granted");
        }
        Log.d(TAG, "getLocation: "+ mLastLocation);
        return mLastLocation;
    }

    private synchronized void buildGoogleApiClient(Context context){
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    private void enableGPS() {
        Log.d(TAG, "enableGPS: ");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        builder.setAlwaysShow(false);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(this);
    }


    @Override
    public void onResult(@NonNull LocationSettingsResult result) {
        final Status status = result.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.d(TAG, "onResult: updatelocation");
                updateLocation();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult(mActivity, MainActivity.REQUEST_ACCESS_COURSE_LOCATION);
                } catch (IntentSender.SendIntentException ignored) {
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                break;
        }
    }

    static double getlatitude(){return latitude;}

    static double getlongitude(){return longitude;}

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: " + location);
        if(location != null)
        {
            mLastLocation = location;
            latitude= location.getLatitude();
            longitude = location.getLongitude();
            mHasLocationListener.hasLocation(location.getLatitude(), location.getLongitude());
        }
    }
}
