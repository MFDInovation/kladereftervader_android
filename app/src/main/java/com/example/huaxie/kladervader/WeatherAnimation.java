package com.example.huaxie.kladervader;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import static com.example.huaxie.kladervader.JsonParser.TAG;
import static com.example.huaxie.kladervader.WeatherSymbol.WeatherStatus.Rain;
import static com.example.huaxie.kladervader.WeatherSymbol.WeatherStatus.Rainshowers;

/**
 * Created by huaxie on 2017-01-11.
 */

public class WeatherAnimation {
    /*private Weather mCurrentWeather;
    private Context mContext;
    private int mWindowHeight;
    private int mWindowWidth;
    private ImageView animationItem;
    private PercentRelativeLayout fatherContainer;
    private int intensity;
    private ObjectAnimator objectAnimator;

    public WeatherAnimation(Weather mCurrentWeather, Context mContext, PercentRelativeLayout baseContainer){
        this.mCurrentWeather = mCurrentWeather;
        this.mContext = mContext;
        this.fatherContainer = baseContainer;
        getWindowSize();
    }*/


   /* public void start(){
//        createRandomImage(resID);
//        animationItem.setImageResource(resID);
        objectAnimator = ObjectAnimator.ofFloat(animationItem, "translationY",
                0, mWindowHeight);
        objectAnimator.setDuration(6000);
        /*objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                animationItem.getAnimation().cancel();
                fatherContainer.removeView(animationItem);
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

//        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//
//            }
//        });
        objectAnimator.start();
    }*/
    final static String TAG = "Animation";
    public static void createRandomImage(Weather weather, AppCompatActivity activity,
                                  PercentRelativeLayout fatherContainer, int height, int width){
        int[] resId = getResourceId(weather);
        int randomStartpoint = (int)Math.round(Math.random()*width);
        ImageView animationItem= new ImageView(activity);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMarginStart(randomStartpoint);
        animationItem.setLayoutParams(params);
        animationItem.setImageResource(resId[0]);
        fatherContainer.addView(animationItem);
        startAnimator(activity,animationItem, height,
                fatherContainer);


    }

    private static void startAnimator(final AppCompatActivity activity, final ImageView animationItem, int height, final PercentRelativeLayout fatherContainer){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(animationItem, "translationY",
                0,height);
        objectAnimator.setDuration(6000);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                fatherContainer.post(new Runnable() {
                    public void run () {
                        // it works without the runOnUiThread, but all UI updates must
                        // be done on the UI thread
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                fatherContainer.removeView(animationItem);
                            }
                        });
                    }
                });
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        objectAnimator.start();
    }

//    public static int[] getResourceId(Weather weather){
//        int[] idList = new int[2];
//        return idList;
//    }

//    private void getWindowSize(){
//        DisplayMetrics displaymetrics = mContext.getResources().getDisplayMetrics();
//        mWindowWidth = displaymetrics.widthPixels;
//        mWindowHeight = displaymetrics.heightPixels;
//    }

//    public void start(){
//        double rainfall;
//        double windSpeed;
//        if(mCurrentWeather.getRainfall() < 0.1){return;
//        } else{
//            // make sure the animation is visible, even for low amounts
//            rainfall = Math.max(mCurrentWeather.getRainfall(),0.7);
//            windSpeed = Math.min(mCurrentWeather.getRainfall()/3,2);
//        }
//        WeatherSymbol.WeatherStatus status = mCurrentWeather.getWeatherStatus();
//        switch (status){
//            case Rain:
//                createAnimation(R.mipmap.rain, 400);
//                setIntensity((int)Math.min(rainfall*10,100.0));
//                break;
//            case Rainshowers:
//                createAnimation(R.mipmap.rain, 400);
//                setIntensity((int)Math.min(rainfall*10,100.0));
//                break;
//            case Snowfall:
//                createAnimation(R.mipmap.snow, 6000);
////                setIntensity((int)Math.min(rainfall*20,200.0));
//                break;
//            case Snowshowers:
//                createAnimation(R.mipmap.snow, 6000);
//                setIntensity((int)Math.min(rainfall*20,200.0));
//                break;
//            case Sleet:
////                createAnimation(R.mipmap.rain, 400);
////                createAnimation(R.mipmap.snow, 6000);
//                break;
//            case Lightsleet:
//                break;
//            case Thunder:
//                break;
//            case Thunderstorm:
//                break;
//            default:
//                break;
//        }
//    }
//
    public static ArrayList<AnimationValues> getResourceId(Weather mCurrentWeather){
        /*if(mCurrentWeather.rainfall < 0.1)
        {
            return null;
        }*/
        double rainfall = Math.max(mCurrentWeather.rainfall,0.7) ;
        WeatherSymbol.WeatherStatus status = mCurrentWeather.getWeatherStatus();
        ArrayList<AnimationValues> values = null;
        int windSpeed = (int)Math.round(Math.min(mCurrentWeather.windSpeed/3,2));
        int intensity;
        switch (status){
            case Snowfall:
                intensity = (int)(Math.round(Math.min(rainfall*20, 200.0)));
                values.add(new AnimationValues(R.mipmap.snow,6000,windSpeed,intensity));
                break;
            case Snowshowers:
                returnv[0] = R.mipmap.snow;
                break;
            case Rain:
                returnv[0] = R.mipmap.rain;
                break;
            case Rainshowers:
                returnv[0] = R.mipmap.rain;
                break;
            case Sleet:
                returnv[0] = R.mipmap.snow;
                returnv[1] = R.mipmap.rain;
                break;
            case Lightsleet:
                returnv[0] = R.mipmap.snow;
                returnv[1] = R.mipmap.rain;
                break;
            case Thunder:
//                returnv[0] = R.mipmap.lightning;
                break;
            case Thunderstorm:
//                returnv[0] = R.mipmap.lightning;
                break;
        }
        return returnv;
    }

    public static class AnimationValues{
        public int resId;
        public int duration;
        public int windSpeed;
        public int intensity;

        public AnimationValues(int resId,int duration,int windSpeed,int intensity){
            this.resId = resId;
            this.duration = duration;
            this.windSpeed = windSpeed;
            this.intensity = intensity;
        }
    }
//
//    private void setIntensity(int value){
//        this.intensity = value;
//    }
//
//    public int getIntensity() {
//        return intensity;
//    }
}
