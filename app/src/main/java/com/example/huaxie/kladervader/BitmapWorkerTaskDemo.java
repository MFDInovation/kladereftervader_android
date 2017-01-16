package com.example.huaxie.kladervader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;



/**
 * Created by huaxie on 2017-01-16.
 */

public class BitmapWorkerTaskDemo extends AsyncTask<Integer, Void, Bitmap> {
    private Context mContext;
    private final WeakReference<ImageView> imageViewReference;
    private DemoActivity activity;
    private Boolean isbackground;
    public static final String TAG = "BitmapWorkerTaskDemo";

    public BitmapWorkerTaskDemo(ImageView imageView, Context mcontext, DemoActivity activity,Boolean isbackground) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.mContext = mcontext;
        this.activity = activity;
        this.isbackground = isbackground;
    }
    @Override
    protected Bitmap doInBackground(Integer... integers) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        int width= mContext.getResources().getDisplayMetrics().widthPixels/8;
        int height= mContext.getResources().getDisplayMetrics().heightPixels/8;
        BitmapFactory.Options options = new BitmapFactory.Options();
        int old = BitmapWorkerTask.calculateInSampleSize(options, 300, 300);
        options.inSampleSize = 3;
        options.inJustDecodeBounds = false;
        Log.d(TAG, "doInBackground: " +old);
        Bitmap changedImage = BitmapFactory.decodeResource(mContext.getResources(),integers[0],options);
        activity.addBitmapToMemoryCache(String.valueOf(integers[0]), changedImage);
        return changedImage;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
