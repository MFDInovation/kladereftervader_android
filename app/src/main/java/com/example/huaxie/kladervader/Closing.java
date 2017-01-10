package com.example.huaxie.kladervader;

/**
 * Created by huaxie on 2017-01-09.
 */

public class Closing {
    private int imageId;
    public enum TempStatus {
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
        private TempStatus(String s) {
            this.name = s;
        }
    }




    public int getClosingImage(TempStatus status){
        imageId = 0;
        switch (status){
            case mycketKallt: return R.mipmap.minus20;
            case kallt: return R.mipmap.minusten;
            case nollgradigt: return R.mipmap.zero;
            case nollgradigtRegn: return R.mipmap.zerorain;
            case kyligt: return R.mipmap.ten;
            case kyligtRegn: return R.mipmap.tenrain;
            case varmt: return R.mipmap.minus20;
            /*case .varmtRegn: return #imageLiteral(resourceName: "plus20N")
            case .hett: return #imageLiteral(resourceName: "plus25")
            case .hettRegn: return #imageLiteral(resourceName: "plus25N")
            case .errorNetwork: return #imageLiteral(resourceName: "internet_error")
            case .errorGPS: return #imageLiteral(resourceName: "gps_error")*/
        }
        return imageId;
    }

    public TempStatus getStatus(Weather weather){
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
