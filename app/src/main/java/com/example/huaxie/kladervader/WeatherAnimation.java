package com.example.huaxie.kladervader;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.huaxie.kladervader.JsonParser.TAG;
import static com.example.huaxie.kladervader.MainActivity.*;
import static com.example.huaxie.kladervader.WeatherSymbol.WeatherStatus.Rain;
import static com.example.huaxie.kladervader.WeatherSymbol.WeatherStatus.Rainshowers;
import static com.example.huaxie.kladervader.WeatherSymbol.WeatherStatus.Thunder;
import static com.example.huaxie.kladervader.WeatherSymbol.WeatherStatus.Thunderstorm;

/**
 * Created by huaxie on 2017-01-11.
 */

public class WeatherAnimation {

    final static String TAG = "Animation";

    public static void snowAnimation(int windSpeed, AppCompatActivity activity,
                                     PercentRelativeLayout fatherContainer, int height, int width){
        int snowDuration = 6000;
        ImageView animationItem = createRandomImage(activity,fatherContainer,height,width,R.mipmap.snow);
        doWeatherAnimation(activity, animationItem, fatherContainer,windSpeed,snowDuration);
    }

    public static void rainAnimation(int windSpeed, AppCompatActivity activity,
                                     PercentRelativeLayout fatherContainer, int height, int width){
        int rainDuration = 400;
        ImageView animationItem = createRandomImage(activity,fatherContainer,height,width,R.mipmap.rain);
        doWeatherAnimation(activity,animationItem,fatherContainer,windSpeed,rainDuration);
    }

    public static void thunderAnimation(AppCompatActivity activity,
                                     PercentRelativeLayout fatherContainer, int height, int width){
        ImageView animationItem = createRandomImage(activity,fatherContainer,height,width,R.mipmap.lightning);
        doThunderAnimation(animationItem,activity,fatherContainer);
    }


    public static ImageView createRandomImage(AppCompatActivity activity,
                                  PercentRelativeLayout fatherContainer, int height, int width, int resId){
        int randomStartpoint = (int)Math.round(Math.random()*width);
        ImageView animationItem= new ImageView(activity);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMarginStart(randomStartpoint);
        animationItem.setLayoutParams(params);
        animationItem.setImageResource(resId);
        fatherContainer.addView(animationItem);
        return animationItem;
    }

