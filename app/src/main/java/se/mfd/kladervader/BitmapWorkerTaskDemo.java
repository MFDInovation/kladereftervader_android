package se.mfd.kladervader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;



/**
 * Used for loading images that are placed in the apps resources
 */

class BitmapWorkerTaskDemo extends AsyncTask<Integer, Void, Bitmap> {
    private Context mContext;
    private final WeakReference<ImageView> imageViewReference;
    public static final String TAG = "BitmapWorkerTaskDemo";

    BitmapWorkerTaskDemo(ImageView imageView, Context mcontext) {
        imageViewReference = new WeakReference<>(imageView);
        this.mContext = mcontext;
    }
    @Override
    protected Bitmap doInBackground(Integer... integers) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(mContext.getResources(),integers[0],options);
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