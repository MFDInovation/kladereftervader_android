package com.example.huaxie.kladervader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;



public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,View.OnClickListener{

    private final String TAG = "MainActivity";
    private TextView mTemp;
    private PercentRelativeLayout baseContainer;
    private ImageView baseBackground;
    private RelativeLayout animationContainer;
    private RelativeLayout tempContainer;
    private GPS mgps;
    private Weather mCurrentWeather;
    private ProgressBar progressBar;
    private ImageView portrait;
    private TextView egnaBilderButton;
    private ArrayList<String> mUriList;
    private ArrayList<Uri> mViewPagerList;
    private ViewPager mViewPager;
    private int tempContainerHeight;
    private Clothing.TempStatus tempKey;
    private int demoCounter = 0;
    public final static String ExtraMessage = "height";
    private Runnable thunderRunnable = null;
    private Runnable rainRunnable = null;
    private Runnable snowRunnable = null;
    private int mWindowHeight;
    private int mWindowWidth;
    private Weather demoWeather = mCurrentWeather;


    private WeatherAnimation mWeatherAnimation = null;

    protected static final int REQUEST_ACCESS_COURSE_LOCATION = 118;
    protected static final int ACTIVITY_RESULT_CODE = 1;
    public static final String gpsError = "gpsError";
    public static final String networkError = "networkError";
    public static final int demoNumber = 9;


    private PagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTemp = (TextView) findViewById(R.id.temp);

        //layout update
        baseContainer = (PercentRelativeLayout)findViewById(R.id.base_container);
        animationContainer = (RelativeLayout)findViewById(R.id.animation_container);
        baseBackground = (ImageView)findViewById(R.id.base_background);
        tempContainer = (RelativeLayout)findViewById(R.id.temp_container);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        portrait = (ImageView)findViewById(R.id.portrait_container);
        egnaBilderButton = (TextView)findViewById(R.id.egna_bilder_button);
        mViewPager = (ViewPager) findViewById(R.id.myViewPager);
        TextView mDemoButton = (TextView) findViewById(R.id.demo_button);
        mDemoButton.setOnClickListener(this);
        TextView utvaderaButton = (TextView)findViewById(R.id.evaluation_button);
        utvaderaButton.setOnClickListener(this);

        progressBar.setVisibility(View.VISIBLE);

//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        Set<String> oldDataSet  = preferences.getStringSet("UriSet",null);
//        if(oldDataSet != null){
//            mUriList = new ArrayList<String>(oldDataSet);
//            updateViewPager();
//        }else {
//            mUriList = new ArrayList<String>();
//        }

        mgps = new GPS(this,hasLocationListener);
        egnaBilderButton.setOnClickListener(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        mgps.stop();
        super.onStop();
    }

    public void getWeatherInfo(){
        mCurrentWeather = null;
        final Networking newNetworking = new Networking(new Networking.AsyncResponse(){
            @Override
            public void processFinish(final Weather weather, String error) {
                if(error != null){
                    showError(networkError);
                }
                mCurrentWeather = weather;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mCurrentWeather == null){
                            Toast.makeText(MainActivity.this,"Did not get the weather info,try again", Toast.LENGTH_LONG).show();
                            showError(networkError);
                        }else {
                            updateLayout(mCurrentWeather);
                            progressBar.setVisibility(View.GONE);
                            tempKey = Clothing.getStatus(weather);
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                            Set<String> oldDataSet  = preferences.getStringSet(tempKey.getName(),null);
                            if(oldDataSet != null){
                                mUriList = new ArrayList<String>(oldDataSet);
                                updateViewPager();
                            }else {
                                mUriList = new ArrayList<String>();
                            }
                        }
                    }
                });
            }
        });// get json object from weather web
        newNetworking.execute();
    }

    private void updateLayout(Weather weather){
        Log.d(TAG, "updateLayout: mycurrentWeather :" +  weather.toString());
        //update temp
        double temp =  weather.getTemperature();
        String temperature = (int)Math.round(temp) + "°";
        mTemp.setText(temperature);
        //update background
        WeatherSymbol.WeatherStatus status =  weather.getWeatherStatus();
        WeatherImage weatherImage = new WeatherImage();
        int id = weatherImage.getWeatherSymbolImage(status,weatherImage.getCurrentSeason());
        Log.d(TAG, "updateLayout: id" + id);
        baseBackground.setImageResource(id);
        baseBackground.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                baseBackground.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int imageHeight = baseBackground.getHeight();
                Log.d(TAG, "update: imageHeight" + imageHeight);
                tempContainerHeight = (int)Math.round(imageHeight*0.22);
                PercentRelativeLayout.LayoutParams params = new PercentRelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, tempContainerHeight);
                tempContainer.setLayoutParams(params);
                baseBackground.setVisibility(View.VISIBLE);
            }
        });

        tempKey = Clothing.getStatus(weather);
        Log.d(TAG, "updateLayout: tempkey" +tempKey);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Set<String> oldDataSet  = preferences.getStringSet(tempKey.getName(),null);
        if(oldDataSet != null && !oldDataSet.isEmpty()){
            mUriList = new ArrayList<String>(oldDataSet);
            updateViewPager();
        }else {
            mUriList = new ArrayList<String>();
            //update portrait
            recoverImageView();
            Clothing clothing = new Clothing();
            int portraitId = clothing.getClosingImage( weather);
            loadClothes(portrait,this,this,portrait.getHeight(),portrait.getWidth(),portraitId);
        }
        //start animation
        startAnimation(weather);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
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
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                        Set<String> oldDataSet  = preferences.getStringSet(tempKey.getName(),null);
                        if(oldDataSet != null&&!oldDataSet.isEmpty()){
                            mUriList = new ArrayList<String>(oldDataSet);
                            updateViewPager();
                        }else {
                            mUriList = new ArrayList<String>();
                            recoverImageView();
                        }
                        if(demoWeather != null){
                            startAnimation(demoWeather);
                        }
                        break;
                }
        }
    }

    private com.example.huaxie.kladervader.GPS.HasLocationListener hasLocationListener = new GPS.HasLocationListener() {

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
            case R.id.demo_button:
                stopAnimation();
                Log.d(TAG, "onClick: demobutton");
                demoCounter++;
                startDemo();
                break;
            case R.id.evaluation_button:
                Log.d(TAG, "onClick: evalutation button");
                evaluateApp();
            default:
                Log.d(TAG, "onClick: "+ view.toString()+ "is clicked");
        }
    }

    private void updateViewPager(){
        progressBar.setVisibility(View.INVISIBLE);
        portrait.setVisibility(View.GONE);
        mViewPagerList = changeStringListToUri(mUriList);
        if(!mViewPagerList.isEmpty()){
            adapterViewPager = new MyPagerAdapter(mViewPagerList,this);
            mViewPager.setAdapter(adapterViewPager);
            mViewPager.setVisibility(View.VISIBLE);
        }
    }

    private void recoverImageView(){
        mViewPager.setVisibility(View.GONE);
        portrait.setVisibility(View.VISIBLE);
    }

    private ArrayList<Uri> changeStringListToUri(ArrayList<String> mUriList){
        ArrayList<Uri> list = new ArrayList<Uri>();
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

        public MyPagerAdapter(ArrayList<Uri> dataList, Context context){
            this.dataList = dataList;
            this.mContext = context;
            this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mInflater.inflate(R.layout.picture_list_row, container, false);
            try{
                ImageView imageView = (ImageView) itemView.findViewById(R.id.picture_place_holder);
                Uri URI = dataList.get(position);
                String imagePath = BitmapWorkerTask.getPathFromImageUri(URI,mContext);
                BitmapWorkerTask ImageLoader = new BitmapWorkerTask(imageView);
                ImageLoader.execute(imagePath);
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
    }


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String status = bundle.getString("status");
            final int interval = bundle.getInt("interval");
            final int windspeed = bundle.getInt("windspeed");
            if(status.equals("snow")){
                Log.d(TAG, "handleMessage: snow");
                mHandler.post(snowRunnable = new Runnable() {
                    @Override
                    public void run() {
                        WeatherAnimation.snowAnimation(windspeed,MainActivity.this,animationContainer,mWindowHeight,mWindowWidth);
                        mHandler.postDelayed(this, interval);
                    }
                });

            }else if (status.equals("rain")){
                Log.d(TAG, "handleMessage: rain");
                mHandler.post(rainRunnable = new Runnable() {
                    @Override
                    public void run() {
                        WeatherAnimation.rainAnimation(windspeed,MainActivity.this,animationContainer,mWindowHeight,mWindowWidth);
                        mHandler.postDelayed(this, interval);
                    }
                });

            }else if (status.equals("thunder")){
                Log.d(TAG, "handleMessage: thunder");
                mHandler.post(thunderRunnable = new Runnable() {
                    @Override
                    public void run() {
                        WeatherAnimation.thunderAnimation(MainActivity.this,animationContainer,mWindowHeight,mWindowWidth);
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
//        int testSymbolvalue = 13;
//        int testTemp = 10;
//        double testwind = 5;
//        double testrain = 0.5;
//        Weather weather = new Weather(testSymbolvalue,testTemp,testrain,testwind);
        WeatherAnimation.setAnimationInterval(this,mCurrentWeather);
    }

    private void startDemo(){
        stopAnimation();
        int position = demoCounter%(demoNumber+1);
        demoWeather = getFakeWeather(position);
        updateLayout(demoWeather);
//        animationContainer.setVisibility(View.VISIBLE);
    }

    public Weather getFakeWeather(int position){
        Weather fakeWeather;
        switch (position){
            case 1:
                fakeWeather= new Weather(WeatherSymbol.WeatherStatus.ClearSky,3.0,0,0); //spring
                break;
            case 2:
                fakeWeather = new Weather(WeatherSymbol.WeatherStatus.Lightsleet, 1.0,0.75,5); //spring rain and snow
                break;
            case 3:
                fakeWeather = new Weather(WeatherSymbol.WeatherStatus.Lightsleet, 2.0,0.6,7); //spring rain and snow
                break;
            case 4:
                fakeWeather = new Weather(WeatherSymbol.WeatherStatus.ClearSky, 25.0,0.0,0.0); //summer
                break;
            case 5:
                fakeWeather = new Weather(WeatherSymbol.WeatherStatus.Thunder,20.0,0.75,6); //summer Thunder and rain
                break;
            case 6:
                fakeWeather = new Weather(WeatherSymbol.WeatherStatus.ClearSky, 14.0, 0,0); // autumn
                break;
            case 7:
                fakeWeather = new Weather(WeatherSymbol.WeatherStatus.Rain,10.0, 0.75, 0); //autumn rain
                break;
            case 8:
                fakeWeather = new Weather(WeatherSymbol.WeatherStatus.Snowshowers, -9, 0.75, 8); //winter
                break;
            case 9:
                fakeWeather = new Weather(WeatherSymbol.WeatherStatus.Cloudysky, -20,0,0); // winter no snow but cold
                break;
            default:
                fakeWeather = mCurrentWeather;
                break;
        }
        return fakeWeather;
    }

    public void stopAnimation(){
        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
        for(int index=0; index<((ViewGroup)animationContainer).getChildCount(); ++index) {
            View nextChild = ((ViewGroup)animationContainer).getChildAt(index);
            nextChild.clearAnimation();
            animationContainer.removeView(nextChild);
            animationContainer.invalidate();
        }

    }

    private void evaluateApp(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.mfd.se/kladereftervader"));
        startActivity(browserIntent);
    }

    private void showError(String key){
        progressBar.setVisibility(View.INVISIBLE);
        if(key.equals(gpsError)){
            baseBackground.setImageResource(R.mipmap.gps_error);
        }else if (key.equals(networkError)){
            baseBackground.setImageResource(R.mipmap.internet_error);
        }
    }
    private void loadClothes(ImageView imageView, Context mcontext, MainActivity activity, int height, int width, int resId){
        BitmapWorkerTaskDemo clothesLoader = new BitmapWorkerTaskDemo(imageView,mcontext,activity,height,width);
        clothesLoader.execute(resId);
    }
}
