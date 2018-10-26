package com.coolweather.android.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 发起网络请求的工具类
 * Created by lenovo-CY on 2018/10/25.
 */

public class HttpUtil {

    /**
     * 使用OKHttp请求网络数据
     * @param address 网络地址
     * @param callback 返回数据的回调接口
     */
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        //创建一个OKHttpClient类
        OkHttpClient client = new OkHttpClient();
        //创建一个Reuest来发起HTTP请求
        Request request=new Request.Builder().url(address).build();
        //将最后的数据回调来该接口中
        client.newCall(request).enqueue(callback);
    }
}
