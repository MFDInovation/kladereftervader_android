package com.example.huaxie.kladervader;

import android.util.Log;

import java.util.Calendar;

/**
 * Created by huaxie on 2017-01-09.
 */

public class WeatherImage {

    private static final String TAG = "WeatherImage";

    public static final String spring = "spring";
    public static final String summer = "summer";
    public static final String autumn = "autumn";
    public static final String winter = "winter";

    int springDays = 76;
    int summerDays = 131;
    int autumnDays = 273;
    int winterDays = 341;

    public String currentWeather;

    public String getCurrentSeason(){
        currentWeather = summer;
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        if(day > winterDays || day <= springDays) {
            currentWeather = winter;
        }else if (day > autumnDays && day <= winterDays) {
            currentWeather = autumn;
        }else if (day > summerDays && day <= autumnDays) {
            currentWeather = summer;
        }else if (day > springDays && day <= summerDays){
            currentWeather = spring;
        }
        Log.d(TAG, "getCurrentWeather: " + currentWeather);
        return currentWeather;
    }

    public static int getWeatherSymbolImage(WeatherSymbol.WeatherStatus status, String season) {
        if (season.equals("spring")) {
            switch (status) {
                case ClearSky:
                    return R.mipmap.var_klart;
                case NearlyclearSky:
                    return R.mipmap.var_mest_klart;
                case Variablecloudiness:
                    return R.mipmap.var_vaxlandemolnighet;
                case Halfclearsky:
                    return R.mipmap.var_vaxlandemolnighet;
                case Cloudysky:
                    return R.mipmap.var_molnigt;
                case Overcast:
                    return R.mipmap.var_regn_mulet_snoblandat_aska;
                case Fog:
                    return R.mipmap.var_dimma;
                case Rainshowers:
                    return R.mipmap.var_regnskur_byarsnoblandat_askskurar;
                case Thunderstorm:
                    return R.mipmap.var_regnskur_byarsnoblandat_askskurar;
                case Lightsleet:
                    return R.mipmap.var_regn_mulet_snoblandat_aska;
                case Snowshowers:
                    return R.mipmap.var_regnskur_byarsnoblandat_askskurar;
                case Rain:
                    return R.mipmap.var_regn_mulet_snoblandat_aska;
                case Thunder:
                    return R.mipmap.var_regn_mulet_snoblandat_aska;
                case Sleet:
                    return R.mipmap.var_regn_mulet_snoblandat_aska;
                case Snowfall:
                    return R.mipmap.var_regn_mulet_snoblandat_aska;
                default:
                    return 0;
            }
        } else if (season.equals("summer")) {
            switch (status) {
                case ClearSky:
                    return R.mipmap.sommar_klart;
                case NearlyclearSky:
                    return R.mipmap.sommar_mest_klart;
                case Variablecloudiness:
                    return R.mipmap.sommar_vaxlandemolnighet;
                case Halfclearsky:
                    return R.mipmap.sommar_vaxlandemolnighet;
                case Cloudysky:
                    return R.mipmap.sommar_molnigt;
                case Overcast:
                    return R.mipmap.sommar_regn_mulet_aska;
                case Fog:
                    return R.mipmap.sommar_dimma;
                case Rainshowers:
                    return R.mipmap.sommar_regnskur_askskurar;
                case Thunderstorm:
                    return R.mipmap.sommar_regnskur_askskurar;
                case Lightsleet:
                    return R.mipmap.sommar_regnskur_askskurar;
                case Snowshowers:
                    return R.mipmap.sommar_regnskur_askskurar;
                case Rain:
                    return R.mipmap.sommar_regn_mulet_aska;
                case Thunder:
                    return R.mipmap.sommar_regn_mulet_aska;
                case Sleet:
                    return R.mipmap.sommar_regn_mulet_aska;
                case Snowfall:
                    return R.mipmap.sommar_regn_mulet_aska;
                default:
                    return 0;
            }
        }else if (season.equals("autumn")){
            switch (status) {
                case ClearSky:
                    return R.mipmap.host_klart;
                case NearlyclearSky:
                    return R.mipmap.host_mest_klart;
                case Variablecloudiness:
                    return R.mipmap.host_vaxlandemolnighet;
                case Halfclearsky:
                    return R.mipmap.host_vaxlandemolnighet;
                case Cloudysky:
                    return R.mipmap.host_molnigt;
                case Overcast:
                    return R.mipmap.host_regn__snoblandat_mulet_aska;
                case Fog:
                    return R.mipmap.host_dimma;
                case Rainshowers:
                    return R.mipmap.host_regnskur_snobyar_askskurar;
                case Thunderstorm:
                    return R.mipmap.host_regnskur_snobyar_askskurar;
                case Lightsleet:
                    return R.mipmap.host_regn__snoblandat_mulet_aska;
                case Snowshowers:
                    return R.mipmap.host_regnskur_snobyar_askskurar;
                case Rain:
                    return R.mipmap.host_regn__snoblandat_mulet_aska;
                case Thunder:
                    return R.mipmap.host_regn__snoblandat_mulet_aska;
                case Sleet:
                    return R.mipmap.host_regn__snoblandat_mulet_aska;
                case Snowfall:
                    return R.mipmap.host_regn__snoblandat_mulet_aska;
                default:
                    return 0;
            }
        }else if(season.equals("winter")) {
            switch (status) {
                case ClearSky:
                    return R.mipmap.vinter_klart;
                case NearlyclearSky:
                    return R.mipmap.vinter_mest_klart;
                case Variablecloudiness:
                    return R.mipmap.vinter_vaxlandemolnighet;
                case Halfclearsky:
                    return R.mipmap.vinter_vaxlandemolnighet;
                case Cloudysky:
                    return R.mipmap.vinter_molnigt;
                case Overcast:
                    return R.mipmap.vinter_regnskur_byarsnoblandat_askskurar;
                case Fog:
                    return R.mipmap.vinter_dimma;
                case Rainshowers:
                    return R.mipmap.vinter_regnskur_byarsnoblandat_askskurar;
                case Thunderstorm:
                    return R.mipmap.vinter_regnskur_byarsnoblandat_askskurar;
                case Lightsleet:
                    return R.mipmap.vinter_regn_mulet_snoblandat_aska;
                case Snowshowers:
                    return R.mipmap.vinter_regnskur_byarsnoblandat_askskurar;
                case Rain:
                    return R.mipmap.vinter_regn_mulet_snoblandat_aska;
                case Thunder:
                    return R.mipmap.vinter_regn_mulet_snoblandat_aska;
                case Sleet:
                    return R.mipmap.vinter_regn_mulet_snoblandat_aska;
                case Snowfall:
                    return R.mipmap.vinter_snofall;
                default:
                    return 0;
            }
        }
        return 0;
    }
}