    private static void doThunderAnimation(ImageView animationItem,AppCompatActivity activity,
                                           PercentRelativeLayout fatherContainer){
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

    private static void doWeatherAnimation(final AppCompatActivity activity, final ImageView animationItem,
                                           final PercentRelativeLayout fatherContainer, int windSpeed, int duration){
        float endX =  animationItem.getX()-(float) Math.random()*windSpeed;
        TranslateAnimation newAnimation = new TranslateAnimation(animationItem.getX(),endX,0,fatherContainer.getBottom());
        newAnimation.setDuration(duration);
        newAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                activity.runOnUiThread(new Runnable() {
                    public void run () {
                        fatherContainer.removeView(animationItem);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationItem.startAnimation(newAnimation);
    }

    public static void setAnimationInterval(MainActivity activity, Weather weather){
        int interval = 0;
        int intensity = 0;
        int windSpeed = 0;
        Bundle rainbundle = new Bundle();
        Bundle snowbundle = new Bundle();
        double rainfall = Math.max(weather.rainfall,0.7) ;
        if(rainfall < 0.1)
        {
            return;
        }
        WeatherSymbol.WeatherStatus status = weather.getWeatherStatus();
        final int snowDuration = 6000;
        final int rainDuration = 400;
        switch (status){
            case Snowfall:
            case Snowshowers:
                intensity = (int)(Math.round(Math.min(rainfall*20, 200.0)));
                interval = Math.round(6000/intensity);
                windSpeed = (int)Math.round(Math.min(weather.windSpeed/3,2));
                snowbundle.putString("status","snow");
                snowbundle.putInt("interval",interval);
                snowbundle.putInt("windspeed", windSpeed);
                Message snowmsg = new Message();
                snowmsg.setData(snowbundle);
                activity.getHandler().sendMessage(snowmsg);
                break;
            case Rain:
            case Rainshowers:
                intensity = (int)(Math.round(Math.min(rainfall*10, 100.0)));
                interval = Math.round(400/intensity);
                windSpeed = (int)Math.round(Math.min(weather.windSpeed/3,2));
                rainbundle.putString("status","rain");
                rainbundle.putInt("interval",interval);
                rainbundle.putInt("windspeed", windSpeed);
                Message rainmsg = new Message();
                rainmsg.setData(rainbundle);
                activity.getHandler().sendMessage(rainmsg);
                break;
            case Sleet:
            case Lightsleet:
                intensity = (int)(Math.round(Math.min(rainfall*5, 50.0)));
                interval = Math.round(6000/intensity);
                windSpeed = (int)Math.round(Math.min(weather.windSpeed/3,2));
                snowbundle.putString("status","snow");
                snowbundle.putInt("interval",interval);
                snowbundle.putInt("windspeed", windSpeed);
                Message msg1 = new Message();
                msg1.setData(snowbundle);
                activity.getHandler().sendMessage(msg1);

                intensity = (int)(Math.round(Math.min(rainfall*10, 100.0)));
                interval = Math.round(400/intensity);
                windSpeed = (int)Math.round(Math.min(weather.windSpeed/3,2));
                rainbundle.putString("status","rain");
                rainbundle.putInt("interval",interval);
                rainbundle.putInt("windspeed", windSpeed);
                Message msg2 = new Message();
                msg2.setData(rainbundle);
                activity.getHandler().sendMessage(msg2);
                break;
            case Thunder:
            case Thunderstorm:
                interval = (int)(Math.round(Math.random()*2000 + 5000));
                windSpeed = 0;
                Bundle thunderBundle = new Bundle();
                thunderBundle.putString("status","thunder");
                thunderBundle.putInt("interval",interval);
                thunderBundle.putInt("windspeed", windSpeed);
                Message thunderMsg = new Message();
                thunderMsg.setData(thunderBundle);
                activity.getHandler().sendMessage(thunderMsg);
                break;
            default:
                break;
        }
    }

    private static class AnimTimerTask extends TimerTask {
        private Handler mHandler;
        private String status;
        @Override
        public void run() {
            Bundle bundle = new Bundle();
            bundle.putString("status",status);
            Message msg = new Message();
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }

        private AnimTimerTask(Handler mHandler, String status){
            this.mHandler = mHandler;
            this.status = status;
        }
    }

    public static void setAnimationIntervalDemo(DemoActivity activity, Weather weather){
        int interval = 0;
        int intensity = 0;
        int windSpeed = 0;
        Bundle rainbundle = new Bundle();
        Bundle snowbundle = new Bundle();
        double rainfall = Math.max(weather.rainfall,0.7) ;
        if(rainfall < 0.1)
        {
            return;
        }
        WeatherSymbol.WeatherStatus status = weather.getWeatherStatus();
        final int snowDuration = 6000;
        final int rainDuration = 400;
        switch (status){
            case Snowfall:
            case Snowshowers:
                intensity = (int)(Math.round(Math.min(rainfall*20, 200.0)));
                interval = Math.round(6000/intensity);
                windSpeed = (int)Math.round(Math.min(weather.windSpeed/3,2));
                snowbundle.putString("status","snow");
                snowbundle.putInt("interval",interval);
                snowbundle.putInt("windspeed", windSpeed);
                Message snowmsg = new Message();
                snowmsg.setData(snowbundle);
                activity.getHandler().sendMessage(snowmsg);

                break;
            case Rain:
            case Rainshowers:
                intensity = (int)(Math.round(Math.min(rainfall*10, 100.0)));
                interval = Math.round(400/intensity);
                windSpeed = (int)Math.round(Math.min(weather.windSpeed/3,2));
                rainbundle.putString("status","rain");
                rainbundle.putInt("interval",interval);
                rainbundle.putInt("windspeed", windSpeed);
                Message rainmsg = new Message();
                rainmsg.setData(rainbundle);
                activity.getHandler().sendMessage(rainmsg);
                break;
            case Sleet:
            case Lightsleet:
                intensity = (int)(Math.round(Math.min(rainfall*5, 50.0)));
                interval = Math.round(6000/intensity);
                windSpeed = (int)Math.round(Math.min(weather.windSpeed/3,2));
                snowbundle.putString("status","snow");
                snowbundle.putInt("interval",interval);
                snowbundle.putInt("windspeed", windSpeed);
                Message msg1 = new Message();
                msg1.setData(snowbundle);
                activity.getHandler().sendMessage(msg1);
                intensity = (int)(Math.round(Math.min(rainfall*10, 100.0)));
                interval = Math.round(400/intensity);
                windSpeed = (int)Math.round(Math.min(weather.windSpeed/3,2));
                rainbundle.putString("status","rain");
                rainbundle.putInt("interval",interval);
                rainbundle.putInt("windspeed", windSpeed);
                Message msg2 = new Message();
                msg2.setData(rainbundle);
                activity.getHandler().sendMessage(msg2);
                break;
            case Thunder:
            case Thunderstorm:
                interval = (int)(Math.round(Math.random()*2000 + 5000));
                windSpeed = 0;
                Bundle thunderBundle = new Bundle();
                thunderBundle.putString("status","thunder");
                thunderBundle.putInt("interval",interval);
                thunderBundle.putInt("windspeed", windSpeed);
                Message thunderMsg = new Message();
                thunderMsg.setData(thunderBundle);
                activity.getHandler().sendMessage(thunderMsg);
                break;
            default:
                break;
        }
    }

}
