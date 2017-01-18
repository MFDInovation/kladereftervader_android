package com.example.huaxie.kladervader;

/**
 * Created by huaxie on 2017-01-09.
 */

class Clothing {
    enum TempStatus {
        mycketKallt("kängor, varma strumpor, Underställ, täckbyxor, varm tröja, varm jacka, varma vantar, varm mössa, halsduk"),
        kallt("kängor, Underställ, tröja, varm jacka, vantar, mössa, halsduk"),
        nollgradigt("Kängor, Varm jacka, halsduk, mössa, vantar"),
        nollgradigtRegn ("kängor, Varm jacka, halsduk, mössa, vantar, paraply"),
        kyligt("gympaskor, Jacka"),
        kyligtRegn("stövlar, Regnkläder, tröja"),
        varmt("t-shirt, shorts, skor"),
        varmtRegn("t-shirt, shorts, skor, regnkläder"),
        hett("t-shirt, shorts, sandaler"),
        hettRegn("t-shirt, shorts, sandaler, paraply"),
        errorNetwork("Kunde inte ladda väderdata"),
        errorGPS("Kunde inte hitta din plats");
        public String getName() {
            return name;
        }

        private String name;
        TempStatus(String s) {
            this.name = s;
        }
    }


    int getClosingImage(Weather weather){
        return getClosingImage(getStatus(weather));
    }

    private int getClosingImage(TempStatus status){
        int imageId = 0;
        switch (status){
            case mycketKallt: return R.mipmap.minus20;
            case kallt: return R.mipmap.minus10;
            case nollgradigt: return R.mipmap.plus0;
            case nollgradigtRegn: return R.mipmap.plus0n;
            case kyligt: return R.mipmap.plus10;
            case kyligtRegn: return R.mipmap.plus10n;
            case varmt: return R.mipmap.plus20;
            case varmtRegn: return R.mipmap.plus20n;
            case hett: return R.mipmap.plus25;
            case hettRegn: return R.mipmap.plus25n;
            case errorNetwork: return R.mipmap.internet_error;
            case errorGPS: return R.mipmap.gps_error;
        }
        return imageId;
    }

    static TempStatus getStatus(Weather weather){
       int temp = (int)Math.round(weather.getTemperature());
        if (temp > -100 && temp < -15){
            return TempStatus.mycketKallt;
        }else if (temp >= -15 && temp < -5){
            return TempStatus.kallt;
        }else if (temp >= -5 && temp < 5){
            return (weather.getRainfall() > 0 ? TempStatus.nollgradigtRegn : TempStatus.nollgradigt);
        }else if (temp >= 5 && temp < 15){
            return (weather.getRainfall() > 0 ? TempStatus.kyligtRegn : TempStatus.kyligt);
        }else if (temp >= 15 && temp < 25){
            return (weather.getRainfall() > 0 ? TempStatus.varmtRegn : TempStatus.varmt);
        }else if(temp >= 25 && temp < 100){
            return (weather.getRainfall() > 0 ? TempStatus.hettRegn : TempStatus.hett);
        }
        return TempStatus.nollgradigtRegn;
    }
}
