package com.example.huaxie.kladervader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.util.LruCache;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;


public class DemoActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{
    private static int tempContainerHeight;
    private final int dedaultHeight = 150;
    private ArrayList<Weather> fakeList;
    private int mWindowHeight;
    private int mWindowWidth;
    PercentRelativeLayout basecontainer;
    private final static String TAG = "DemoActivity";
    private Runnable thunderRunnable = null;
    private Runnable rainRunnable = null;
    private Runnable snowRunnable = null;

    private LruCache<String, Bitmap> mMemoryCache;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        tempContainerHeight = getIntent().getIntExtra(MainActivity.ExtraMessage,dedaultHeight);
        MyPagerAdapter adapterViewPager = new MyPagerAdapter(getFakeWeather(),this,this);
        ViewPager mViewPager = (ViewPager)findViewById(R.id.myViewPager);
        mViewPager.setAdapter(adapterViewPager);
        mViewPager.setOnPageChangeListener(this);

        basecontainer = (PercentRelativeLayout)findViewById(R.id.base_container);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mWindowHeight = displaymetrics.heightPixels;
        mWindowWidth = displaymetrics.widthPixels;

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    /*public void loadBitmap(int resId, ImageView imageView) {
        final String imageKey = String.valueOf(resId);

        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            BitmapWorkerTaskDemo task = new BitmapWorkerTaskDemo(imageView,mContext);
            task.execute(resId);
        }
    }*/

