package com.rejig.base;

import androidx.annotation.NonNull;

import com.baidu.location.Poi;
import com.baidu.mapapi.search.core.PoiInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 会玩内部地理位置类
 *
 * @author rejig
 * date 2021-10-15
 */
public class HWPosition implements Serializable {
    private String id = ""; //id
    private String address; //位置信息
    private String area; //所在的区县
    private String city; //所属城市
    private int distance; //距离中心点的距离
    private String province; //所在省份
    private String name;//名称
    private double latitude; //纬度坐标
    private double longitude; //经度坐标
    private long time; //定位时间

    public static final String MY_ID = "hw";

    public HWPosition() {
    }

    public HWPosition(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    private HWPosition copyPoi() {
        HWPosition hwPosition = new HWPosition();
        hwPosition.address = address;
        hwPosition.area = area;
        hwPosition.city = city;
        hwPosition.distance = distance;
        hwPosition.province = province;
        hwPosition.name = name;
        hwPosition.latitude = latitude;
        hwPosition.longitude = longitude;
        hwPosition.id = id;
        return hwPosition;
    }

    @NonNull
    @Override
    public String toString() {
        return "HWPosition{" +
                "address='" + address + '\'' +
                ", area='" + area + '\'' +
                ", city='" + city + '\'' +
                ", distance=" + distance +
                ", province='" + province + '\'' +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public static List<HWPosition> BDPoiDetailListToHWPoiList(List<PoiInfo> poiDetailInfos) {
        List<HWPosition> positionList = new ArrayList<>();
        if (poiDetailInfos == null) return positionList;
        for (PoiInfo poiDetailInfo : poiDetailInfos) {
            positionList.add(BDPoiDetailToHWPoi(poiDetailInfo));
        }
        return positionList;
    }

    public static HWPosition BDPoiDetailToHWPoi(PoiInfo detailInfo) {
        HWPosition hwPosition = new HWPosition();
        hwPosition.address = detailInfo.getAddress();
        hwPosition.area = detailInfo.getArea();
        hwPosition.city = detailInfo.getCity();
        hwPosition.distance = detailInfo.getDistance();
        hwPosition.province = detailInfo.getProvince();
        hwPosition.name = detailInfo.getName();
        hwPosition.latitude = detailInfo.getLocation().latitude;
        hwPosition.longitude = detailInfo.getLocation().longitude;
        hwPosition.id = detailInfo.getUid();
        return hwPosition;
    }

    public static List<HWPosition> BDPoiListToHWPoiList(HWPosition basePoi, List<Poi> poiDetailInfos) {
        List<HWPosition> positionList = new ArrayList<>();
        if (poiDetailInfos == null) return positionList;
        for (Poi poi : poiDetailInfos) {
            positionList.add(BDPoiToHWPoi(basePoi, poi));
        }
        return positionList;
    }

    public static HWPosition BDPoiToHWPoi(HWPosition basePoi, Poi poi) {
        HWPosition hwPosition = basePoi.copyPoi();
        hwPosition.address = poi.getAddr();
        hwPosition.name = poi.getName();
        hwPosition.id = poi.getId();
        return hwPosition;
    }
}
