package com.example.huaxie.kladervader;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
//import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;


/**
 * Created by huaxie on 2017-01-04.
 */

public abstract class PermissionUtil {

    private static final String TAG = "permissionUtil";
    public static final String PERMISSION_PROMPT_LOCATION_DIALOG = "permission_prompt_location_dialog";
    public static final String PERMISSION_PROMPT_GPS_DIALOG = "permission_prompt_gps_dialog";
    public static final String PERMISSION_PROMPT_CAMERA_DIALOG =
            "permission_prompt_camera_dialog";
    protected static final int REQUEST_ACCESS_COURSE_LOCATION = 118;
    protected static final int REQUEST_CAMERA = 128;

    public static boolean checkPermission(Activity activity, final String whatPermission){
        Log.d(TAG, "checkPermission: ing");
        if (ContextCompat.checkSelfPermission(activity, whatPermission)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    whatPermission)) {
                switch (whatPermission) {
                    case Manifest.permission.ACCESS_COARSE_LOCATION:
                        if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean
                                (PERMISSION_PROMPT_GPS_DIALOG, true)) {
                            createLocationDialog(activity,Manifest.permission.ACCESS_COARSE_LOCATION);
                            Log.d(TAG, "checkPermission: explaining");
                        }
                        break;
                    case Manifest.permission.READ_EXTERNAL_STORAGE:
                        break;
                    case Manifest.permission.CAMERA:
                        createCameraDialog(activity,Manifest.permission.CAMERA);
                        break;
                }

            } else {
                switch (whatPermission){
                    case Manifest.permission.ACCESS_COARSE_LOCATION:
                        Log.d(TAG, "checkPermission: permission requesting");
                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                REQUEST_ACCESS_COURSE_LOCATION);
                        break;
                }
            }
        } else {
            return true;
        }
        return false;
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

    public static void createLocationDialog(final Activity activity, final String what) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
        builder.setTitle(R.string.request_permission_location_title);
        builder.setMessage(R.string.request_permission_location_text);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Log.d(TAG, "onClick: click ok, request again");
                ActivityCompat.requestPermissions(activity,
                        new String[]{what},
                        REQUEST_ACCESS_COURSE_LOCATION);
            }
        });
        builder.setNegativeButton(R.string.permission_request_dont_ask_again, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences
                        (activity).edit();
                edit.putBoolean(PermissionUtil.PERMISSION_PROMPT_LOCATION_DIALOG, false);
                edit.apply();
                dialog.dismiss();
            }
        });
        builder.show();
    }


    private static void createCameraDialog(final Activity activity,final String what) {
        if (activity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.permission_request_camera_title);
            builder.setMessage(R.string.request_permission_camera_text);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Log.d(TAG, "onClick: click ok, request again");
                    ActivityCompat.requestPermissions(activity,
                            new String[]{what},REQUEST_CAMERA);
                }
            });
            builder.setNegativeButton(R.string.permission_request_dont_ask_again, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences
                            (activity).edit();
                    edit.putBoolean(PermissionUtil.PERMISSION_PROMPT_CAMERA_DIALOG, false);
                    edit.apply();
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }
}


