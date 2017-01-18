package com.example.huaxie.kladervader;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by huaxie on 2017-01-03.
 */

class JsonParser {
    private JSONObject originalObj;
    private JSONArray timeSeries = null;
    private Date startDate;
    private Date endDate;

    private final static String TAG = "JsonParser";
    JsonParser(JSONObject originalObj){
        this.originalObj = originalObj;
    }

    Weather getWeather(){
        getValidDateTime();
        double maxRainfall = -Double.MAX_VALUE;
        double minTemperature = Double.MAX_VALUE;
        int worstSymbolPriority = 0;
        int worstSymbol = 0;
        double maxWindSpeed = 0;
        double temperature;
        double rainfall;
        int symbol;
        double windSpeed;
        WeatherSymbol weatherSymbol;
        Weather weather = new Weather();
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
                            switch (name) {
                                case "t":
                                    temperature = elementInParameter.getJSONArray("values").getDouble(0);
                                    minTemperature = Math.min(minTemperature, temperature);
//                                Log.d(TAG, "getWeather: t" + temperature);
//                        Log.d(TAG, "getParameters: "+ elementInParameter.getJSONArray("values").getDouble(0));
                                    break;
                                case "pmax":
                                    rainfall = elementInParameter.getJSONArray("values").getDouble(0);
//                                Log.d(TAG, "getWeather: rainfall" + rainfall);
                                    maxRainfall = Math.max(maxRainfall, rainfall);
                                    break;
                                case "Wsymb":
                                    symbol = elementInParameter.getJSONArray("values").getInt(0);
//                                Log.d(TAG, "getWeather: symbol" + symbol);
                                    weatherSymbol = new WeatherSymbol(symbol);
                                    int currentSymbolPriority = weatherSymbol.getmSymbolPriority();
                                    if (currentSymbolPriority > worstSymbolPriority) {
                                        worstSymbolPriority = currentSymbolPriority;
                                        worstSymbol = symbol;
                                        Log.d(TAG, "getWeather: priority" + weatherSymbol.getmSymbolPriority());
                                    }
                                    break;
                                case "ws":
                                    windSpeed = elementInParameter.getJSONArray("values").getDouble(0);
//                                Log.d(TAG, "getWeather: " + windSpeed);
                                    maxWindSpeed = Math.max(maxWindSpeed, windSpeed);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
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
        weather.symbol = worstSymbol;
        weather.weatherSymbol = new WeatherSymbol(worstSymbol).getWeatherStatus(worstSymbol);
        return weather;
    }

    private void getValidDateTime(){
        TimeZone timeZone = TimeZone.getDefault();
        int offSet = timeZone.getRawOffset();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND,-offSet);
        startDate = calendar.getTime();

        calendar.add(Calendar.HOUR, 8);
        endDate = calendar.getTime();

        Log.d(TAG, "getValidDateTime: starttime" + startDate + "endDate " + endDate);
    }

}
