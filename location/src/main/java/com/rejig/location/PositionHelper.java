package com.rejig.location;

import android.content.Context;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.List;

public class PositionHelper {

    private static PositionHelper instance;
    private PoiSearch poiSearch;
    private Callback callback;

    public static PositionHelper getInstance() {
        if (instance == null){
            instance = new PositionHelper();
        }
        return instance;
    }

    public void initBDSdk(Context context){
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(context);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }

    public void initSearch(){
        poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(listener);
    }

    public void unInitSearch(){
        poiSearch.destroy();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }
    /**
     *  PoiCiySearchOption 设置检索属性
     *  city 检索城市
     *  keyword 检索内容关键字
     *  pageNum 分页页码
     */
    public void searchPosition(String city, String keyword, int pageNum){
        /*
         * pageNum	分页编号，默认返回第0页结果
         * pageCapacity	设置每页容量，默认为10条结果
         * tag	设置检索分类，如“美食”
         * scope	值为1 或 空，返回基本信息
         * 值为2，返回POI详细信息
         * cityLimit	是否限制检索区域为城市内
         * poiFilter	设置检索过滤条件，scope为2时有效
         */
        poiSearch.searchInCity(new PoiCitySearchOption()
                .city(city) //必填
                .keyword(keyword) //必填
                .pageCapacity(20)
                .pageNum(pageNum));
    }

    private final OnGetPoiSearchResultListener listener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            List<HWPosition> poiDetailInfoList = HWPosition.BDPoiListToHWPoiList(poiResult.getAllPoi());
            if (callback != null){
                callback.onSearchResult(poiDetailInfoList);
            }
        }
        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

        }
        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
        //废弃
        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }
    };

    interface Callback{
        void onSearchResult(List<HWPosition> hwPositionList);
    }

}
