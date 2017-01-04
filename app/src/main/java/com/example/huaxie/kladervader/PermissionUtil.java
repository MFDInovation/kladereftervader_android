package com.example.huaxie.kladervader;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.preference.PreferenceManager;
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

    public static final String PERMISSION_PROMPT_LOCATION_DIALOG = "permission_prompt_location_dialog";
    public static final String PERMISSION_PROMPT_GPS_DIALOG = "permission_prompt_gps_dialog";
    public static final String PERMISSION_PROMPT_CAMERA_DIALOG =
            "permission_prompt_camera_dialog";

    /*public static boolean requestLocation(Activity activity, DialogInterface.OnClickListener
            listener) {
        if (ContextCompat.checkSelfPermission(activity, Manifest
                .permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (activity != null && ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean
                        (PERMISSION_PROMPT_GPS_DIALOG, true)) {
                    createLocationDialog(activity, listener);
                } else {}
            } else {
                if (activity != null) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            Base.ACCESS_FINE_LOCATION_PERMISSION_REQUEST);
//                    checkPermission();
                }
            }
        } else {
            return true;
        }
        return false;
    }

    public static boolean checkLocationPermissionGranted(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest
                .permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
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

    private static void createLocationDialog(final Activity activity, DialogInterface
            .OnClickListener
            listener) {
        if (activity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.permission_request_gps_title);
            builder.setMessage(R.string.permission_request_gps_text);
            builder.setPositiveButton(R.string.ok, listener);
            builder.setNegativeButton(R.string.permission_request_dont_ask_again, listener);
            builder.show();
        }
    }

    public static boolean checkCameraPermission(Activity activity, DialogInterface
            .OnClickListener listener, boolean start) {
        if (ContextCompat.checkSelfPermission(activity, Manifest
                .permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            if (activity != null && ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.CALL_PHONE)) {
                if (start) {
                    if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean
                            (PERMISSION_PROMPT_CALL_DIALOG, true)) {
                        createPhoneDialog(activity, listener);
                    }
                } else {
                    createPhoneDialog(activity, listener);
                }
            } else {
                if (activity != null) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.CALL_PHONE},
                            Base.CALL_PHONE_PERMISSION);
//                    checkPermission();
                }
            }
        } else {
            return true;
        }
        return false;
    }

    private static void createCameraDialog(final Activity activity,
                                          DialogInterface.OnClickListener listener) {
        if (activity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.permission_request_call_phone_title);
            builder.setMessage(R.string.permission_request_call_phone_text);
            builder.setPositiveButton(R.string.ok, listener);
            builder.setNegativeButton(R.string.denied, listener);
            builder.show();
        }
    }*/
}


