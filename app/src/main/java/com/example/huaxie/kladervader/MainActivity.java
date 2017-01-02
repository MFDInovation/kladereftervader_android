package com.example.huaxie.kladervader;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private final int POSITION_PERMISSION = 1;
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.mtext);

        GPS mGPS = new GPS(getApplicationContext(),this);
        mTextView.setText("latitude = " + mGPS.getLatitude() + "getLongitude = "+ mGPS.getLongitude());
    }

    private final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 128;
    public void checkPermission(int permissionType){
        if(permissionType == POSITION_PERMISSION){
            if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION  },
                        MY_PERMISSION_ACCESS_COURSE_LOCATION );
            }
        }
    }


}
