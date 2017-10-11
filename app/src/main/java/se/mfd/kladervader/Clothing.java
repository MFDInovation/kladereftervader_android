package se.mfd.kladervader;

/*
This class represents the different clothing that are needed for different weather conditions and
temperatures. The different conditions are represented as a enum.
 */
class Clothing {
    enum TempStatus {
        mycketKallt("-40° till -15° Sol"),
        mycketKalltSno("-40° till -15° Snö"),
        kallt("-15° till -5° Sol"),
        kalltSno("-15° till -5° Snö"),
        nollgradigtMinus("0° till -5° Sol"),
        nollgradigtMinusRegn ("0° till -5° Snö"),
        nollgradigtPlus("0° till 5° Sol"),
        nollgradigtPlusRegn("0° till 5° Snö/Regn"),
        kyligt("5° till 15° Sol"),
        kyligtRegn("5° till 15° Regn"),
        varmt("15° till 19° Sol"),
        varmtRegn("15° till 19° Regn"),
        varmare("19° till 25° Sol"),
        varmareRegn("19° till 25° Regn"),
        hett("25° till 40° Sol"),
        hettRegn("25° till 40° Regn"),
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


  /*  int getClosingImage(Weather weather){
        return getClosingImage(getStatus(weather));
    }

    private int getClosingImage(TempStatus status){
        int imageId = 0;
        switch (status){
            case mycketKallt: return R.mipmap.minus20;
            case kallt: return R.mipmap.minus20;
            case nollgradigtMinus: return R.mipmap.minus10;
            case nollgradigtMinusRegn: return R.mipmap.minus10u;
            case nollgradigtPlus: return R.mipmap.plus0;
            case nollgradigtPlusRegn: return R.mipmap.plus0;
            case kyligt: return R.mipmap.plus10;
            case kyligtRegn: return R.mipmap.plus10n;
            case varmt: return R.mipmap.plus10;
            case varmtRegn: return R.mipmap.plus10n;
            case varmare: return R.mipmap.plus20;
            case varmareRegn: return R.mipmap.plus20n;
            case hett: return R.mipmap.plus25;
            case hettRegn: return R.mipmap.plus25n;
            case errorNetwork: return R.mipmap.internet_error;
            case errorGPS: return R.mipmap.gps_error;
        }
        return imageId;
    } */

    static TempStatus getStatus(Weather weather){
       int temp = (int)Math.round(weather.getTemperature());
        if (temp > -100 && temp < -15){
            return (weather.getRainfall() > 0 ? TempStatus.mycketKalltSno : TempStatus.mycketKallt);
        }else if (temp >= -15 && temp < -5){
            return (weather.getRainfall() > 0 ? TempStatus.kalltSno : TempStatus.kallt);
        }else if (temp >= -5 && temp <= 0){
            return (weather.getRainfall() > 0 ? TempStatus.nollgradigtMinusRegn : TempStatus.nollgradigtMinus);
        }else if (temp <= 5 && temp > 0) {
            return (weather.getRainfall() > 0 ? TempStatus.nollgradigtPlusRegn : TempStatus.nollgradigtPlus);
        }else if (temp >= 5 && temp < 15){
            return (weather.getRainfall() > 0 ? TempStatus.kyligtRegn : TempStatus.kyligt);
        }else if (temp >= 15 && temp <= 19){
            return (weather.getRainfall() > 0 ? TempStatus.varmtRegn : TempStatus.varmt);
        }else if (temp > 19 && temp < 25) {
            return (weather.getRainfall() > 0 ? TempStatus.varmareRegn : TempStatus.varmare);
        }else if(temp >= 25 && temp < 100){
            return (weather.getRainfall() > 0 ? TempStatus.hettRegn : TempStatus.hett);
        }
        return TempStatus.nollgradigtPlusRegn;
    }
}
