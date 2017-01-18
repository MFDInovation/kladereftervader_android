package com.example.huaxie.kladervader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

/**
 * Created by huaxie on 2017-01-11.
 */

class WeatherAnimation {

    private final static String TAG = "Animation";
    private final static int small = 50;
    private final static int medium = 100;
    final static int large = 150;
    private final static int xlarge = 200;
    private final static int longDuration = 6000;
    private final static int shortDuration = 400;
    private final static int windScale = 100;
    private static final int smallImageHeight = 27;
    private static final int longImageHeight = 137;
    public static final Random random = new Random();

    static void snowAnimation(int windSpeed, AppCompatActivity activity,
                              RelativeLayout fatherContainer, int height, int width, Bitmap image){
        int snowDuration = longDuration;
        int snowHeight = smallImageHeight + random.nextInt(smallImageHeight);

        ImageView animationItem = createRandomImage(activity,fatherContainer,height,width,image,snowHeight);
        doWeatherAnimation(activity, animationItem, fatherContainer,windSpeed,snowDuration);
    }

    static void rainAnimation(int windSpeed, AppCompatActivity activity,
                              RelativeLayout fatherContainer, int height, int width, Bitmap image){
        int rainDuration = shortDuration;
        int rainHeight = longImageHeight + random.nextInt(longImageHeight);
        ImageView animationItem = createRandomImage(activity,fatherContainer,height,width,image,rainHeight);
        doWeatherAnimation(activity,animationItem,fatherContainer,windSpeed,rainDuration);
    }

    static void thunderAnimation(AppCompatActivity activity,
                                 RelativeLayout fatherContainer, int height, int width, Bitmap image){
        ImageView animationItem = createRandomImage(activity,fatherContainer,height,width,image,0);
        doThunderAnimation(animationItem,activity,fatherContainer);
    }


