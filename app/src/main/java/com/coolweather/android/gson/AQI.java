package com.coolweather.android.gson;

/**
 * Created by lenovo-CY on 2018/10/26.
 */

public class AQI {
    public AQICity city;

    public class AQICity {

        public String aqi;
        public String pm25;
    }
}
