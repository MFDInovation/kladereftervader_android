package com.example.huaxie.kladervader;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import static android.content.DialogInterface.BUTTON_POSITIVE;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = "MainActivity";
    private final int POSITION_PERMISSION = 1;
    private TextView mTextView;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private static double latitude; // latitude
    private static double longitude; // longitude
    protected static final int REQUEST_CHECK_SETTINGS = 108;
    protected static final int REQUEST_ACCESS_COURSE_LOCATION = 118;
    protected static final int REQUEST_CAMERA = 128;
    protected static final int REQUEST_READ_EXTERNAL_STORAGE = 138;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.mtext);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


    }


    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        //checkLocationSetting();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public Location getLocation(){
        try{
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                latitude =  mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
                mTextView.setText("latitude = " + String.valueOf(latitude) + "getLongitude = "+ String.valueOf(longitude));
                mTextView.invalidate();
            }
        }catch (SecurityException e){
            Log.d(TAG, "onRequestPermissionsResult: location requestion is granted");
        }
        Networking newNetworking = new Networking();// get json object from weather web
        newNetworking.execute();
        return mLastLocation;
    }

    private void enableGPS(){
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    public void checkPermission(final String whatPermission){
        if (ContextCompat.checkSelfPermission(this, whatPermission)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    whatPermission)) {
                switch (whatPermission) {
                    case Manifest.permission.ACCESS_COARSE_LOCATION:
                        /*String message = "We need your location to get weather information";
                        showMessageOKCancel(message,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which == BUTTON_POSITIVE){
                                            dialog.dismiss();
                                        }else{
                                            dialog.dismiss();
                                            finish();
                                        }
                                    }
                                });
                        enableGPS();
                        getLocation();
                        break;*/
                }

            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_ACCESS_COURSE_LOCATION);
            }
        } else {
            enableGPS();
            getLocation();
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.app.AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_COURSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableGPS();
                    getLocation();
                    break;
                } else {
                    /*String message = "We need your location to get weather information";
                    Toast.makeText(this,message,Toast.LENGTH_LONG).show();
                    enableGPS();
                    mGoogleApiClient.connect();
                    getLocation();*/
                    break;
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case AppCompatActivity.RESULT_OK:
                        getLocation();
                        break;
                    case AppCompatActivity.RESULT_CANCELED:
                        finish();//keep asking if imp or do whatever
                        break;
                }
                break;
        }
    }

    public static double getlatitude(){return latitude;}

    public static double getlongitude(){return longitude;}

    private void createLocationDialog(final String what) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.request_permission_location_title);
        builder.setMessage(R.string.request_permission_location_text);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{what},
                        REQUEST_ACCESS_COURSE_LOCATION);
            }
        });
        builder.setNegativeButton(R.string.permission_request_dont_ask_again, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences
                        (MainActivity.this).edit();
                edit.putBoolean(PermissionUtil.PERMISSION_PROMPT_LOCATION_DIALOG, false);
                edit.apply();
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
