package com.example.lyb.wsandorid.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.example.lyb.wsandorid.R;

import java.util.Date;

/**
 * Created by lyb on 16-11-18.
 */

public class MapFragment extends Fragment implements LocationSource, AMapLocationListener {
    private static MapFragment fragment = null;
    private MapView mapView;
    private AMap aMap;
    private View mapLayout;
    private OnLocationChangedListener mListener;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();

    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;

    public static Fragment newInstance() {
        if (fragment == null) {
            synchronized (MapFragment.class) {
                if (fragment == null) {
                    fragment = new MapFragment();
                }
            }
        }
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mapLayout == null) {
            mapLayout = inflater.inflate(R.layout.tab_map, null);
            mapView = (MapView) mapLayout.findViewById(R.id.amap);
            mapView.onCreate(savedInstanceState);
            if (aMap == null) {
                aMap = mapView.getMap();

                //设置显示定位按钮 并且可以点击
                UiSettings settings = aMap.getUiSettings();
                //设置定位监听
                aMap.setLocationSource(this);
                // 是否显示定位按钮
                settings.setMyLocationButtonEnabled(true);
                // 是否可触发定位并显示定位层
                aMap.setMyLocationEnabled(true);

                MyLocationStyle myLocationStyle = new MyLocationStyle();
                myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.location));

                aMap.setMyLocationStyle(myLocationStyle);
                initLocation();// 初始化定位

            }
        } else {
            if (mapLayout.getParent() != null) {
                ((ViewGroup) mapLayout.getParent()).removeView(mapLayout);
            }
        }
        return mapLayout;
    }

    //初始化定位
    private void initLocation() {

        //初始化定位
        locationClient = new AMapLocationClient(this.getActivity().getApplicationContext());
        //设置定位回调监听
        locationClient.setLocationListener(this);
        //初始化定位参数
        locationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        locationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        locationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        locationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        locationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        locationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        locationClient.setLocationOption(locationOption);
        //启动定位
        startLocation();
    }


    //设置定位默认参数
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        //设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setGpsFirst(false);
        //可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setHttpTimeOut(30000);
        //可选，设置定位间隔。默认为2秒
        mOption.setInterval(2000);
        //可选，设置是否返回逆地理地址信息。默认是true
        mOption.setNeedAddress(true);
        //可选，设置是否单次定位。默认是false
        mOption.setOnceLocation(false);
        //可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        mOption.setOnceLocationLatest(false);
        //可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);
        //可选，设置是否使用传感器。默认是false
        mOption.setSensorEnable(false);

        //设置显示定位按钮 并且可以点击
        UiSettings settings = aMap.getUiSettings();
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.map_markers_single));
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        // 将自定义的 myLocationStyle 对象添加到地图上
        aMap.setMyLocationStyle(myLocationStyle);
        return mOption;
    }


    //定位回调函数
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {

        if (amapLocation != null) {

            if (amapLocation.getErrorCode() == 0) {
                //Toast.makeText(this.getContext(),"Success",Toast.LENGTH_SHORT).show();
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //Toast.makeText(this.getContext(),"dddddd",Toast.LENGTH_SHORT).show();
                Date date = new Date(amapLocation.getTime());
                //df.format(date);//定位时间
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getCity();//城市信息
                amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码

                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    //设置缩放级别
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(7));
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    mListener.onLocationChanged(amapLocation);
                    //添加图钉
                    //aMap.addMarker(getMarkerOptions(amapLocation));
                    //获取定位信息
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() + "" + amapLocation.getProvince() + "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
                    Toast.makeText(this.getActivity().getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();
                    isFirstLoc = false;
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());

                    Toast.makeText(this.getActivity().getApplicationContext(), "定位失败", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    private void startLocation(){
        // 启动定位
        locationClient.startLocation();
    }
    //激活定位
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;

    }

    //停止定位
    @Override
    public void deactivate() {
        mListener = null;
    }


    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}


    /*
    //自定义一个图钉，并且设置图标，当我们点击图钉时，显示设置的信息
    private MarkerOptions getMarkerOptions(AMapLocation amapLocation) {
        //设置图钉选项
        MarkerOptions options = new MarkerOptions();
        //图标
        options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.location));
        //位置
        options.position(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()));
        StringBuffer buffer = new StringBuffer();
        buffer.append(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() +  "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
        //标题
        options.title(buffer.toString());
        //子标题
        options.snippet("这里好火");
        //设置多少帧刷新一次图片资源
        options.period(60);

        return options;

    }
    */


/*
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null &amp;amp;&amp;amp; aMapLocation != null) {
            if (aMapLocation.getAMapException().getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点

                //获取位置信息
                geoLat = aMapLocation.getLatitude();
                geoLng = aMapLocation.getLongitude();
                aMap.moveCamera(CameraUpdateFactory.zoomTo(14));

            }

        }
    }
*/

/*
    //激活定位
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(getActivity());
            //此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            //注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
            //在定位结束后，在合适的生命周期调用destroy()方法
            //其中如果间隔时间为-1，则定位只定一次
            mAMapLocationManager.requestLocationData(LocationProviderProxy.AMapNetwork, 60 * 1000, 10, this);
        }
    }

    //停止定位
    @Override
    public void deactivate() {
        mListener = null;
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destroy();
        }
        mAMapLocationManager = null;
    }
    */