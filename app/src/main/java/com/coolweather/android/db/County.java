package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * 创建数据库表的实体类，用于存放<县/区>的具体信息
 * Created by lenovo-CY on 2018/10/25.
 */

public class County extends DataSupport {
    private int id;
    private String countyName;      //县区的名称
    private String weatherId;       //对应天气的ID
    private int cityId;             //所属市id

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
