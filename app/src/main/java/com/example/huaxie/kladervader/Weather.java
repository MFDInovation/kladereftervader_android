package com.example.huaxie.kladervader;


/**
 * Created by huaxie on 2017-01-03.
 */

class Weather {
    int symbol;
    double temperature;
    double rainfall;
    double windSpeed;
    WeatherSymbol.WeatherStatus weatherSymbol;

    Weather(){
        this.symbol = 0;
        this.temperature = -Double.MAX_VALUE;
        this.rainfall = 0;
        this.windSpeed = 0;
        this.weatherSymbol = null;
    }

    Weather(int symbol, double temperature, double rainfall, double windSpeed){
        this.symbol = symbol;
        this.temperature = temperature;
        this.rainfall = rainfall;
        this.windSpeed = windSpeed;
        if(symbol != 0){
            this.weatherSymbol = new WeatherSymbol(symbol).getWeatherStatus(symbol);
        }else {
            weatherSymbol = null;
        }
    }

    Weather(WeatherSymbol.WeatherStatus weatherSymbol, double temperature, double rainfall, double windSpeed){
        this.temperature = temperature;
        this.rainfall = rainfall;
        this.windSpeed = windSpeed;
        this.weatherSymbol = weatherSymbol;
    }

    @Override
    public String toString() {
        return "symbol: "+symbol + "temperature: " + temperature + "rainfall: " + rainfall + "windSpeed :" +windSpeed + "weathersymbol" + getWeatherStatus();
    }


    double getRainfall() {
        return rainfall;
    }

    double getTemperature() {
        return temperature;
    }

    WeatherSymbol.WeatherStatus getWeatherStatus(){
        return weatherSymbol;
    }
}
