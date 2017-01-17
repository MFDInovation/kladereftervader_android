package com.example.huaxie.kladervader;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * Created by huaxie on 2017-01-02.
 */

public class Networking extends AsyncTask< String, String, Weather > {

    private final static String TAG = "Networking";
    private URL weatherInfoURL;
    private String dataBuffer;
    private Weather mWeather;
    private String error = null;

    public static final String errorMessage = "error";

    public interface AsyncResponse {
        void processFinish(Weather weather, String error);
    }

    public AsyncResponse delegate = null;

    public Networking (AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected Weather doInBackground(String... strings) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String longitude = String.format("%.3f",GPS.getlongitude());
            String latitude = String.format("%.3f",GPS.getlatitude());
            weatherInfoURL = new URL(String.format("http://opendata-download-metfcst.smhi.se/api/category/pmp2g/version/2/geotype/point/lon/%s/lat/%s/data.json",
                    longitude, latitude));
            connection = (HttpURLConnection) weatherInfoURL.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line+"\n");
            }
            dataBuffer = buffer.toString();
            return parseWeather(getJsonObject(dataBuffer));

        } catch (MalformedURLException e) {
            error = errorMessage;
            e.printStackTrace();
        } catch (IOException e) {
            error = errorMessage;
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                error = errorMessage;
                e.printStackTrace();
            }
        }
        return null;
    }

    public JSONObject getJsonObject(String dataBuffer) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(dataBuffer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public Weather parseWeather(JSONObject dataBufferObj) {
        JsonParser jsParser = new JsonParser(dataBufferObj);
        //Log.d(TAG, "getWeather: " + jsParser.getWeather().toString());
        mWeather = jsParser.getWeather();
        return mWeather;
    }

    public Weather getmWeather(){
        return mWeather;
    }

    @Override
    protected void onPostExecute(Weather weather) {
        delegate.processFinish(weather,error);
    }
}

