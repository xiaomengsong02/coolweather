package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

 /**
 *  用于创建数据库表的实体类，用于存放<省>的数据信息。
 * Created by lenovo-CY on 2018/10/25.
 */

public class Province extends DataSupport {
    private int id;                     //自增字段
    private String provinceName;        //省的名称
    private int provinceCode;           //省的代码

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
