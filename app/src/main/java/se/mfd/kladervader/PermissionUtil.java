package se.mfd.kladervader;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

//import android.support.v13.app.ActivityCompat;


/**
 * Created by dev on 2017-01-04.
 */

abstract class PermissionUtil {

    private static final String TAG = "permissionUtil";
    private static final String PERMISSION_PROMPT_LOCATION_DIALOG = "permission_prompt_location_dialog";
    private static final String PERMISSION_PROMPT_GPS_DIALOG = "permission_prompt_gps_dialog";
    private static final String PERMISSION_PROMPT_STORAGE_DIALOG =
            "permission_prompt_storage_dialog";
    private static final int REQUEST_ACCESS_COURSE_LOCATION = 118;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 138;

    static boolean checkPermission(AppCompatActivity activity, final String whatPermission){
        Log.d(TAG, "checkPermission: ing");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.w(TAG, "hasPermissions: API version < M, returning true by default");
            return true;
        }

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
                        if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean
                                (PERMISSION_PROMPT_GPS_DIALOG, true)) {
                            createStorageDialog(activity,Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                        break;
                    case Manifest.permission.CAMERA:
                        //createCameraDialog(activity,Manifest.permission.CAMERA);
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
                    case Manifest.permission.READ_EXTERNAL_STORAGE:
                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_READ_EXTERNAL_STORAGE);
                        break;
                }
            }
        } else {
            return true;
        }
        return false;
    }

    private static void createLocationDialog(final Activity activity, final String what) {
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


    private static void createStorageDialog(final Activity activity,final String what) {
        if (activity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.permission_request_storage_title);
            builder.setMessage(R.string.request_permission_storage_text);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Log.d(TAG, "onClick: click ok, request again");
                    ActivityCompat.requestPermissions(activity,
                            new String[]{what},REQUEST_READ_EXTERNAL_STORAGE);
                }
            });
            builder.setNegativeButton(R.string.permission_request_dont_ask_again, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences
                            (activity).edit();
                    edit.putBoolean(PermissionUtil.PERMISSION_PROMPT_STORAGE_DIALOG, false);
                    edit.apply();
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

}


