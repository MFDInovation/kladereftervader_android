package com.example.huaxie.kladervader;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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


public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    private final String TAG = "MainActivity";
    private final int POSITION_PERMISSION = 1;
    private TextView mTextView;
    private PercentRelativeLayout baseContainer;
    private ImageView baseBackground;
    private RelativeLayout tempContainer;
    private GPS mgps;

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

        mgps = new GPS(this,hasLocationListener);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }



    public void getWeatherInfo(){
//        Log.d(TAG, "getWeatherInfo: !!!!!!!!!");
        Networking newNetworking = new Networking();// get json object from weather web
        newNetworking.execute();
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
                    onActivityResult(requestCode, AppCompatActivity.RESULT_OK,null);
                    break;
                } else {
                    String message = "We need your location to get weather information";
                    Toast.makeText(this,message,Toast.LENGTH_LONG).show();
                    onActivityResult(requestCode, AppCompatActivity
                            .RESULT_CANCELED, null);
                    break;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ACCESS_COURSE_LOCATION:
                switch (resultCode) {
                    case AppCompatActivity.RESULT_OK:
                        Log.d(TAG, "onActivityResult: client allow to enable GPS");
                        mgps.doWhenPermissionIsGranted();
                        break;
                    case AppCompatActivity.RESULT_CANCELED:
                        Log.d(TAG, "onActivityResult: client donnt allow to enable GPS");
                        finish();
                        break;
                }
                break;
        }
    }

    private com.example.huaxie.kladervader.GPS.HasLocationListener hasLocationListener = new GPS.HasLocationListener() {

        @Override
        public void hasLocation(final double latitude, final double longitude) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this,"latitude" + latitude + "longitude" + longitude, Toast.LENGTH_LONG).show();
                }
            });
            mgps.stop();
        }
    };


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
