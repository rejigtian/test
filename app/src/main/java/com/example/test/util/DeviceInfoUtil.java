package com.example.test.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.test.deviceInfo.DeviceInfo;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author rejig
 * date 2020-09-28
 */
@SuppressLint("HardwareIds")
public class DeviceInfoUtil {
    public final static int REQUEAST_PERMISSION = 1;
    public final static String NEEDPERMISSION = "needpermission";
    public final static String DEFAULT_MAC = "02:00:00:00:00:00";
    public final static String HUIWAN_DEVICE = "dX9PAbRsEtKgsYAW";


    //需要获得READ_PHONE_STATE权限，>=6.0，默认返回null
    @SuppressLint("HardwareIds")
    public static String getIMEI(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "unknown";
            }
            if (tm != null) {
                return tm.getDeviceId();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "unknown";
    }

    //需要获得READ_PHONE_STATE权限，>=6.0，默认返回null
    @SuppressLint("HardwareIds")
    public static String getIMSI(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return NEEDPERMISSION;
            }
            if (tm != null) {
                return tm.getSubscriberId();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * IMEI 2号
     *
     * @param context
     * @return
     */
    public static String getIMEI_2(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Class clazz = tm.getClass();
        try {
            Method getImei = clazz.getDeclaredMethod("getImei", int.class);
            return getImei.invoke(tm, 1).toString();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 获得设备的AndroidId
     *
     * @param context 上下文
     * @return 设备的AndroidId
     */
    public static String getAndroidId(Context context) {
        try {
            return Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "unknown";
    }

    /**
     * 获得设备序列号（如：WTK7N16923005607）, 个别设备无法获取
     *
     * @return 设备序列号
     */
    @SuppressLint("HardwareIds")
    private static String getSERIAL() {
        try {
            return Build.SERIAL;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }


    /**
     * 获得设备硬件uuid
     * 使用硬件信息，计算出一个随机数
     * android.os.Build.BOARD：获取设备基板名称
     * android.os.Build.BOOTLOADER:获取设备引导程序版本号
     * android.os.Build.BRAND：获取设备品牌
     * android.os.Build.CPU_ABI：获取设备指令集名称（CPU的类型）
     * android.os.Build.CPU_ABI2：获取第二个指令集名称
     * android.os.Build.DEVICE：获取设备驱动名称
     * android.os.Build.DISPLAY：获取设备显示的版本包（在系统设置中显示为版本号）和ID一样
     * android.os.Build.FINGERPRINT：设备的唯一标识。由设备的多个信息拼接合成。
     * android.os.Build.HARDWARE：设备硬件名称,一般和基板名称一样（BOARD）
     * android.os.Build.HOST：设备主机地址
     * android.os.Build.ID:设备版本号。
     * android.os.Build.MODEL ：获取手机的型号 设备名称。
     * android.os.Build.MANUFACTURER:获取设备制造商
     * android:os.Build.PRODUCT：整个产品的名称
     * android:os.Build.RADIO：无线电固件版本号，通常是不可用的 显示unknown
     * android.os.Build.TAGS：设备标签。如release-keys 或测试的 test-keys
     * android.os.Build.TIME：时间
     * android.os.Build.TYPE:设备版本类型 主要为”user” 或”eng”.
     * android.os.Build.USER:设备用户名 基本上都为android-build
     * @return 设备硬件uuid
     */


    /**
     * Android 7.0之后获取Mac地址
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET"></uses-permission>
     */
    public static String getMacFromHardware() {
        try {
            ArrayList<NetworkInterface> all =
                    Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equals("wlan0"))
                    continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) return "";
                StringBuilder res1 = new StringBuilder();
                for (Byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }
                if (!TextUtils.isEmpty(res1)) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Android 6.0（包括） - Android 7.0（不包括）
     *
     * @return
     */
    private static String getMacAddress() {
        String WifiAddress = "02:00:00:00:00:00";
        try {
            WifiAddress = new BufferedReader(new FileReader(new File("/sys/class/net/wlan0/address"))).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return WifiAddress;
    }

    /**
     * Android 6.0 之前（不包括6.0）获取mac地址
     * 必须的权限 <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
     *
     * @param context * @return
     */
    @SuppressLint("HardwareIds")
    public static String getMacDefault(Context context) {
        String mac = "";
        if (context == null) {
            return mac;
        }
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = null;
        try {
            if (wifi != null) {
                info = wifi.getConnectionInfo();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (info == null) {
            return "";
        }
        mac = info.getMacAddress();
        if (!TextUtils.isEmpty(mac)) {
            mac = mac.toUpperCase(Locale.ENGLISH);
        }
        return mac;
    }

    /**
     * 获取mac地址（适配所有Android版本）
     */
    public static String getMac(Context context) {
        String mac;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mac = getMacDefault(context);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mac = getMacAddress();
        } else {
            mac = getMacFromHardware();
        }
        if (mac == null || mac.length() <= 17 || mac.equals(DEFAULT_MAC)) {
            return "unknown";
        }
        return mac;
    }

    public static String getSerialNumber() {
        String serialNumber;

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            serialNumber = (String) get.invoke(c, "gsm.sn1");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ril.serialnumber");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ro.serialno");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "sys.serialnumber");
            if (serialNumber.equals(""))
                serialNumber = Build.SERIAL;

            // If none of the methods above worked
            if (serialNumber.equals(""))
                serialNumber = "null";
        } catch (Exception e) {
            e.printStackTrace();
            serialNumber = "null";
        }

        return serialNumber;
    }

    public static String longToYearAndDayAndMin(long time) {
        return new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(time);
    }


    @Nullable
    public static Location getLocation(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) return null;
        List<String> providers = locationManager.getProviders(true);
        String locationProvider = null;
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            return null;
        }
        return locationManager.getLastKnownLocation(locationProvider);
    }

    /**
     * @param context context
     * @param level   获取的设备id等级
     * @return 对应等级的设备id map
     */
    public static Map<String, String> getDeviceIdMap(Context context, int level) {
        Map<String, String> map = new HashMap<>();
        if (level <= 1) {
            map.put("mac_address", getMac(context));
            map.put("android_id", getAndroidId(context));
            map.put("imei", getIMEI(context));
        } else if (level <= 2) {
            map.put("board", Build.BOARD);
            map.put("bootloader", Build.BOOTLOADER);
            map.put("brand", Build.BRAND);
            map.put("cpu_abi", Build.CPU_ABI);
            map.put("device", Build.DEVICE);
            map.put("device_version", Build.ID);
            map.put("host", Build.HOST);
            map.put("model", Build.MODEL);
            map.put("manufacturer", Build.MANUFACTURER);
            map.put("product", Build.PRODUCT);
            map.put("time", String.valueOf(Build.TIME));
            map.put("hardware", Build.HARDWARE);
            map.put("display", Build.DISPLAY);
        } else if (level <= 3) {
            map.put("version_release", Build.VERSION.RELEASE);
            map.put("version_incremental", Build.VERSION.INCREMENTAL);
            map.put("version_sdk_int", String.valueOf(Build.VERSION.SDK_INT));
            Location location = getLocation(context);
            if (location != null) {
                map.put("latitude", String.valueOf(location.getLatitude()));
                map.put("longitude", String.valueOf(location.getLongitude()));
            } else {
                map.put("latitude", "unknown");
                map.put("longitude", "unknown");
            }
        }
        return map;
    }

    public static DeviceInfo getDeviceInfo(Context context) {
        DeviceInfo deviceinfo = new DeviceInfo();
        deviceinfo.macAddress = getMac(context);
        deviceinfo.androidId = getAndroidId(context);
        deviceinfo.imei = getIMEI(context);
        deviceinfo.board = Build.BOARD;
        deviceinfo.bootloader = Build.BOOTLOADER;
        deviceinfo.brand = Build.BRAND;
        deviceinfo.cpuAbi = Build.CPU_ABI;
        deviceinfo.device = Build.DEVICE;
        deviceinfo.deviceVersion = Build.ID;
        deviceinfo.host = Build.HOST;
        deviceinfo.model = Build.MODEL;
        deviceinfo.manufacturer = Build.MANUFACTURER;
        deviceinfo.product = Build.PRODUCT;
        deviceinfo.time = String.valueOf(Build.TIME);
        deviceinfo.hardware = Build.HARDWARE;
        deviceinfo.display = Build.DISPLAY;
        deviceinfo.versionRelease = Build.VERSION.RELEASE;
        deviceinfo.versionIncremental = Build.VERSION.INCREMENTAL;
        deviceinfo.versionSdkInt = String.valueOf(Build.VERSION.SDK_INT);
        Location location = getLocation(context);
        if (location != null) {
            deviceinfo.latitude = String.valueOf(location.getLatitude());
            deviceinfo.longitude = String.valueOf(location.getLongitude());
        }
        return deviceinfo;
    }


    public static String getDeviceIdJsonString(DeviceInfo deviceInfo) {
        Gson gson = new Gson();
        return gson.toJson(deviceInfo);
    }
    

}
