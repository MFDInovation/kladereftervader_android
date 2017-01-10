package com.example.huaxie.kladervader;

/**
 * Created by huaxie on 2017-01-09.
 */

public class WeatherSymbol {
    private int mSymbolValue;
    private int mSymbolPriority;

    public WeatherSymbol(int symbolValue){
        this.mSymbolValue = symbolValue;
        this.mSymbolPriority = getPriority(getWeatherStatus(symbolValue));
    }

    public enum WeatherStatus{
            ClearSky("ClearSky"),
            NearlyclearSky("NearlyclearSky"),
            Variablecloudiness("Variablecloudiness"),
            Halfclearsky("Halfclearsky"),
            Cloudysky("Cloudysky"),
            Overcast("Overcast"),
            Fog("Fog"),
            Rainshowers("Rainshowers"),
            Thunderstorm("Thunderstorm"),
            Lightsleet("Lightsleet"),
            Snowshowers("Snowshowers"),
            Rain("Rain"),
            Thunder("Thunder"),
            Sleet("Sleet"),
            Snowfall("Snowfall");

        private String description;
        private WeatherStatus(String s) {
            description = s;
        }
    }



    public WeatherStatus getWeatherStatus(int symbolValue) {
        switch (symbolValue) {
            case 1:
                return WeatherStatus.ClearSky;
            case 2:
                return WeatherStatus.NearlyclearSky;
            case 3:
                return WeatherStatus.Variablecloudiness;
            case 4:
                return WeatherStatus.Halfclearsky;
            case 5:
                return WeatherStatus.Cloudysky;
            case 6:
                return WeatherStatus.Overcast;
            case 7:
                return WeatherStatus.Fog;
            case 8:
                return WeatherStatus.Rainshowers;
            case 9:
                return WeatherStatus.Thunderstorm;
            case 10:
                return WeatherStatus.Lightsleet;
            case 11:
                return WeatherStatus.Snowshowers;
            case 12:
                return WeatherStatus.Rain;
            case 13:
                return WeatherStatus.Thunder;
            case 14:
                return WeatherStatus.Sleet;
            case 15:
                return WeatherStatus.Snowfall;
            default:
                return null;
        }
    }

    private int getPriority(WeatherStatus status){
        mSymbolPriority = 0;
        switch (status){
            case ClearSky:
                mSymbolPriority = 1;
            case NearlyclearSky:
                mSymbolPriority = 2;
            case Variablecloudiness:
                mSymbolPriority = 3;
            case Halfclearsky:
                mSymbolPriority = 4;
            case Cloudysky:
                mSymbolPriority = 5;
            case Overcast:
                mSymbolPriority = 6;
            case Fog:
                mSymbolPriority = 7;
            case Rainshowers:
                mSymbolPriority = 8;
            case Rain:
                mSymbolPriority = 9;
            case Snowshowers:
                mSymbolPriority = 10;
            case Snowfall:
                mSymbolPriority = 11;
            case Thunderstorm:
                mSymbolPriority = 12;
            case Thunder:
                mSymbolPriority = 13;
            case Lightsleet:
                mSymbolPriority = 14;
            case Sleet:
                mSymbolPriority = 15;
        }
        return mSymbolPriority;
    }

    public int getmSymbolPriority() {
        return mSymbolPriority;
    }

    public int getmSymbolValue() {
        return mSymbolValue;
    }
}

