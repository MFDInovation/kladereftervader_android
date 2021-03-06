package se.mfd.kladervader;

/**
 * Created by dev on 2017-01-09.
 */

class WeatherSymbol {
    private int mSymbolPriority;

    WeatherSymbol(int symbolValue){
        this.mSymbolPriority = getPriority(getWeatherStatus(symbolValue));
    }

    enum WeatherStatus{
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

        WeatherStatus(String s) {}
    }



    WeatherStatus getWeatherStatus(int symbolValue) {
		        // For now, we return just one single value in the cases where the API has different levels of the same weather type
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
			case 9:
			case 10:
                return WeatherStatus.Rainshowers;
            case 11:
                return WeatherStatus.Thunderstorm;
            case 12:
			case 13:
			case 14:
                return WeatherStatus.Lightsleet;
            case 15:
			case 16:
			case 17:
                return WeatherStatus.Snowshowers;
            case 18:
			case 19:
			case 20:
                return WeatherStatus.Rain;
            case 21:
                return WeatherStatus.Thunder;
            case 22:
			case 23:
			case 24:
                return WeatherStatus.Sleet;
            case 25:
			case 26:
			case 27:
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
                break;
            case NearlyclearSky:
                mSymbolPriority = 2;
                break;
            case Variablecloudiness:
                mSymbolPriority = 3;
                break;
            case Halfclearsky:
                mSymbolPriority = 4;
                break;
            case Cloudysky:
                mSymbolPriority = 5;
                break;
            case Overcast:
                mSymbolPriority = 6;
                break;
            case Fog:
                mSymbolPriority = 7;
                break;
            case Rainshowers:
                mSymbolPriority = 8;
                break;
            case Rain:
                mSymbolPriority = 9;
                break;
            case Snowshowers:
                mSymbolPriority = 10;
                break;
            case Snowfall:
                mSymbolPriority = 11;
                break;
            case Thunderstorm:
                mSymbolPriority = 12;
                break;
            case Thunder:
                mSymbolPriority = 13;
                break;
            case Lightsleet:
                mSymbolPriority = 14;
                break;
            case Sleet:
                mSymbolPriority = 15;
                break;
            default:
                break;
        }
        return mSymbolPriority;
    }

    int getmSymbolPriority() {
        return mSymbolPriority;
    }

}

