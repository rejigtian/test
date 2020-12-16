package com.example.test.deviceInfo;


import com.google.gson.annotations.SerializedName;

public class DeviceInfo {
    @SerializedName("device_id")
    public String deviceId;

    @SerializedName("mac_address")
    public String macAddress;

    @SerializedName("android_id")
    public String androidId;

    @SerializedName("imei")
    public String imei;

    @SerializedName("board")
    public String board;

    @SerializedName("bootloader")
    public String bootloader;

    @SerializedName("brand")
    public String brand;

    @SerializedName("cpu_abi")
    public String cpuAbi;

    @SerializedName("device")
    public String device;

    @SerializedName("device_version")
    public String deviceVersion;

    @SerializedName("host")
    public String host;

    @SerializedName("model")
    public String model;

    @SerializedName("manufacturer")
    public String manufacturer;

    @SerializedName("product")
    public String product;

    @SerializedName("time")
    public String time;

    @SerializedName("hardware")
    public String hardware;

    @SerializedName("display")
    public String display;

    @SerializedName("latitude")
    public String latitude;

    @SerializedName("longitude")
    public String longitude;

    @SerializedName("version_release")
    public String versionRelease;

    @SerializedName("version_incremental")
    public String versionIncremental;

    @SerializedName("version_sdk_int")
    public String versionSdkInt;

    @SerializedName("shumei_id")
    public String shumeiId;



    @Override
    public String toString() {
        return "Deviceinfo{" +
                "macAddress='" + macAddress + '\'' +
                ", androidId='" + androidId + '\'' +
                ", imei='" + imei + '\'' +
                ", board='" + board + '\'' +
                ", bootloader='" + bootloader + '\'' +
                ", brand='" + brand + '\'' +
                ", cpuAbi='" + cpuAbi + '\'' +
                ", device='" + device + '\'' +
                ", deviceVersion='" + deviceVersion + '\'' +
                ", host='" + host + '\'' +
                ", model='" + model + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", product='" + product + '\'' +
                ", time='" + time + '\'' +
                ", hardware='" + hardware + '\'' +
                ", display='" + display + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", versionRelease='" + versionRelease + '\'' +
                ", versionIncremental='" + versionIncremental + '\'' +
                ", versionSdkInt='" + versionSdkInt + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceInfo that = (DeviceInfo) o;

        if (macAddress != null ? !macAddress.equals(that.macAddress) : that.macAddress != null) return false;
        if (androidId != null ? !androidId.equals(that.androidId) : that.androidId != null) return false;
        if (imei != null ? !imei.equals(that.imei) : that.imei != null) return false;
        if (board != null ? !board.equals(that.board) : that.board != null) return false;
        if (bootloader != null ? !bootloader.equals(that.bootloader) : that.bootloader != null) return false;
        if (brand != null ? !brand.equals(that.brand) : that.brand != null) return false;
        if (cpuAbi != null ? !cpuAbi.equals(that.cpuAbi) : that.cpuAbi != null) return false;
        if (device != null ? !device.equals(that.device) : that.device != null) return false;
        if (deviceVersion != null ? !deviceVersion.equals(that.deviceVersion) : that.deviceVersion != null)
            return false;
        if (host != null ? !host.equals(that.host) : that.host != null) return false;
        if (model != null ? !model.equals(that.model) : that.model != null) return false;
        if (manufacturer != null ? !manufacturer.equals(that.manufacturer) : that.manufacturer != null) return false;
        if (product != null ? !product.equals(that.product) : that.product != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        if (hardware != null ? !hardware.equals(that.hardware) : that.hardware != null) return false;
        if (display != null ? !display.equals(that.display) : that.display != null) return false;
        if (latitude != null ? !latitude.equals(that.latitude) : that.latitude != null) return false;
        if (longitude != null ? !longitude.equals(that.longitude) : that.longitude != null) return false;
        if (versionRelease != null ? !versionRelease.equals(that.versionRelease) : that.versionRelease != null)
            return false;
        if (versionIncremental != null ? !versionIncremental.equals(that.versionIncremental) : that.versionIncremental != null)
            return false;
        if (versionSdkInt != null ? !versionSdkInt.equals(that.versionSdkInt) : that.versionSdkInt != null)
            return false;
        return shumeiId != null ? shumeiId.equals(that.shumeiId) : that.shumeiId == null;
    }

    @Override
    public int hashCode() {
        int result = macAddress != null ? macAddress.hashCode() : 0;
        result = 31 * result + (androidId != null ? androidId.hashCode() : 0);
        result = 31 * result + (imei != null ? imei.hashCode() : 0);
        result = 31 * result + (board != null ? board.hashCode() : 0);
        result = 31 * result + (bootloader != null ? bootloader.hashCode() : 0);
        result = 31 * result + (brand != null ? brand.hashCode() : 0);
        result = 31 * result + (cpuAbi != null ? cpuAbi.hashCode() : 0);
        result = 31 * result + (device != null ? device.hashCode() : 0);
        result = 31 * result + (deviceVersion != null ? deviceVersion.hashCode() : 0);
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + (model != null ? model.hashCode() : 0);
        result = 31 * result + (manufacturer != null ? manufacturer.hashCode() : 0);
        result = 31 * result + (product != null ? product.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (hardware != null ? hardware.hashCode() : 0);
        result = 31 * result + (display != null ? display.hashCode() : 0);
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        result = 31 * result + (versionRelease != null ? versionRelease.hashCode() : 0);
        result = 31 * result + (versionIncremental != null ? versionIncremental.hashCode() : 0);
        result = 31 * result + (versionSdkInt != null ? versionSdkInt.hashCode() : 0);
        result = 31 * result + (shumeiId != null ? shumeiId.hashCode() : 0);
        return result;
    }
}
