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
 * Parses the response from SMHIs API
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

    Weather getWeather() throws JSONException, ParseException {
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
        timeSeries = originalObj.getJSONArray("timeSeries");
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",Locale.US);
        for(int i = 0; i< timeSeries.length(); i++){
        JSONObject elementInTimeSeries = timeSeries.getJSONObject(i);
            Date compareDate= dateformat.parse(elementInTimeSeries.getString("validTime"));
            if(compareDate.after(startDate) && compareDate.before(endDate)){
                JSONArray parameter = elementInTimeSeries.getJSONArray("parameters");
                for(int j = 0; j< parameter.length(); j++){
                    JSONObject elementInParameter = parameter.getJSONObject(j);
                    String name = elementInParameter.getString("name");
                    switch (name) {
                        case "t":
                            temperature = elementInParameter.getJSONArray("values").getDouble(0);
                            minTemperature = Math.min(minTemperature, temperature);
                            break;
                        case "pmax":
                            rainfall = elementInParameter.getJSONArray("values").getDouble(0);
                            maxRainfall = Math.max(maxRainfall, rainfall);
                            break;
                        case "Wsymb":
                            symbol = elementInParameter.getJSONArray("values").getInt(0);
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
                            maxWindSpeed = Math.max(maxWindSpeed, windSpeed);
                            break;
                        default:
                            break;
                    }
                }
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
//        Log.d(TAG, "getValidDateTime: starttime" + startDate + "endDate " + endDate);
    }

}
