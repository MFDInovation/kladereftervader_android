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
    public WeatherSymbol weatherSymbol;



    public Weather(){ new Weather(0,0,0,0);};

//    public Weather(String test){
//        new Weather(testSymbolvalue,testTemp,testrain,testwind);
//    }

    public Weather(int symbol, double temperature, double rainfall, double windSpeed){
        this.symbol = symbol;
        this.temperature = temperature;
        this.rainfall = rainfall;
        this.windSpeed = windSpeed;
        if(symbol != 0){
            this.weatherSymbol = new WeatherSymbol(symbol);
        }else {
            weatherSymbol = null;
        }
    }

    @Override
    public String toString() {
        return "symbol: "+symbol + "temperature: " + temperature + "rainfall: " + rainfall + "windSpeed :" +windSpeed + "weathersymbol" + getWeatherStatus();
    }

    public int getSymbol() {
        return symbol;
    }

    public double getRainfall() {
        return rainfall;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public WeatherSymbol getWeatherSymbol() {return weatherSymbol;}

    public WeatherSymbol.WeatherStatus getWeatherStatus(){
        return weatherSymbol.getWeatherStatus(symbol);
    }
}