    private static ImageView createRandomImage(AppCompatActivity activity,
                                               RelativeLayout fatherContainer, int height, int width, Bitmap image, int randomHeight){
        int randomStartpoint = random.nextInt(width);
        ImageView animationItem= new ImageView(activity);
        RelativeLayout.LayoutParams params;
        if(randomHeight == 0 ){
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }else {
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,randomHeight);
        }
        params.setMarginStart(randomStartpoint);
        animationItem.setLayoutParams(params);
        animationItem.setImageBitmap(image);
        fatherContainer.addView(animationItem);
        return animationItem;
    }

    private static void doThunderAnimation(ImageView animationItem,AppCompatActivity activity,
                                           RelativeLayout fatherContainer){
        AnimationSet set = new AnimationSet(true);
        addThunderAnimatior(0.01f,1.0f,animationItem,set,100,fatherContainer,activity);
        addThunderAnimatior(1.0f,0.3f,animationItem,set,200,fatherContainer,activity);
        addThunderAnimatior(0.3f,1.0f,animationItem,set,100,fatherContainer,activity);
        addThunderAnimatior(1.0f,0f,animationItem,set,800,fatherContainer,activity);
        animationItem.startAnimation(set);
    }

    private static void addThunderAnimatior(float startAlpha, float endAlpha, final ImageView animationItem, AnimationSet set, int duration,
                                            final RelativeLayout fatherContainer, final AppCompatActivity activity){
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
                                           final RelativeLayout fatherContainer, int windSpeed, int duration){
        float moveX = random.nextInt(windSpeed);
        float endX =  animationItem.getX()- moveX;
        /*float px = animationItem.getPivotX();
        float py = animationItem.getPivotY();
        float degree = (float) Math.asin(moveX/(animationItem.getHeight()/2.0))*360;
        Matrix matrix = new Matrix();
        animationItem.setScaleType(ImageView.ScaleType.MATRIX);   //required
        matrix.postRotate((float) degree, px, py);
        animationItem.setImageMatrix(matrix);*/

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

    static void setAnimationInterval(MainActivity activity, Weather weather){
        Bundle rainbundle = new Bundle();
        Bundle snowbundle = new Bundle();
        if(weather.rainfall < 0.1)
        {
            return;
        }
        double rainfall = weather.rainfall ;
        WeatherSymbol.WeatherStatus status = weather.getWeatherStatus();
        switch (status){
            case Snowfall:
            case Snowshowers:
                sendSnowMessage(activity, weather, snowbundle, rainfall, xlarge);
                break;
            case Rain:
            case Rainshowers:
                sendRainMessage(activity, weather, rainbundle, rainfall,medium);
                break;
            case Sleet:
            case Lightsleet:
                sendSleetMessage(activity, weather, rainbundle, snowbundle, rainfall,medium,small);
                break;
            case Thunder:
            case Thunderstorm:
                sendThunderMessage(activity);
                if(weather.temperature > 0)
                {
                    sendRainMessage(activity, weather, rainbundle, rainfall,medium);
                }else {
                    sendSnowMessage(activity, weather, snowbundle, rainfall,medium);
                }
                break;
            default:
                break;
        }
    }

    private static void sendThunderMessage(MainActivity activity) {
        int interval;
        int windSpeed;
        interval = random.nextInt(2000) + 2000;
        windSpeed = 0;
        Bundle thunderBundle = new Bundle();
        thunderBundle.putString("status","thunder");
        thunderBundle.putInt("interval",interval);
        thunderBundle.putInt("windspeed", windSpeed);
        thunderBundle.putParcelable("picture",getBitmap(activity,R.mipmap.lightning));
        Message thunderMsg = new Message();
        thunderMsg.setData(thunderBundle);
        activity.getHandler().sendMessage(thunderMsg);
    }

    private static void sendSleetMessage(MainActivity activity, Weather weather, Bundle rainbundle, Bundle snowbundle, double rainfall, int medium, int small) {
        int intensity;
        int interval;
        int windSpeed;
        intensity = (int)(Math.round(Math.min(rainfall*medium/10, medium))+medium);
        interval = longDuration/intensity;
        windSpeed = (int)Math.round(weather.windSpeed)*windScale;
        snowbundle.putString("status","snow");
        snowbundle.putInt("interval",interval);
        snowbundle.putInt("windspeed", windSpeed);
        snowbundle.putParcelable("picture",getBitmap(activity,R.mipmap.snow));
        Message msg1 = new Message();
        msg1.setData(snowbundle);
        activity.getHandler().sendMessage(msg1);

        intensity = (int)(Math.round(Math.min(rainfall*small/10, small))+small);
        interval = Math.round(shortDuration/intensity);
        windSpeed = (int)Math.round(Math.min(weather.windSpeed/3,2));
        rainbundle.putString("status","rain");
        rainbundle.putInt("interval",interval);
        rainbundle.putInt("windspeed", windSpeed);
        rainbundle.putParcelable("picture",getBitmap(activity,R.mipmap.rain));
        Message msg2 = new Message();
        msg2.setData(rainbundle);
        activity.getHandler().sendMessage(msg2);
    }

    private static void sendRainMessage(MainActivity activity, Weather weather, Bundle rainbundle, double rainfall,int medium) {
        int intensity;
        int interval;
        int windSpeed;
        intensity = (int)(Math.round(Math.min(rainfall*medium/10, medium)) + medium);
        interval = shortDuration/intensity;
        windSpeed = (int)Math.round(weather.windSpeed)*windScale;
        rainbundle.putString("status","rain");
        rainbundle.putInt("interval",interval);
        rainbundle.putInt("windspeed", windSpeed);
        rainbundle.putParcelable("picture",getBitmap(activity,R.mipmap.rain));
        Message rainmsg = new Message();
        rainmsg.setData(rainbundle);
        activity.getHandler().sendMessage(rainmsg);
    }

    private static void sendSnowMessage(MainActivity activity, Weather weather, Bundle snowbundle, double rainfall, int xlarge) {
        int intensity;
        int interval;
        int windSpeed;
        intensity = (int)(Math.round(Math.min(rainfall*xlarge/10, xlarge))+xlarge);
        interval = longDuration/intensity;
        Log.d(TAG, "sendSnowMessage: interval " + interval);
        windSpeed = (int)Math.round(weather.windSpeed)*windScale;
        snowbundle.putString("status","snow");
        snowbundle.putInt("interval",interval);
        snowbundle.putInt("windspeed", windSpeed);
        snowbundle.putParcelable("picture",getBitmap(activity,R.mipmap.snow));
        Message snowmsg = new Message();
        snowmsg.setData(snowbundle);
        activity.getHandler().sendMessage(snowmsg);
    }

    private static Bitmap getBitmap(Activity activity, int id){
        return BitmapFactory.decodeResource(activity.getResources(),id);
    }
}
