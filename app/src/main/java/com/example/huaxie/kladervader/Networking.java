package com.example.huaxie.kladervader;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
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

public class Networking extends AsyncTask< String, String, String > {

    private final static String TAG = "Networking";
    private URL weatherInfoURL;
    private String dataBuffer;
    private Context context;


    @Override
    protected String doInBackground(String... strings) {
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
            getWeather(getJsonObject(dataBuffer));
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

    /*test data
    @Override
    protected String doInBackground(String... strings) {
        String textData = "{\"approvedTime\":\"2017-01-03T12:57:08Z\",\"referenceTime\":\"2017-01-03T10:00:00Z\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[[18.073028,59.343199]]},\"timeSeries\":[{\"validTime\":\"2017-01-03T11:00:00Z\",\"parameters\":[{\"name\":\"msl\",\"levelType\":\"hmsl\",\"level\":0,\"unit\":\"hPa\",\"values\":[1001]},{\"name\":\"t\",\"levelType\":\"hl\",\"level\":2,\"unit\":\"Cel\",\"values\":[-4.1]},{\"name\":\"vis\",\"levelType\":\"hl\",\"level\":2,\"unit\":\"km\",\"values\":[56.0]},{\"name\":\"wd\",\"levelType\":\"hl\",\"level\":10,\"unit\":\"degree\",\"values\":[203]},{\"name\":\"ws\",\"levelType\":\"hl\",\"level\":10,\"unit\":\"m/s\",\"values\":[2.2]},{\"name\":\"r\",\"levelType\":\"hl\",\"level\":2,\"unit\":\"percent\",\"values\":[78]},{\"name\":\"tstm\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"percent\",\"values\":[0]},{\"name\":\"tcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[5]},{\"name\":\"lcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[0]},{\"name\":\"mcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[0]},{\"name\":\"hcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[4]},{\"name\":\"gust\",\"levelType\":\"hl\",\"level\":10,\"unit\":\"m/s\",\"values\":[4.9]},{\"name\":\"pmin\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.0]},{\"name\":\"pmax\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.0]},{\"name\":\"spp\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"percent\",\"values\":[-9]},{\"name\":\"pcat\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"category\",\"values\":[0]},{\"name\":\"pmean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.0]},{\"name\":\"pmedian\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.0]},{\"name\":\"Wsymb\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"category\",\"values\":[3]}]},{\"validTime\":\"2017-01-03T12:00:00Z\",\"parameters\":[{\"name\":\"msl\",\"levelType\":\"hmsl\",\"level\":0,\"unit\":\"hPa\",\"values\":[999]},{\"name\":\"t\",\"levelType\":\"hl\",\"level\":2,\"unit\":\"Cel\",\"values\":[-3.7]},{\"name\":\"vis\",\"levelType\":\"hl\",\"level\":2,\"unit\":\"km\",\"values\":[51.0]},{\"name\":\"wd\",\"levelType\":\"hl\",\"level\":10,\"unit\":\"degree\",\"values\":[183]},{\"name\":\"ws\",\"levelType\":\"hl\",\"level\":10,\"unit\":\"m/s\",\"values\":[2.0]},{\"name\":\"r\",\"levelType\":\"hl\",\"level\":2,\"unit\":\"percent\",\"values\":[75]},{\"name\":\"tstm\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"percent\",\"values\":[0]},{\"name\":\"tcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[4]},{\"name\":\"lcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[0]},{\"name\":\"mcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[0]},{\"name\":\"hcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[4]},{\"name\":\"gust\",\"levelType\":\"hl\",\"level\":10,\"unit\":\"m/s\",\"values\":[4.4]},{\"name\":\"pmin\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.0]},{\"name\":\"pmax\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.0]},{\"name\":\"spp\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"percent\",\"values\":[-9]},{\"name\":\"pcat\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"category\",\"values\":[0]},{\"name\":\"pmean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.0]},{\"name\":\"pmedian\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.0]},{\"name\":\"Wsymb\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"category\",\"values\":[2]}]},{\"validTime\":\"2017-01-03T13:00:00Z\",\"parameters\":[{\"name\":\"msl\",\"levelType\":\"hmsl\",\"level\":0,\"unit\":\"hPa\",\"values\":[997]},{\"name\":\"t\",\"levelType\":\"hl\",\"level\":2,\"unit\":\"Cel\",\"values\":[-3.4]},{\"name\":\"vis\",\"levelType\":\"hl\",\"level\":2,\"unit\":\"km\",\"values\":[31.0]},{\"name\":\"wd\",\"levelType\":\"hl\",\"level\":10,\"unit\":\"degree\",\"values\":[167]},{\"name\":\"ws\",\"levelType\":\"hl\",\"level\":10,\"unit\":\"m/s\",\"values\":[1.9]},{\"name\":\"r\",\"levelType\":\"hl\",\"level\":2,\"unit\":\"percent\",\"values\":[78]},{\"name\":\"tstm\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"percent\",\"values\":[0]},{\"name\":\"tcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[7]},{\"name\":\"lcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[0]},{\"name\":\"mcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[2]},{\"name\":\"hcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[6]},{\"name\":\"gust\",\"levelType\":\"hl\",\"level\":10,\"unit\":\"m/s\",\"values\":[4.1]},{\"name\":\"pmin\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.0]},{\"name\":\"pmax\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.0]},{\"name\":\"spp\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"percent\",\"values\":[-9]},{\"name\":\"pcat\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"category\",\"values\":[0]},{\"name\":\"pmean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.0]},{\"name\":\"pmedian\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.0]},{\"name\":\"Wsymb\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"category\",\"values\":[3]}]},{\"validTime\":\"2017-01-03T14:00:00Z\",\"parameters\":[{\"name\":\"msl\",\"levelType\":\"hmsl\",\"level\":0,\"unit\":\"hPa\",\"values\":[996]},{\"name\":\"t\",\"levelType\":\"hl\",\"level\":2,\"unit\":\"Cel\",\"values\":[-3.2]},{\"name\":\"vis\",\"levelType\":\"hl\",\"level\":2,\"unit\":\"km\",\"values\":[17.0]},{\"name\":\"wd\",\"levelType\":\"hl\",\"level\":10,\"unit\":\"degree\",\"values\":[157]},{\"name\":\"ws\",\"levelType\":\"hl\",\"level\":10,\"unit\":\"m/s\",\"values\":[2.5]},{\"name\":\"r\",\"levelType\":\"hl\",\"level\":2,\"unit\":\"percent\",\"values\":[83]},{\"name\":\"tstm\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"percent\",\"values\":[0]},{\"name\":\"tcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[8]},{\"name\":\"lcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[1]},{\"name\":\"mcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[5]},{\"name\":\"hcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[7]},{\"name\":\"gust\",\"levelType\":\"hl\",\"level\":10,\"unit\":\"m/s\",\"values\":[5.1]},{\"name\":\"pmin\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.0]},{\"name\":\"pmax\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.0]},{\"name\":\"spp\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"percent\",\"values\":[-9]},{\"name\":\"pcat\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"category\",\"values\":[0]},{\"name\":\"pmean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.0]},{\"name\":\"pmedian\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.0]},{\"name\":\"Wsymb\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"category\",\"values\":[6]}]},{\"validTime\":\"2017-01-03T15:00:00Z\",\"parameters\":[{\"name\":\"msl\",\"levelType\":\"hmsl\",\"level\":0,\"unit\":\"hPa\",\"values\":[993]},{\"name\":\"t\",\"levelType\":\"hl\",\"level\":2,\"unit\":\"Cel\",\"values\":[-2.5]},{\"name\":\"vis\",\"levelType\":\"hl\",\"level\":2,\"unit\":\"km\",\"values\":[11.0]},{\"name\":\"wd\",\"levelType\":\"hl\",\"level\":10,\"unit\":\"degree\",\"values\":[155]},{\"name\":\"ws\",\"levelType\":\"hl\",\"level\":10,\"unit\":\"m/s\",\"values\":[2.9]},{\"name\":\"r\",\"levelType\":\"hl\",\"level\":2,\"unit\":\"percent\",\"values\":[86]},{\"name\":\"tstm\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"percent\",\"values\":[0]},{\"name\":\"tcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[8]},{\"name\":\"lcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[3]},{\"name\":\"mcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[8]},{\"name\":\"hcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[8]},{\"name\":\"gust\",\"levelType\":\"hl\",\"level\":10,\"unit\":\"m/s\",\"values\":[6.1]},{\"name\":\"pmin\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.0]},{\"name\":\"pmax\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.1]},{\"name\":\"spp\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"percent\",\"values\":[100]},{\"name\":\"pcat\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"category\",\"values\":[1]},{\"name\":\"pmean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.0]},{\"name\":\"pmedian\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.0]},{\"name\":\"Wsymb\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"category\",\"values\":[6]}]},{\"validTime\":\"2017-01-03T16:00:00Z\",\"parameters\":[{\"name\":\"msl\",\"levelType\":\"hmsl\",\"level\":0,\"unit\":\"hPa\",\"values\":[991]},{\"name\":\"t\",\"levelType\":\"hl\",\"level\":2,\"unit\":\"Cel\",\"values\":[-1.6]},{\"name\":\"vis\",\"levelType\":\"hl\",\"level\":2,\"unit\":\"km\",\"values\":[10.0]},{\"name\":\"wd\",\"levelType\":\"hl\",\"level\":10,\"unit\":\"degree\",\"values\":[161]},{\"name\":\"ws\",\"levelType\":\"hl\",\"level\":10,\"unit\":\"m/s\",\"values\":[3.1]},{\"name\":\"r\",\"levelType\":\"hl\",\"level\":2,\"unit\":\"percent\",\"values\":[88]},{\"name\":\"tstm\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"percent\",\"values\":[0]},{\"name\":\"tcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[8]},{\"name\":\"lcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[6]},{\"name\":\"mcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[8]},{\"name\":\"hcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[8]},{\"name\":\"gust\",\"levelType\":\"hl\",\"level\":10,\"unit\":\"m/s\",\"values\":[6.5]},{\"name\":\"pmin\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.0]},{\"name\":\"pmax\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.5]},{\"name\":\"spp\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"percent\",\"values\":[100]},{\"name\":\"pcat\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"category\",\"values\":[1]},{\"name\":\"pmean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.2]},{\"name\":\"pmedian\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.0]},{\"name\":\"Wsymb\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"category\",\"values\":[15]}]},{\"validTime\":\"2017-01-03T17:00:00Z\",\"parameters\":[{\"name\":\"msl\",\"levelType\":\"hmsl\",\"level\":0,\"unit\":\"hPa\",\"values\":[989]},{\"name\":\"t\",\"levelType\":\"hl\",\"level\":2,\"unit\":\"Cel\",\"values\":[-1.0]},{\"name\":\"vis\",\"levelType\":\"hl\",\"level\":2,\"unit\":\"km\",\"values\":[12.0]},{\"name\":\"wd\",\"levelType\":\"hl\",\"level\":10,\"unit\":\"degree\",\"values\":[162]},{\"name\":\"ws\",\"levelType\":\"hl\",\"level\":10,\"unit\":\"m/s\",\"values\":[3.4]},{\"name\":\"r\",\"levelType\":\"hl\",\"level\":2,\"unit\":\"percent\",\"values\":[85]},{\"name\":\"tstm\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"percent\",\"values\":[0]},{\"name\":\"tcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[8]},{\"name\":\"lcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[8]},{\"name\":\"mcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[8]},{\"name\":\"hcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[8]},{\"name\":\"gust\",\"levelType\":\"hl\",\"level\":10,\"unit\":\"m/s\",\"values\":[7.3]},{\"name\":\"pmin\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.0]},{\"name\":\"pmax\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[1.4]},{\"name\":\"spp\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"percent\",\"values\":[100]},{\"name\":\"pcat\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"category\",\"values\":[1]},{\"name\":\"pmean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.6]},{\"name\":\"pmedian\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.5]},{\"name\":\"Wsymb\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"category\",\"values\":[15]}]},{\"validTime\":\"2017-01-03T18:00:00Z\",\"parameters\":[{\"name\":\"msl\",\"levelType\":\"hmsl\",\"level\":0,\"unit\":\"hPa\",\"values\":[987]},{\"name\":\"t\",\"levelType\":\"hl\",\"level\":2,\"unit\":\"Cel\",\"values\":[-0.4]},{\"name\":\"vis\",\"levelType\":\"hl\",\"level\":2,\"unit\":\"km\",\"values\":[16.0]},{\"name\":\"wd\",\"levelType\":\"hl\",\"level\":10,\"unit\":\"degree\",\"values\":[155]},{\"name\":\"ws\",\"levelType\":\"hl\",\"level\":10,\"unit\":\"m/s\",\"values\":[3.8]},{\"name\":\"r\",\"levelType\":\"hl\",\"level\":2,\"unit\":\"percent\",\"values\":[78]},{\"name\":\"tstm\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"percent\",\"values\":[0]},{\"name\":\"tcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[8]},{\"name\":\"lcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[8]},{\"name\":\"mcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[8]},{\"name\":\"hcc_mean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"octas\",\"values\":[8]},{\"name\":\"gust\",\"levelType\":\"hl\",\"level\":10,\"unit\":\"m/s\",\"values\":[8.1]},{\"name\":\"pmin\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[0.4]},{\"name\":\"pmax\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[1.6]},{\"name\":\"spp\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"percent\",\"values\":[100]},{\"name\":\"pcat\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"category\",\"values\":[1]},{\"name\":\"pmean\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[1.1]},{\"name\":\"pmedian\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"kg/m2/h\",\"values\":[1.2]},{\"name\":\"Wsymb\",\"levelType\":\"hl\",\"level\":0,\"unit\":\"category\",\"values\":[15]}]}]}";
        getWeather(getJsonObject(textData));
            return textData;

    }*/

    public JSONObject getJsonObject(String dataBuffer) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(dataBuffer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public void getWeather(JSONObject dataBufferObj) {
        JsonParser jsParser = new JsonParser(dataBufferObj);
        Log.d(TAG, "getWeather: " + jsParser.getWeather().toString());
    }

}

