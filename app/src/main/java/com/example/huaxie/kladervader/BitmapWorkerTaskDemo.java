package com.example.huaxie.kladervader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;



/**
 * Created by huaxie on 2017-01-16.
 */

public class BitmapWorkerTaskDemo extends AsyncTask<Integer, Void, Bitmap> {
    private Context mContext;
    private final WeakReference<ImageView> imageViewReference;
    private MainActivity activity;
    private int height;
    private int width;
    public static final String TAG = "BitmapWorkerTaskDemo";

    public BitmapWorkerTaskDemo(ImageView imageView, Context mcontext, MainActivity activity, int height, int width) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.mContext = mcontext;
        this.activity = activity;
        this.height = height;
        this.width = width;
    }
    @Override
    protected Bitmap doInBackground(Integer... integers) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        int old = BitmapWorkerTask.calculateInSampleSize(options,width, height);
        options.inSampleSize = 2;
        options.inJustDecodeBounds = false;
        Bitmap changedImage = BitmapFactory.decodeResource(mContext.getResources(),integers[0],options);
        return changedImage;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}