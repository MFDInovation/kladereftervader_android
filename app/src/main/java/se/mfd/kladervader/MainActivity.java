package se.mfd.kladervader;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/*
The main activity that is linked to the main window. Starts a GPS search and with
the gathered data from the phones current location it will proceed to call on a networking task
that makes a API call to SMHI. With the weather information fetched, it displays a picture
based on the conditions for the next 8 hours. It has a help button and a button to start changing
pictures that are displayed for different weathers.
 */

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,View.OnClickListener{

    private final String TAG = "MainActivity";
    private TextView mTemp;
    private ImageView baseBackground;
    private RelativeLayout animationContainer;
    private GPS mgps;
    private Weather mCurrentWeather = null;
    private ProgressBar progressBar;
    private ArrayList<String> mUriList;
    private ViewPager mViewPager;
    private Clothing.TempStatus tempKey;
    private int mWindowHeight;
    private int mWindowWidth;
    protected static final int REQUEST_ACCESS_COURSE_LOCATION = 118;
    protected static final int ACTIVITY_RESULT_CODE = 1;
    public static final String gpsError = "gpsError";
    public static final String networkError = "networkError";
    private Button b1,b2;
    private String tempDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTemp = (TextView) findViewById(R.id.temp);

        DisplayMetrics dpMet =this.getResources().getDisplayMetrics();
        int height = dpMet.heightPixels;

        // Init the layout and the different elements in the view
        animationContainer = (RelativeLayout)findViewById(R.id.animation_container);
        baseBackground = (ImageView)findViewById(R.id.base_background);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        b1 = (Button) findViewById(R.id.mainLeftButton);
        b2 = (Button) findViewById(R.id.mainRightButton);

        Button egnaBilderButton = (Button) findViewById(R.id.egna_bilder_button);
        egnaBilderButton.setOnClickListener(this);
        egnaBilderButton.getBackground().setColorFilter(0xFF003399, PorterDuff.Mode.MULTIPLY);
        egnaBilderButton.setText(R.string.hantera_bilder);
        Button helpButton = (Button) findViewById(R.id.helpButton);
        helpButton.setOnClickListener(this);
        helpButton.setText("?");
        helpButton.getBackground().setColorFilter(0xFF003399, PorterDuff.Mode.MULTIPLY);

        if(height < 1000 && height > 500){
            egnaBilderButton.setWidth(200);
            helpButton.setWidth(50);
        }
        else if(height < 500){
            egnaBilderButton.setWidth(100);
            helpButton.setWidth(30);
        }

        mViewPager = (ViewPager) findViewById(R.id.myViewPager);
        progressBar.setVisibility(View.VISIBLE);
        mgps = new GPS(this,hasLocationListener);


        // Initiate buttons.
        b1.setText("<");
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mViewPager.getCurrentItem() > 0)
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
            }
        });
        b1.setVisibility(View.INVISIBLE);
        b1.getBackground().setColorFilter(0xFF003399, PorterDuff.Mode.MULTIPLY);

        b2.setText(">");
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mViewPager.getCurrentItem() < mViewPager.getAdapter().getCount()+1)
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            }
        });
        b2.setVisibility(View.INVISIBLE);
        b2.getBackground().setColorFilter(0xFF003399, PorterDuff.Mode.MULTIPLY);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        EnumSet tempSet = EnumSet.allOf(Clothing.TempStatus.class);
        ArrayList<Clothing.TempStatus> weatherList = new ArrayList<>(tempSet);

        // Add the path for the standard clothing portraits to a weather key in shared preferences
        // Only done one time after install since the datasets will never be null afterwards
        for(Clothing.TempStatus c : weatherList){
            if(c.getName().equals("Kunde inte")) continue;
            Set<String> oldDataSet  = preferences.getStringSet(c.getName(),null);
            if(oldDataSet == null){
                String path = "";
                switch (c){
                    case mycketKallt: path = String.valueOf(R.mipmap.minus20); break;
                    case kallt: path = String.valueOf(R.mipmap.minus20); break;
                    case mycketKalltSno: path = String.valueOf(R.mipmap.minus20); break;
                    case kalltSno: path = String.valueOf(R.mipmap.minus20); break;
                    case nollgradigtMinus: path = String.valueOf(R.mipmap.plus0); break;
                    case nollgradigtMinusRegn: path = String.valueOf(R.mipmap.plus0n); break;
                    case nollgradigtPlus: path = String.valueOf(R.mipmap.plus0); break;
                    case nollgradigtPlusRegn: path = String.valueOf(R.mipmap.plus0n); break;
                    case kyligt: path = String.valueOf(R.mipmap.plus10); break;
                    case kyligtRegn: path = String.valueOf(R.mipmap.plus10n); break;
                    case varmt: path = String.valueOf(R.mipmap.plus10); break;
                    case varmtRegn: path = String.valueOf(R.mipmap.plus10n); break;
                    case varmare: path = String.valueOf(R.mipmap.plus20); break;
                    case varmareRegn: path = String.valueOf(R.mipmap.plus20n); break;
                    case hett: path = String.valueOf(R.mipmap.plus25); break;
                    case hettRegn: path = String.valueOf(R.mipmap.plus25n); break;
                }
                Set<String> myset = new HashSet<>();
                myset.add(path);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putStringSet(c.getName(),myset);
                editor.apply();
            }
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        if(mgps == null){
            mgps = new GPS(this,hasLocationListener);
        }
        if(mCurrentWeather != null){
            startAnimation(mCurrentWeather);
        }
        super.onResume();
    }
    

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        stopAnimation();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        mgps.stop();
        mgps = null;
        super.onStop();
    }

    // Gathers the weather information and starts progressing towards updating layout based on the result.
    public void getWeatherInfo(){
        mCurrentWeather = null;
        final Networking newNetworking = new Networking(new Networking.AsyncResponse(){
            @Override
            public void processFinish(final Weather weather, final Exception exception) {
                if (exception != null) {
                  //  Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                   /* new AlertDialog.Builder(MainActivity.this).setTitle("Error (please take screenshot)").setMessage(exception.getMessage())
                            .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();*/
                    if(exception instanceof IOException){
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showError(networkError);
                            }
                        });
                    }
                }
                mCurrentWeather = weather;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mCurrentWeather == null){
                            Toast.makeText(MainActivity.this,"Did not get the weather info,try again", Toast.LENGTH_LONG).show();
                          //  showError(networkError);
                        }else {
                            // Got weather info, now its time to update the layout
                            updateLayout(mCurrentWeather);
                            progressBar.setVisibility(View.GONE);
                            tempKey = Clothing.getStatus(weather);
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                            Set<String> oldDataSet  = preferences.getStringSet(tempKey.getName(),null);
                            if(oldDataSet != null){
                                mUriList = new ArrayList<>(oldDataSet);
                                updateViewPager();
                            }else {
                                mUriList = new ArrayList<>();
                            }
                        }
                    }
                });
            }
        });// get json object from weather web
        newNetworking.execute();
    }

    private void updateLayout(Weather weather){

        // update the temperature
        double temp =  weather.getTemperature();
        String temperature = (int)Math.round(temp) + "°";
        mTemp.setText(temperature);
        mTemp.setLetterSpacing(0.0001f);

        if(weather.getRainfall() > 0)
            mTemp.setContentDescription(temperature + " celsius med nederbörd");
        else
            mTemp.setContentDescription(temperature + " celsius");

        // update the background based on the current weather
        WeatherSymbol.WeatherStatus status =  weather.getWeatherStatus();
        WeatherImage weatherImage = new WeatherImage();
        int id = WeatherImage.getWeatherSymbolImage(status,weatherImage.getCurrentSeason());
        baseBackground.setImageResource(id);

        baseBackground.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                baseBackground.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                baseBackground.setVisibility(View.VISIBLE);
            }
        });

        tempKey = Clothing.getStatus(weather);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Set<String> oldDataSet  = preferences.getStringSet(tempKey.getName(),null);
        switch (tempKey){
            case mycketKallt: tempDesc=getString(R.string.mycketKallt); break;
            case kallt: tempDesc=getString(R.string.kallt); break;
            case mycketKalltSno: tempDesc=getString(R.string.mycketKallt); break;
            case kalltSno: tempDesc=getString(R.string.kallt); break;
            case nollgradigtMinus: tempDesc=getString(R.string.nollgradigtMinus); break;
            case nollgradigtMinusRegn: tempDesc=getString(R.string.nollgradigtMinusRegn); break;
            case nollgradigtPlus: tempDesc=getString(R.string.nollgradigtPlus); break;
            case nollgradigtPlusRegn: tempDesc=getString(R.string.nollgradigtPlusRegn); break;
            case kyligt: tempDesc=getString(R.string.kyligt); break;
            case kyligtRegn: tempDesc=getString(R.string.kyligtRegn); break;
            case varmt: tempDesc=getString(R.string.kyligt); break;
            case varmtRegn: tempDesc=getString(R.string.kyligtRegn); break;
            case varmare: tempDesc=getString(R.string.varmt); break;
            case varmareRegn: tempDesc=getString(R.string.varmtRegn); break;
            case hett: tempDesc=getString(R.string.hett); break;
            case hettRegn: tempDesc=getString(R.string.hettRegn); break;
        }
        mUriList = new ArrayList<>(oldDataSet); // Cannot be null since each dataset is populated in onCreate if it has not been saved before.
        baseBackground.setContentDescription("");
        RelativeLayout main = (RelativeLayout) findViewById(R.id.activity_main);
        main.setContentDescription("");
        updateViewPager();

        startAnimation(weather);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
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
                        showError(gpsError);
                        break;
                }
                break;
            case ACTIVITY_RESULT_CODE:
                switch (resultCode) {
                    case AppCompatActivity.RESULT_OK:
                        if(tempKey != null) {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                            Set<String> oldDataSet = preferences.getStringSet(tempKey.getName(), null);

                            mUriList = new ArrayList<>(oldDataSet);
                            updateViewPager();
                        }
                        break;
                }
        }
    }

    private se.mfd.kladervader.GPS.HasLocationListener hasLocationListener = new GPS.HasLocationListener() {

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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.egna_bilder_button :
                stopAnimation();
                Intent intent = new Intent(this, ClothesListActivity.class);
                intent.putExtra("tempKey",tempKey);
                startActivityForResult(intent,ACTIVITY_RESULT_CODE);
                break;
            case R.id.helpButton:
                final Dialog dialog = new Dialog(this);

                dialog.setContentView(R.layout.help_dialog);
                dialog.setTitle("Help dialog");
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                final TextView editText = (TextView) dialog.findViewById(R.id.helpText);
                editText.setText(R.string.help_text_main);
                editText.setContentDescription(getString(R.string.help_text_main));
                Button btn = (Button) dialog.findViewById(R.id.close);
                btn.getBackground().setColorFilter(0xFF003399, PorterDuff.Mode.MULTIPLY);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }});
                dialog.show();
                break;

            default:
                Log.d(TAG, "onClick: "+ view.toString()+ "is clicked");
        }
    }

    // Update the imagelist and initiate the viewpager.
    private void updateViewPager(){
        progressBar.setVisibility(View.INVISIBLE);
        final ArrayList<Uri> mViewPagerList = changeStringListToUri(mUriList);
        if(!mViewPagerList.isEmpty()){
            Collections.sort(mViewPagerList,new Comparator<Uri>() {
                @Override
                public int compare(Uri s1, Uri s2) {
                    return -s1.compareTo(s2);
                }
            });
            DisplayMetrics dpMet =this.getResources().getDisplayMetrics();
            int height = dpMet.heightPixels;
            height = height - (height/3) + (height/7);
            final PagerAdapter adapterViewPager = new MyPagerAdapter(mViewPagerList, this, tempDesc,height);
            mViewPager.setAdapter(adapterViewPager);
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    if(position == 0) b1.setVisibility(View.INVISIBLE);
                    else b1.setVisibility(View.VISIBLE);

                    if(position == mViewPager.getAdapter().getCount()-1) b2.setVisibility(View.INVISIBLE);
                    else b2.setVisibility(View.VISIBLE);

                    if(mViewPagerList.size() == 1){
                        b1.setVisibility(View.INVISIBLE);
                        b2.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            //Set button initial visibility based on number of images for current weather.
            if(mViewPagerList.size() == 1){
                b1.setVisibility(View.INVISIBLE);
                b2.setVisibility(View.INVISIBLE);
            }
            if(mViewPagerList.size() > 1){
                b1.setVisibility(View.INVISIBLE);
                b2.setVisibility(View.VISIBLE);
            }

            mViewPager.setVisibility(View.VISIBLE);
            if(mViewPagerList.size() > 1) b2.setVisibility(View.VISIBLE);
        }
    }

    private ArrayList<Uri> changeStringListToUri(ArrayList<String> mUriList){
        ArrayList<Uri> list = new ArrayList<>();
        for (String s: mUriList) {
            Uri myUri = Uri.parse(s);
            list.add(myUri);
        }
        return list;
    }

    private static class MyPagerAdapter extends PagerAdapter {
        private ArrayList<Uri> dataList;
        private LayoutInflater mInflater;
        private Context mContext;
        private String tempDesc;
        private int px;

        MyPagerAdapter(ArrayList<Uri> dataList, Context context,String imageDesc, int px){
            this.dataList = dataList;
            this.mContext = context;
            this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.tempDesc = imageDesc;
            this.px = px;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mInflater.inflate(R.layout.picture_list_row, container, false);
            itemView.setContentDescription(tempDesc);
            Button delete = (Button)itemView.findViewById(R.id.delete_button);
            delete.setVisibility(View.INVISIBLE);
            try{
                ImageView imageView = (ImageView) itemView.findViewById(R.id.picture_place_holder);
                imageView.getLayoutParams().height = px;

                Uri selectedImage = dataList.get(position);
                if(!selectedImage.toString().contains("/")){  //Don't use filepaths, just load image directly.
                    BitmapWorkerTaskDemo clothesLoader = new BitmapWorkerTaskDemo(imageView,mContext);
                    clothesLoader.execute(Integer.parseInt(selectedImage.toString()));
                    imageView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
                    imageView.postInvalidate();
                }
                //Image taken from media folder, use filepaths and bitworker.
                else {
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = mContext.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    assert cursor != null;
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imagePath = cursor.getString(columnIndex);
                    cursor.close();
                    int rotate = getCameraPhotoOrientation(mContext, selectedImage, imagePath);
                    Log.d("Image", " " + rotate);
                    BitmapWorkerTask ImageLoader = new BitmapWorkerTask(imageView);
                    ImageLoader.execute(imagePath);
                    if (rotate == 90) {
                        int px2 = imageView.getLayoutParams().width;
                        if(px2 > 0 ) imageView.getLayoutParams().height = px2;
                        imageView.setRotation(90);
                    } else if (rotate == 270) {
                        int px2 = imageView.getLayoutParams().width;
                        if(px2 > 0 ) imageView.getLayoutParams().height = px2;
                        imageView.setRotation(-90);
                    }
                    imageView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
                }
            }catch (OutOfMemoryError e1) {
                e1.printStackTrace();
                Log.e("Memory exceptions", "exceptions" + e1);
                return null;
            }
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }

        private int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath){
            int rotate = 0;
            try {
                context.getContentResolver().notifyChange(imageUri, null);
                File imageFile = new File(imagePath);

                ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate = 270;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate = 90;
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return rotate;
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String status = bundle.getString("status");
            final int interval = bundle.getInt("interval");
            final double windspeed = bundle.getDouble("windspeed");
            final Bitmap picture = bundle.getParcelable("picture");
            assert status != null;
            if(java.util.Objects.equals(status, "snow")){
                Log.d(TAG, "handleMessage: snow");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        WeatherAnimation.snowAnimation(windspeed,MainActivity.this,animationContainer,mWindowHeight,mWindowWidth,picture);
                        mHandler.postDelayed(this, interval);
                    }
                });

            }else if ("rain".equals(status)){
                Log.d(TAG, "handleMessage: rain" + windspeed);
                mHandler.post( new Runnable() {
                    @Override
                    public void run() {
                        WeatherAnimation.rainAnimation(windspeed,MainActivity.this,animationContainer,mWindowHeight,mWindowWidth,picture);
                        mHandler.postDelayed(this, interval);
                    }
                });

            }else if (status.equals("thunder")){
                Log.d(TAG, "handleMessage: thunder");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        WeatherAnimation.thunderAnimation(MainActivity.this,animationContainer,mWindowHeight,mWindowWidth,picture);
                        mHandler.postDelayed(this, interval);
                    }
                });
            }
        }

    };

    public Handler getHandler(){
        return mHandler;
    }


    private void startAnimation(Weather mCurrentWeather){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mWindowHeight = displaymetrics.heightPixels;
        mWindowWidth = displaymetrics.widthPixels;
        WeatherAnimation.setAnimationInterval(this,mCurrentWeather);
    }


    public void stopAnimation(){
        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
        for(int index=0; index<animationContainer.getChildCount(); ++index) {
            View nextChild = animationContainer.getChildAt(index);
            nextChild.clearAnimation();
            animationContainer.removeView(nextChild);
            animationContainer.invalidate();
        }

    }

    private void showError(String key){
        progressBar.setVisibility(View.INVISIBLE);
        if(key.equals(gpsError)){
            baseBackground.setImageResource(R.mipmap.gps_error);
        }else if (key.equals(networkError)){
            baseBackground.setImageResource(R.mipmap.internet_error);
        }
    }
}
