package com.example.huaxie.kladervader;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
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

public class Networking extends AsyncTask< String, String, String > {

    private final static String TAG = "Networking";
    private URL weatherInfoURL;
    private String dataBuffer;


    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String longitude = String.format("%.3f",MainActivity.getlongitude());
            String latitude = String.format("%.3f",MainActivity.getlatitude());
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
            getWeather(getJsonObject());
            return dataBuffer;


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
                e.printStackTrace();
            }
        }
        return null;
    }

    public JSONObject getJsonObject() {
        JSONObject obj = null;
        try {
            obj = new JSONObject(dataBuffer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public void getWeather(JSONObject collectedData) {
        JsonParser jsParser = new JsonParser(collectedData);

        Log.d(TAG, "getWeather: " + jsParser.getWeather().toString());
    }

}

