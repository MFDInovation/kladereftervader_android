package com.example.huaxie.kladervader;

import org.json.JSONObject;

/**
 * Created by huaxie on 2017-01-02.
 */

public class Weather {
    private int symbol;
    private double temperature;
    private double rainfall;
    private double windSpeed;

    public Weather(int symbol, double temperature, double rainfall, double windSpeed){
        this.symbol = symbol;
        this.temperature = temperature;
        this.rainfall = rainfall;
        this.windSpeed = windSpeed;
    }

    public void initWeather(JSONObject json, int startTime, int endTime){
        double maxRainfall;
        double minTemperature;
        int worstSymbol;
        double maxWindSpeed;
    }
}

