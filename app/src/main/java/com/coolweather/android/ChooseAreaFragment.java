package com.coolweather.android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.LogUtil;
import com.coolweather.android.util.MyApplication;
import com.coolweather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 遍历省市县数据的碎片类
 * Created by lenovo-CY on 2018/10/25.
 */

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;

    private ProgressDialog progressDialog;//带进度条的对话框

    //碎片布局中的控件
    private TextView titleText;
    private Button backButton;
    private ListView listView;

    //关联ListView的适配器
    private ArrayAdapter<String> adapter;

    //总数据列表？
    private List<String> dataList=new ArrayList<>();

    /**
     * 从数据库查询后返回的省列表
     */
    private List<Province> provinceList;

    /**
     * 市列表
     */
    private List<City> cityList;

    /**
     * 县/区列表
     */
    private List<County> countyList;

    /**
     * 选中的省份
     */
    private Province selectedProvince;

    /**
     * 选中的城市
     */
    private City selectedCity;

    /**
     * 当前选中的级别
     */
    private int currentLevel;

    /**
     * 该方法为碎片创建视图（加载布局）时调用
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //加载一个布局文件,并返回布局的View（视图）对象
        View view=inflater.inflate(R.layout.choose_area,container,false);
        //利用这个View(视图)去获取布局里面的控件
        titleText=(TextView)view.findViewById(R.id.title_text);
        backButton=(Button)view.findViewById(R.id.back_button);
        listView=(ListView)view.findViewById(R.id.list_view);
        //实例化适配器
        adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,
                dataList);
        //将适配器和listView关联起来
        listView.setAdapter(adapter);

        return view;
    }

    /**
     * 该方法为 确保与碎片相关联的活动一定已经创建完毕的时候调用
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //设置listView的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(currentLevel==LEVEL_PROVINCE){
                    //如果选中的是省级
                    //获取当前点击位置的省对象
                    selectedProvince=provinceList.get(position);
                    //然后进入对应的市级
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    //如果选中的是市级
                    //获取当前点击位置的市级对象
                    selectedCity=cityList.get(position);
                    //然后进入对应的县级
                    queryCounties();

                }
            }
        });
        //设置返回按钮的点击事件
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLevel==LEVEL_COUNTY){
                    //如果在县/区级页面点击返回
                    queryCities();

                }else if(currentLevel==LEVEL_CITY){
                    //如果在市级页面点击返回
                    queryProvinces();

                }
            }
        });

        queryProvinces(); //加载省级数据
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryProvinces(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);//设置返回按钮隐藏
        //从数据库中查询数据
        provinceList= DataSupport.findAll(Province.class);
        if(provinceList.size()>0){
            //如果本地数据库里面有数据
            dataList.clear();
            for(Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            //获取出来的数据都临时存放集合中后，通知列表控件刷新
            adapter.notifyDataSetChanged();
            //设置当前选定的项目为第一个
            listView.setSelection(0);
            currentLevel=LEVEL_PROVINCE;
        }else{
            //如果本地数据库没有数据，则访问服务器查询
            String address="http://guolin.tech/api/china";
            //调用该方法获取服务器上的数据
            queryFromServer(address,"province");
        }

    }

    /**
     * 查询选中省内的所有市，优先从数据库查询，如果没有再从服务器获取
     */
    private void queryCities(){
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        //查询数据库中所有的市
        cityList=DataSupport.where("provinceid=?",String.valueOf(selectedProvince.
                getId())).find(City.class);
        if(cityList.size()>0){
            //表示数据库有数据
            dataList.clear();//清空之前集合中的数据
            for(City city:cityList){
                dataList.add(city.getCityName());
            }
            //获取到所有数据并存放到集合中后，通知listView刷新
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else{
            //表示数据库没有数据，需要从服务器上去查
            int provinceCode=selectedProvince.getProvinceCode();
            String adress="http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(adress,"city");
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，没有在到服务器中查询
     */
    private void queryCounties(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList=DataSupport.where("cityid= ?",String.valueOf(selectedCity.
                getId())).find(County.class);
        if(countyList.size()>0){
            //表示数据库有数据
            dataList.clear();
            for(County county:countyList){
                dataList.add(county.getCountyName());
            }
            //获取到所有数据并存储到集合后，通知列表控件刷新
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;

        }else{
            //表示数据库中没有数据，去服务器查询
            int provinceCode=selectedProvince.getProvinceCode();
            int cityCode=selectedCity.getCityCode();
            String adress="http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(adress,"county");

        }
    }





    /**
     * 根据传入的地址和类型从服务器上查询省市县数据
     * @param adress 网络地址
     * @param type  省或市或县
     */
    private void  queryFromServer(String adress,final String type){
        showProgressDialog();//显示一个进度条对话框
        //访问服务器获取数据
        HttpUtil.sendOkHttpRequest(adress, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获取服务器返回的数据
                String responesText=response.body().string();
                boolean result=false;
                if("province".equals(type)){
                    //获取省级数据
                    result=Utility.handleProvinceResponse(responesText);
                }else if("city".equals(type)){
                    LogUtil.d("获取市级数据：",responesText);
                    //获取市级数据
                    result=Utility.handleCityResponse(responesText,
                            selectedProvince.getId());

                }else if("county".equals(type)){
                    //获取县级数据
                    result=Utility.handleCountyResponse(responesText,
                            selectedCity.getId());
                }

                if(result){
                    //说明解析成功，获取到数据并存入数据库了.开始更新UI
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();//关闭进度条对话框
                            if("province".equals(type)){
                                queryProvinces();

                            }else if("city".equals(type)){
                                queryCities();

                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });

                }

            }
            @Override
            public void onFailure(Call call, IOException e) {
                //当获取数据出现异常后，会调用方法
                //我们在该方法中执行一条弹出加载失败的通知

                //依然要通过方法回到UI线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();//关闭进度条对话框
                        Toast.makeText(getContext(), "加载失败,请检查网络是否开启。",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }
    /**
     * 显示对话框
     */
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭对话框
     */
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}
