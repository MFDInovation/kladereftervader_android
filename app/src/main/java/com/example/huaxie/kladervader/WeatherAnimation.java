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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import static com.example.huaxie.kladervader.JsonParser.TAG;
import static com.example.huaxie.kladervader.WeatherSymbol.WeatherStatus.Rain;
import static com.example.huaxie.kladervader.WeatherSymbol.WeatherStatus.Rainshowers;
import static com.example.huaxie.kladervader.WeatherSymbol.WeatherStatus.Thunder;
import static com.example.huaxie.kladervader.WeatherSymbol.WeatherStatus.Thunderstorm;

/**
 * Created by huaxie on 2017-01-11.
 */

public class WeatherAnimation {

    final static String TAG = "Animation";
    public static void createRandomImage(Weather weather, AppCompatActivity activity,
                                  PercentRelativeLayout fatherContainer, int height, int width){
        if(weather.getWeatherStatus() == Thunder || weather.getWeatherStatus()==Thunderstorm){
            doThunderAnimation(weather,activity,fatherContainer,height,width);
        }else {
            ArrayList values = getAnimationValues(weather);
            if(values != null){
                for (int i = 0; i < values.size(); i++) {
                    AnimationValues value = (AnimationValues) values.get(i);
                    int randomStartpoint = (int)Math.round(Math.random()*width);
                    ImageView animationItem= new ImageView(activity);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMarginStart(randomStartpoint);
                    animationItem.setLayoutParams(params);
                    animationItem.setImageResource(value.resId);
                    fatherContainer.addView(animationItem);
                    startAnimator(activity,animationItem,value,height,
                            fatherContainer);

                }
            }
        }
    }

    private static void doThunderAnimation(Weather weather,AppCompatActivity activity,
                                           PercentRelativeLayout fatherContainer, int height, int width){
        int randomStartpoint = (int)Math.round(Math.random()*width);
        ImageView animationItem= new ImageView(activity);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMarginStart(randomStartpoint);
        animationItem.setLayoutParams(params);
        animationItem.setImageResource(R.mipmap.lightning);
        fatherContainer.addView(animationItem);
        AnimationSet set = new AnimationSet(true);
        addThunderAnimatior(0.01f,1.0f,animationItem,set,100,fatherContainer,activity);
        addThunderAnimatior(1.0f,0.3f,animationItem,set,200,fatherContainer,activity);
        addThunderAnimatior(0.3f,1.0f,animationItem,set,100,fatherContainer,activity);
        addThunderAnimatior(1.0f,0f,animationItem,set,800,fatherContainer,activity);
        animationItem.startAnimation(set);
    }

    private static void addThunderAnimatior(float startAlpha, float endAlpha, final ImageView animationItem, AnimationSet set, int duration,
                                            final PercentRelativeLayout fatherContainer, final AppCompatActivity activity){
        AlphaAnimation animation1 = new AlphaAnimation(startAlpha, endAlpha);
        animation1.setDuration(duration);
        animation1.setFillAfter(true);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fatherContainer.removeView(animationItem);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        set.addAnimation(animation1);
    }

    private static void startAnimator(final AppCompatActivity activity, final ImageView animationItem,AnimationValues value, int height, final PercentRelativeLayout fatherContainer){
        /*TranslateAnimation newAnimation = new TranslateAnimation(animationItem.getX(),animationItem.getX(),0,fatherContainer.getBottom());
        newAnimation.setDuration(value.duration);
        newAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fatherContainer.post(new Runnable() {
                    public void run () {
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                fatherContainer.removeView(animationItem);
                            }
                        });
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationItem.startAnimation(newAnimation);*/
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(animationItem, "translationY",
                0,height);
        objectAnimator.setDuration(value.duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
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

    public static ArrayList<AnimationValues> getAnimationValues(Weather mCurrentWeather){
        ArrayList<AnimationValues> values = new ArrayList<>();
        int windSpeed = (int)Math.round(Math.min(mCurrentWeather.windSpeed/3,2));
        int intensity;
        if(mCurrentWeather.rainfall < 0.1)
        {
            return null;
        }else{
            double rainfall = Math.max(mCurrentWeather.rainfall,0.7) ;
            WeatherSymbol.WeatherStatus status = mCurrentWeather.getWeatherStatus();
            switch (status){
                case Snowfall:
                    intensity = (int)(Math.round(Math.min(rainfall*20, 200.0)));
                    values.add(new AnimationValues(R.mipmap.snow,6000,windSpeed,intensity));
                    break;
                case Snowshowers:
                    intensity = (int)(Math.round(Math.min(rainfall*20, 200.0)));
                    values.add(new AnimationValues(R.mipmap.snow,6000,windSpeed,intensity));
                    break;
                case Rain:
                    intensity = (int)(Math.round(Math.min(rainfall*10, 100.0)));
                    values.add(new AnimationValues(R.mipmap.rain,400,windSpeed,intensity));
                    break;
                case Rainshowers:
                    intensity = (int)(Math.round(Math.min(rainfall*10, 100.0)));
                    values.add(new AnimationValues(R.mipmap.rain,400,windSpeed,intensity));
                    break;
                case Sleet:
                    intensity = (int)(Math.round(Math.min(rainfall*5, 50.0)));
                    values.add(new AnimationValues(R.mipmap.snow,6000,windSpeed,intensity));
                    intensity = (int)(Math.round(Math.min(rainfall*10, 100.0)));
                    values.add(new AnimationValues(R.mipmap.rain,400,windSpeed,intensity));
                    break;
                case Lightsleet:
                    intensity = (int)(Math.round(Math.min(rainfall*5, 50.0)));
                    values.add(new AnimationValues(R.mipmap.snow,6000,windSpeed,intensity));
                    intensity = (int)(Math.round(Math.min(rainfall*10, 100.0)));
                    values.add(new AnimationValues(R.mipmap.rain,400,windSpeed,intensity));
                    break;
            }
        }
        return values;
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
}
