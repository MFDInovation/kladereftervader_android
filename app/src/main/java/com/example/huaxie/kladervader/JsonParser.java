package com.example.huaxie.kladervader;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by huaxie on 2017-01-03.
 */

public class JsonParser {
    JSONObject originalObj;
    JSONArray timeSeries = null;
    JSONArray validTimeSeries = null;
    private Date startDate;
    private Date endDate;

    final static String TAG = "JsonParser";
    public JsonParser(JSONObject originalObj){
        this.originalObj = originalObj;
    }

    public Weather getWeather(){
        getValidDateTime();
        double maxRainfall = -Double.MAX_VALUE;
        double minTemperature = Double.MAX_VALUE;
        int worstSymbol = 0;
        double maxWindSpeed = 0;
        double temperature;
        double rainfall= 0;
        int symbol = 0;
        double windSpeed;
        Weather weather = new Weather();
        boolean worstSymbolChange = false;
        try {
            timeSeries = originalObj.getJSONArray("timeSeries");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",Locale.US);
        for(int i = 0; i< timeSeries.length(); i++){
            try {
                JSONObject elementInTimeSeries = timeSeries.getJSONObject(i);
                try {
                    Date compareDate= dateformat.parse(elementInTimeSeries.getString("validTime"));
                    if(compareDate.after(startDate) && compareDate.before(endDate)){
//                        Log.d(TAG, "getWeather: "+ dateformat.format(compareDate));
                        JSONArray parameter = elementInTimeSeries.getJSONArray("parameters");
                        for(int j = 0; j< parameter.length(); j++){
                            JSONObject elementInParameter = parameter.getJSONObject(j);
                            String name = elementInParameter.getString("name");
                            if(name.equals("t")){
                                temperature = elementInParameter.getJSONArray("values").getDouble(0);
                                minTemperature = Math.min(minTemperature,temperature);
                                Log.d(TAG, "getWeather: t" + temperature);
//                        Log.d(TAG, "getParameters: "+ elementInParameter.getJSONArray("values").getDouble(0));
                            }else if(name.equals("pmax")){
                                rainfall = elementInParameter.getJSONArray("values").getDouble(0);
                                Log.d(TAG, "getWeather: rainfall" + rainfall);
                                if(rainfall > maxRainfall)
                                {
                                    worstSymbolChange = true;
                                }else {
                                    worstSymbolChange = false;
                                }
                                maxRainfall = Math.max(maxRainfall,rainfall);
                            }else if(name.equals("Wsymb")){
                                symbol = elementInParameter.getJSONArray("values").getInt(0);
                                Log.d(TAG, "getWeather: symbol" + symbol);
                                if(worstSymbolChange){
                                    worstSymbol = symbol;
                                }
                            }else if(name.equals("ws")){
                                windSpeed = elementInParameter.getJSONArray("values").getDouble(0);
                                Log.d(TAG, "getWeather: " + windSpeed);
                                maxWindSpeed = Math.max(maxWindSpeed, windSpeed);
                            }else {
                            }
                        }
                    }
                    worstSymbolChange = false;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        weather.temperature= minTemperature;
        weather.rainfall = maxRainfall;
        weather.windSpeed = maxWindSpeed;
        weather.symbol = worstSymbol;//not sure whether it is right value
        return weather;
    }

    public void getValidDateTime(){
        startDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.HOUR, 8);
        endDate = calendar.getTime();

//        String endTime = dateformat.format(endDate);
//        Log.d(TAG, "getValidTimeSeries: end time" +endTime );

    }

}
