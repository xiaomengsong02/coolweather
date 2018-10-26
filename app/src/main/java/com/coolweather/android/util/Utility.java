package com.coolweather.android.util;

import android.text.TextUtils;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 解析和处理JSON数据的类
 * Created by lenovo-CY on 2018/10/25.
 */

public class Utility {

    /**
     * 解析和处理服务器返回的省级数据(解析的同时在存进数据库)
     * @param response  服务器返回的省级数据
     * @return  解析成功返回true，解析失败返回false
     */
    public static boolean handleProvinceResponse(String response){
        //判断传入的数据是否不为 null或""
        if(!TextUtils.isEmpty(response)){
           try{
               //创建JSONArrry对象并传递要解析的数据
               JSONArray allProvince=new JSONArray(response);
               for (int i = 0; i < allProvince.length(); i++) {
                   //获取一个JSONObject对象
                   JSONObject provinceObject=allProvince.getJSONObject(i);
                   //创建一个Province对象
                   Province province=new Province();
                   province.setProvinceName(provinceObject.getString("name"));
                   province.setProvinceCode(provinceObject.getInt("id"));
                   province.save();//将数据保存到数据库
               }
               return true;
           }catch (JSONException e){
               e.printStackTrace();
           }
        }
        return  false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     * @param response 服务器返回的数据
     * @param provinceId 市级所属省id
     * @return 解析成功返回true，否则false
     */
    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCities=new JSONArray(response);
                for(int i=0;i<allCities.length();i++){
                    //取出的每组数据都是一个对象。
                    JSONObject cityObject=allCities.getJSONObject(i);
                    City city=new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();//保存到数据库

                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return false;
    }

    /**
     * 解析和处理服务器返回的县/区级数据
     * @param response  服务器返回的数据
     * @param cityId 县/区所属市id
     * @return 解析成功返回true.否则false
     */
    public static boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounty=new JSONArray(response);
                for (int i = 0; i < allCounty.length(); i++) {
                    JSONObject countyObject=allCounty.getJSONObject(i);
                    County county=new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();//保存到数据库
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
