package com.example.huaxie.kladervader;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by huaxie on 2017-01-02.
 */

class Networking extends AsyncTask< String, String, Weather > {

    private final static String TAG = "Networking";
    private String error = null;

    private static final String errorMessage = "error";

    interface AsyncResponse {
        void processFinish(Weather weather, String error);
    }

    private AsyncResponse delegate = null;

    Networking(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected Weather doInBackground(String... strings) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String longitude = String.format("%.3f",GPS.getlongitude());
            String latitude = String.format("%.3f",GPS.getlatitude());
            URL weatherInfoURL = new URL(String.format("http://opendata-download-metfcst.smhi.se/api/category/pmp2g/version/2/geotype/point/lon/%s/lat/%s/data.json",
                    longitude, latitude));
            connection = (HttpURLConnection) weatherInfoURL.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuilder buffer = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            String dataBuffer = buffer.toString();
            return parseWeather(getJsonObject(dataBuffer));

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

    private JSONObject getJsonObject(String dataBuffer) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(dataBuffer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    private Weather parseWeather(JSONObject dataBufferObj) {
        JsonParser jsParser = new JsonParser(dataBufferObj);
        //Log.d(TAG, "getWeather: " + jsParser.getWeather().toString());
        return jsParser.getWeather();
    }

    @Override
    protected void onPostExecute(Weather weather) {
        delegate.processFinish(weather,error);
    }
}

