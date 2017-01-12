package com.example.huaxie.kladervader;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.percent.PercentRelativeLayout;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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

    public static void createRandomImage(int resId, Context mContext,
                                  PercentRelativeLayout fatherContainer, int height, int width){
        int randomStartpoint = (int)Math.round(Math.random()*width);
        ImageView animationItem= new ImageView(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMarginStart(randomStartpoint);
        animationItem.setLayoutParams(params);
        animationItem.setImageResource(resId);
        fatherContainer.addView(animationItem);

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(animationItem, "translationY",
                0,height);
        objectAnimator.setDuration(6000);
        objectAnimator.start();
    }

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
//    public int getResourceId(){
//        WeatherSymbol.WeatherStatus status = mCurrentWeather.getWeatherStatus();
//        int returnv= 0;
//        switch (status){
//            case Snowshowers:
//                returnv = R.mipmap.snow;
//                break;
//
//        }
//        return returnv;
//    }
//
//    private void setIntensity(int value){
//        this.intensity = value;
//    }
//
//    public int getIntensity() {
//        return intensity;
//    }
}
