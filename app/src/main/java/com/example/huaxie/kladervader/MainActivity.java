package com.example.huaxie.kladervader;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import java.util.List;
import java.util.concurrent.locks.Lock;

import static android.content.DialogInterface.BUTTON_POSITIVE;
import static com.example.huaxie.kladervader.PermissionUtil.PERMISSION_PROMPT_GPS_DIALOG;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ViewPager.OnPageChangeListener{

    private final String TAG = "MainActivity";
    private final int POSITION_PERMISSION = 1;
    private TextView mTextView;
    private PercentRelativeLayout baseContainer;
    private ImageView baseBackground;
    private RelativeLayout tempContainer;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private static double latitude; // latitude
    private static double longitude; // longitude
    private LocationRequest locationRequest;
    protected static final int REQUEST_CHECK_SETTINGS = 108;
    protected static final int REQUEST_ACCESS_COURSE_LOCATION = 118;
    protected static final int REQUEST_CAMERA = 128;
    protected static final int REQUEST_READ_EXTERNAL_STORAGE = 138;

    FragmentPagerAdapter adapterViewPager;

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

        //layout update
        baseContainer = (PercentRelativeLayout)findViewById(R.id.base_container);
        baseBackground = (ImageView)findViewById(R.id.base_background);
        tempContainer = (RelativeLayout)findViewById(R.id.temp_container);
        baseBackground.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                baseBackground.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int imageHeight = baseBackground.getHeight();
                Log.d(TAG, "onstart: imageHeight" + imageHeight);
                int height = (int)Math.round(imageHeight*0.22);
                PercentRelativeLayout.LayoutParams params = new PercentRelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                tempContainer.setLayoutParams(params);
            }
        });
        //viewpagaer test
        /*ViewPager vpPager = (ViewPager) findViewById(R.id.myViewPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);*/

        //location related
        locationRequest  = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
    }


    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        super.onStart();
    }

    @Override
    protected void onResume() {
        if(checkLegacyLocationPermission(this)){
            Log.d(TAG, "onresume, can get location");
            //need update
            getLocation();
        }else {
            Log.d(TAG, "onresume, gps need enable");
            enableGPS();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

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
        Log.d(TAG, "getLocation: "+ mLastLocation);
        return mLastLocation;
    }

    public void getWeatherInfo(){
//        Log.d(TAG, "getWeatherInfo: !!!!!!!!!");
        Networking newNetworking = new Networking();// get json object from weather web
        newNetworking.execute();
    }

    private void enableGPS(){
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
                        getLocation();
                        getWeatherInfo();
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
        Log.d(TAG, "checkPermission: ing");
        if (ContextCompat.checkSelfPermission(this, whatPermission)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    whatPermission)) {
                switch (whatPermission) {
                    case Manifest.permission.ACCESS_COARSE_LOCATION:
                        if (PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean
                                (PERMISSION_PROMPT_GPS_DIALOG, true)) {
                            createLocationDialog(Manifest.permission.ACCESS_COARSE_LOCATION);
                            Log.d(TAG, "checkPermission: explaining");
                        } else {}
                        break;
                }

            } else {
                Log.d(TAG, "checkPermission: permission requesting");
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_ACCESS_COURSE_LOCATION);
            }
        } else {
            Log.d(TAG, "checkPermission: has permission");
            if(checkLegacyLocationPermission(this)){
                Log.d(TAG, "checkPermission: has location enabled, geting location");
                //need update
                getLocation();
            }else {
                Log.d(TAG, "checkPermission: GPS need enabled");
                enableGPS();
            }
        }
    }

    public static boolean checkLegacyLocationPermission(Context context) {
        boolean gps_enabled = false;
        boolean network_enabled = false;
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_COURSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    if(checkLegacyLocationPermission(this)){
                        getLocation();
                        Log.d(TAG, "onRequestPermissionsResult: GPS is on, waiting for location");
                    }else {
                        Log.d(TAG, "onRequestPermissionsResult: GPS need enable!!!");
                        enableGPS();
                    }
                    break;
                } else {
                    String message = "We need your location to get weather information";
                    Toast.makeText(this,message,Toast.LENGTH_LONG).show();
                    break;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case AppCompatActivity.RESULT_OK:
                        Log.d(TAG, "onActivityResult: client allow to enable GPS");
                        break;
                    case AppCompatActivity.RESULT_CANCELED:
                        Log.d(TAG, "onActivityResult: client donnt allow to enable GPS");
                        finish();
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
                Log.d(TAG, "onClick: click ok, request again");
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        Toast.makeText(MainActivity.this,
                "Selected page position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    private static class MyPagerAdapter extends FragmentPagerAdapter {
//        private List<In>

        private static int NUM_ITEMS = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            return ImageFragment.newInstance(position,R.mipmap.minusten);
        }
    }
}
