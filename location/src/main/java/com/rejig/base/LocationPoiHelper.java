package com.rejig.base;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.ArrayList;
import java.util.List;

/**
 * 定位工具类
 * @author rejig
 * date 2021-10-15
 */
public class LocationPoiHelper {
    private static final String TAG = "LocationHelper";
    private static LocationPoiHelper instance;
    private static final int LOCATE_TIME_GAP = 1000 * 2;//every 2 seconds
    private final LocationClient mLocationClient;
    private HWPosition locationInfo = null;
    private List<HWPosition> nearbyPoi = new ArrayList<>();
    private boolean locateStarted = false;
    private int currentLocateCount = 0;
    private final List<Callback> callbackList = new ArrayList<>();

    public static LocationPoiHelper getInstance(Context context){
        if (instance == null){
            instance = new LocationPoiHelper(context);
        }
        return instance;
    }

    private LocationPoiHelper(Context context) {
        mLocationClient = new LocationClient(context);//声明LocationClient类
        MyLocationListener myListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myListener);    //注册监听函数

        LocationClientOption option = new LocationClientOption();
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        option.setCoorType("bd09ll");
        option.setScanSpan(LOCATE_TIME_GAP);
        option.setIsNeedAddress(true);
        option.setIsNeedLocationPoiList(true);
        mLocationClient.setLocOption(option);
    }

    public void startLocate(boolean forceUpdate, Callback callback) {
        if (locationInfo != null && !forceUpdate){
            callback.onLocationSuc(locationInfo, nearbyPoi);
            return;
        }
        callbackList.add(callback);
        if (locateStarted) {
            return;
        }

        currentLocateCount = 0;
        mLocationClient.start();
        locateStarted = true;
    }

    public void stopLocate() {
        if (!locateStarted) return;
        mLocationClient.stop();
        locateStarted = false;
    }

    /**
     * Baidu location listener
     */
    class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            Log.d(TAG, " onReceiveLocation: " + location);
            if (location != null) {
                if (location.getLocType() == BDLocation.TypeGpsLocation// GPS定位结果
                        || location.getLocType() == BDLocation.TypeNetWorkLocation// 网络定位结果
                        || location.getLocType() == BDLocation.TypeOffLineLocation ) {
                    handlerResult(location);
                } else {
                    handlerError(location);
                }
            }
        }

        private void handlerResult(BDLocation location) {
            Log.i(TAG, " onReceiveLocation handlerResult " + location.getLocType() + " " + location.getLocTypeDescription());
            locationInfo = new HWPosition();
            locationInfo.setLongitude(location.getLongitude());
            locationInfo.setLatitude(location.getLatitude());
            locationInfo.setProvince(location.getProvince());
            locationInfo.setCity(location.getCity());
            locationInfo.setName(location.getCity());
            locationInfo.setAddress(location.getProvince()+location.getCity());
            locationInfo.setTime(System.currentTimeMillis());
            locationInfo.setId(HWPosition.MY_ID);
            stopLocate();
            for (Callback callback : callbackList){
                nearbyPoi = HWPosition.BDPoiListToHWPoiList(locationInfo, location.getPoiList());
                callback.onLocationSuc(locationInfo, nearbyPoi);
            }
            callbackList.clear();
        }

        private void handlerError(BDLocation location) {
            Log.i(TAG, " onReceiveLocation handlerError " + location.getLocType() + " " + location.getLocTypeDescription());
            currentLocateCount++;
            int MAX_LOCATE_NUM = 10;
            if (currentLocateCount >= MAX_LOCATE_NUM) {//没有定位权限
                stopLocate();
                for (Callback callback : callbackList){
                    callback.onLocationFail("定位异常，请检查GPS设置或移动到开阔场地再次尝试~");
                }
                callbackList.clear();
            }
        }
    }

    public interface Callback{
        void onLocationSuc(HWPosition hwPosition, List<HWPosition> NearbyPoiList);
        void onLocationFail(String msg);
    }
}
