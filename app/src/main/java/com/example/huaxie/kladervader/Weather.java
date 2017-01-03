package com.example.huaxie.kladervader;

import org.json.JSONObject;

/**
 * Created by huaxie on 2017-01-03.
 */

public class Weather {
    public int symbol;
    public double temperature;
    public double rainfall;
    public double windSpeed;

    public Weather(){
        Weather(0,0,0,0);
    }

    public void Weather(int symbol, double temperature, double rainfall, double windSpeed){
        this.symbol = symbol;
        this.temperature = temperature;
        this.rainfall = rainfall;
        this.windSpeed = windSpeed;
    }

    @Override
    public String toString() {
        return "symbol: "+symbol + "temperature: " + temperature + "rainfall: " + rainfall + "windSpeed :" +windSpeed;
    }
}
