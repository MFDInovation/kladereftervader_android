package com.example.huaxie.kladervader;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    private final String TAG = "MainActivity";
    private final int POSITION_PERMISSION = 1;
    private TextView mTemp;
    private PercentRelativeLayout baseContainer;
    private ImageView baseBackground;
    private RelativeLayout tempContainer;
    private GPS mgps;
    private Weather mCurrentWeather;
    private ProgressBar progressBar;
    private ImageView portrait;

    protected static final int REQUEST_CHECK_SETTINGS = 108;
    protected static final int REQUEST_ACCESS_COURSE_LOCATION = 118;
    protected static final int REQUEST_CAMERA = 128;
    protected static final int REQUEST_READ_EXTERNAL_STORAGE = 138;

    FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTemp = (TextView) findViewById(R.id.temp);

        //layout update
        baseContainer = (PercentRelativeLayout)findViewById(R.id.base_container);
        baseBackground = (ImageView)findViewById(R.id.base_background);
        tempContainer = (RelativeLayout)findViewById(R.id.temp_container);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        portrait = (ImageView)findViewById(R.id.portrait_container);

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
        progressBar.setVisibility(View.VISIBLE);
        super.onResume();
    }

    @Override
    protected void onStop() {
        mgps.stop();
        super.onStop();
    }

    public void getWeatherInfo(){
        mCurrentWeather = null;
        final Networking newNetworking = new Networking(new Networking.AsyncResponse(){
            @Override
            public void processFinish(final Weather weather) {
                mCurrentWeather = weather;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mCurrentWeather == null){
                            Toast.makeText(MainActivity.this,"Did not get the weather info,try again", Toast.LENGTH_LONG).show();
                        }else {
                            updateLayout(mCurrentWeather);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });// get json object from weather web
        newNetworking.execute();
    }

    private void updateLayout(Weather mCurrentWeather){
        //update temp
        double temp = mCurrentWeather.getTemperature();
        String temperature = (int)Math.round(temp) + "°";
        mTemp.setText(temperature);
        //update background
        WeatherSymbol.WeatherStatus status = mCurrentWeather.getWeatherStatus();
        WeatherImage weatherImage = new WeatherImage();
        int id = weatherImage.getWeatherSymbolImage(status,weatherImage.getCurrentSeason());
        baseBackground.setImageResource(id);
        baseBackground.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                baseBackground.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int imageHeight = baseBackground.getHeight();
                Log.d(TAG, "onstart: imageHeight" + imageHeight);
                int height = (int)Math.round(imageHeight*0.22);
                PercentRelativeLayout.LayoutParams params = new PercentRelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                tempContainer.setLayoutParams(params);
                baseBackground.setVisibility(View.VISIBLE);
            }
        });
        //update portrait
        Closing closing = new Closing();
        int portraitId = closing.getClosingImage(mCurrentWeather);
        portrait.setImageResource(portraitId);
        portrait.setVisibility(View.VISIBLE);
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.VISIBLE);
                                Toast.makeText(MainActivity.this, "Waiting for GPS", Toast.LENGTH_SHORT).show();
                            }
                        });
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
            getWeatherInfo();
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
            return ImageFragment.newInstance(position,R.mipmap.minus10);
        }
    }

}
