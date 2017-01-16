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

    public BitmapWorkerTaskDemo(ImageView imageView, Context mcontext) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.mContext = mcontext;
    }
    @Override
    protected Bitmap doInBackground(Integer... integers) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        options.inJustDecodeBounds = false;
        Bitmap changedImage = BitmapFactory.decodeResource(mContext.getResources(),integers[0],options);
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