    public ArrayList<Weather> getFakeWeather(){
        fakeList = new ArrayList<>();
        Weather fakeWeather;
        fakeWeather= new Weather(WeatherSymbol.WeatherStatus.ClearSky,3.0,0.1,0.1); //spring
        fakeList.add(fakeWeather);
        fakeWeather = new Weather(WeatherSymbol.WeatherStatus.Lightsleet, 1.0,0.75,3); //spring rain and snow
        fakeList.add(fakeWeather);
        fakeWeather = new Weather(WeatherSymbol.WeatherStatus.ClearSky, 25.0,0.0,0.0); //summer
        fakeList.add(fakeWeather);
        fakeWeather = new Weather(WeatherSymbol.WeatherStatus.Thunder,20.0,0.7,0.0); //summer Thunder and rain
        fakeList.add(fakeWeather);
        fakeWeather = new Weather(WeatherSymbol.WeatherStatus.ClearSky, 14.0, 0.1,0.1); // autumn
        fakeList.add(fakeWeather);
        fakeWeather = new Weather(WeatherSymbol.WeatherStatus.Rain,10.0, 0.9, 6); //autumn rain
        fakeList.add(fakeWeather);
        fakeWeather = new Weather(WeatherSymbol.WeatherStatus.Snowshowers, -9, 0.7, 4); //winter
        fakeList.add(fakeWeather);
        fakeWeather = new Weather(WeatherSymbol.WeatherStatus.Cloudysky, -20,0.1,4); // winter no snow but cold
        fakeList.add(fakeWeather);
        return fakeList;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.d(TAG, "onPageScrolled: " + position);
        if(positionOffset == 0){
            WeatherAnimation.setAnimationIntervalDemo(this,fakeList.get(position));
        }else {
            if(mHandler != null){
                if(rainRunnable != null){
                    mHandler.removeCallbacks(rainRunnable);
                    basecontainer.removeAllViews();
                }
                if(snowRunnable != null){
                    mHandler.removeCallbacks(snowRunnable);
                    basecontainer.removeAllViews();
                }
                if(thunderRunnable != null){
                    mHandler.removeCallbacks(thunderRunnable);
                    basecontainer.removeAllViews();
                }
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private static class MyPagerAdapter extends PagerAdapter {
        private ArrayList<Weather>  weatherList;
        private LayoutInflater mInflater;
        private Context mContext;
        private DemoActivity activity;

        public MyPagerAdapter(ArrayList<Weather>  weatherList, Context context, DemoActivity activity){
            this.weatherList = weatherList;
            this.mContext = context;
            this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return weatherList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mInflater.inflate(R.layout.demo_item, container, false);
            ImageView background = (ImageView) itemView.findViewById(R.id.base_background);
            Weather currentWeather = weatherList.get(position);
            int resId = 0;
            if(position < 2) {
                resId = WeatherImage.getWeatherSymbolImage(currentWeather.getWeatherStatus(),WeatherImage.spring);
            }else if(position < 4) {
                resId = WeatherImage.getWeatherSymbolImage(currentWeather.getWeatherStatus(),WeatherImage.summer);
            }else if(position < 6) {
                resId = WeatherImage.getWeatherSymbolImage(currentWeather.getWeatherStatus(),WeatherImage.autumn);
            }else {
                resId = WeatherImage.getWeatherSymbolImage(currentWeather.getWeatherStatus(),WeatherImage.winter);
            }
            RelativeLayout tempContainer = (RelativeLayout)itemView.findViewById(R.id.temp_container);
            PercentRelativeLayout.LayoutParams params = new PercentRelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, tempContainerHeight);
            tempContainer.setLayoutParams(params);

            final String backgroundKey = String.valueOf(resId);

            final Bitmap backgroundImage = activity.getBitmapFromMemCache(backgroundKey);
            if (backgroundImage != null) {
                background.setImageBitmap(backgroundImage);
            } else {
                BitmapWorkerTaskDemo task = new BitmapWorkerTaskDemo(background,mContext,activity,true);
                task.execute(resId);
            }
//            background.setImageResource(resId);


            Clothing clothing = new Clothing();
            int portraitId = clothing.getClosingImage(currentWeather);
            ImageView portrait = (ImageView) itemView.findViewById(R.id.portrait_container);
//            Uri imageUri = Uri.parse("android.resource://mipmap/" + portraitId);
//            String imagePath = BitmapWorkerTask.getPathFromImageUri(imageUri,mContext);
//            BitmapWorkerTask ImageLoader = new BitmapWorkerTask(portrait);
//            ImageLoader.execute(imagePath);
//            BitmapWorkerTaskDemo ImageLoader = new BitmapWorkerTaskDemo(portrait,mContext);
//            ImageLoader.execute(portraitId);

            final String imageKey = String.valueOf(portraitId);

            final Bitmap bitmap = activity.getBitmapFromMemCache(imageKey);
            if (bitmap != null) {
                portrait.setImageBitmap(bitmap);
            } else {
                BitmapWorkerTaskDemo task = new BitmapWorkerTaskDemo(portrait,mContext,activity,false);
                task.execute(portraitId);
            }

            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }
    }

    public Handler getHandler(){
        return mHandler;
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            int testSymbolvalue = 11;
//            int testTemp = -10;
//            double testwind = 2.9;
//            double testrain = 0.1;
//            final Weather weather = new Weather(testSymbolvalue,testTemp,testrain,testwind);
            Bundle bundle = msg.getData();
            String status = bundle.getString("status");
            final int interval = bundle.getInt("interval");
            final int windspeed = bundle.getInt("windspeed");
            if(status.equals("snow")){
                Log.d(TAG, "handleMessage: snow");
                mHandler.post(snowRunnable = new Runnable() {
                    @Override
                    public void run() {
                        WeatherAnimation.snowAnimation(windspeed,DemoActivity.this,basecontainer,mWindowHeight,mWindowWidth);
                        mHandler.postDelayed(this, interval);
                    }
                });

            }else if (status.equals("rain")){
                Log.d(TAG, "handleMessage: rain");
                mHandler.post(rainRunnable = new Runnable() {
                    @Override
                    public void run() {
                        WeatherAnimation.rainAnimation(windspeed,DemoActivity.this,basecontainer,mWindowHeight,mWindowWidth);
                        mHandler.postDelayed(this, interval);
                    }
                });

            }else if (status.equals("thunder")){
                Log.d(TAG, "handleMessage: thunder");
                mHandler.post(thunderRunnable = new Runnable() {
                    @Override
                    public void run() {
                        WeatherAnimation.thunderAnimation(DemoActivity.this,basecontainer,mWindowHeight,mWindowWidth);
                        mHandler.postDelayed(this, interval);
                    }
                });
            }
        }

    };
}
