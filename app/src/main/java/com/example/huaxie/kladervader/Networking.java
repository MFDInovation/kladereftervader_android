package com.example.huaxie.kladervader;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

/**
 * Created by huaxie on 2017-01-02.
 */

class Networking extends AsyncTask< String, String, Weather > {

    private final static String TAG = "Networking";
    private Exception exception = null;

    private static final String errorMessage = "exception";

    interface AsyncResponse {
        void processFinish(Weather weather, Exception exception);
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
            Reader inputStreamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            reader = new BufferedReader(inputStreamReader);

            StringBuilder buffer = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            String dataBuffer = buffer.toString();
            return parseWeather(getJsonObject(dataBuffer));

        } catch (IOException | ParseException | JSONException e) {
            exception = e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    private Weather parseWeather(JSONObject dataBufferObj) throws JSONException, ParseException {
        JsonParser jsParser = new JsonParser(dataBufferObj);
        //Log.d(TAG, "getWeather: " + jsParser.getWeather().toString());
        return jsParser.getWeather();
    }

    @Override
    protected void onPostExecute(Weather weather) {
        delegate.processFinish(weather, exception);
    }
}

