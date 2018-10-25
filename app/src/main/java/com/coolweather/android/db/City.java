package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * 用于创建数据表，用于存放<市>的具体信息。
 * Created by lenovo-CY on 2018/10/25.
 */

public class City extends DataSupport{
    private int id;                //自增字段
    private String cityName;        //市的名称
    private int cityCode;           //市的代号

    private int provinceId;         //市所属省的id值

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
