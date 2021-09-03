package com.example.test.deviceInfo;


import com.google.gson.annotations.SerializedName;

public class DeviceInfo {
    @SerializedName("device_id")
    public String deviceId;

    @SerializedName("shumei_id")
    public String shumeiId;

    @SerializedName("oaid")
    public String oaid;

    @SerializedName("tdid")
    public String tdid;

    @SerializedName("advertisingId")
    public String advertisingId;

    @SerializedName("android_id")
    public String androidId;

    @SerializedName("mac_address")
    public String macAddress;

    @SerializedName("imei")
    public String imei;

    @SerializedName("imsi")
    public String imsi;

    @SerializedName("serial_number")
    public String serialNumber;


    @SerializedName("secondFeature")
    public SecondFeature secondFeature = new SecondFeature();

    public static class SecondFeature{

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

        public SecondFeature() {
        }

        @Override
        public String toString() {
            return "SecondFeature{" +
                    "board='" + board + '\'' +
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
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "deviceId='" + deviceId + '\'' +
                ", shumeiId='" + shumeiId + '\'' +
                ", oaid='" + oaid + '\'' +
                ", tdid='" + tdid + '\'' +
                ", advertisingId='" + advertisingId + '\'' +
                ", androidId='" + androidId + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", imei='" + imei + '\'' +
                ", imsi='" + imsi + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ",\n secondFeature=" + secondFeature +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceInfo that = (DeviceInfo) o;

        if (deviceId != null ? !deviceId.equals(that.deviceId) : that.deviceId != null) return false;
        if (shumeiId != null ? !shumeiId.equals(that.shumeiId) : that.shumeiId != null) return false;
        if (oaid != null ? !oaid.equals(that.oaid) : that.oaid != null) return false;
        if (tdid != null ? !tdid.equals(that.tdid) : that.tdid != null) return false;
        if (advertisingId != null ? !advertisingId.equals(that.advertisingId) : that.advertisingId != null)
            return false;
        if (androidId != null ? !androidId.equals(that.androidId) : that.androidId != null) return false;
        if (macAddress != null ? !macAddress.equals(that.macAddress) : that.macAddress != null) return false;
        if (imei != null ? !imei.equals(that.imei) : that.imei != null) return false;
        if (imsi != null ? !imsi.equals(that.imsi) : that.imsi != null) return false;
        return serialNumber != null ? serialNumber.equals(that.serialNumber) : that.serialNumber == null;
    }

    @Override
    public int hashCode() {
        int result = deviceId != null ? deviceId.hashCode() : 0;
        result = 31 * result + (shumeiId != null ? shumeiId.hashCode() : 0);
        result = 31 * result + (oaid != null ? oaid.hashCode() : 0);
        result = 31 * result + (tdid != null ? tdid.hashCode() : 0);
        result = 31 * result + (advertisingId != null ? advertisingId.hashCode() : 0);
        result = 31 * result + (androidId != null ? androidId.hashCode() : 0);
        result = 31 * result + (macAddress != null ? macAddress.hashCode() : 0);
        result = 31 * result + (imei != null ? imei.hashCode() : 0);
        result = 31 * result + (imsi != null ? imsi.hashCode() : 0);
        result = 31 * result + (serialNumber != null ? serialNumber.hashCode() : 0);
        return result;
    }
}
