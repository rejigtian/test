package com.example.test.util;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class OSUtils {

    private static String TAG = OSUtils.class.getSimpleName();

    public enum ROM_TYPE {
        MIUI_ROM,
        FLYME_ROM,
        EMUI_ROM,
        OPPO_ROM,
        OTHER_ROM
    }

    /**
     *
     * MIUI ROM标识
     * "ro.miui.ui.version.code" -> "5"
     * "ro.miui.ui.version.name" -> "V7"
     * "ro.miui.has_handy_mode_sf" -> "1"
     * "ro.miui.has_real_blur" -> "1"
     *
     * Flyme ROM标识
     * "ro.build.user" -> "flyme"
     * "persist.sys.use.flyme.icon" -> "true"
     * "ro.flyme.published" -> "true"
     * "ro.build.display.id" -> "Flyme OS 5.1.2.0U"
     * "ro.meizu.setupwizard.flyme" -> "true"
     *
     * EMUI ROM标识
     * "ro.build.version.emui" -> "EmotionUI_1.6"
     */

    private static final String KEY_DISPLAY = "ro.build.display.id";


    //MIUI标识
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String ROM_MI = "Xiaomi";
    //EMUI标识
    private static final String KEY_EMUI_VERSION_CODE = "ro.build.version.emui";
    private static final String ROM_HUAWEI = "HUAWEI";
    //Flyme标识
    private static final String KEY_FLYME_ID_FALG_KEY = "ro.build.display.id";
    private static final String KEY_FLYME_ID_FALG_VALUE_KEYWORD = "Flyme";
    private static final String KEY_FLYME_ICON_FALG = "persist.sys.use.flyme.icon";
    private static final String KEY_FLYME_SETUP_FALG = "ro.meizu.setupwizard.flyme";
    private static final String KEY_FLYME_PUBLISH_FALG = "ro.flyme.published";
    // oppo
    private static final Object KEY_OPPO_VERSION_CODE = "ro.build.version.opporom";

    /**
     * 手机品牌
     */
    // 小米
    public static final String PHONE_XIAOMI = "xiaomi";
    // 小米
    public static final String PHONE_XIAOMI2 = "Redmi";
    // 华为
    public static final String PHONE_HUAWEI1 = "Huawei";
    // 华为
    public static final String PHONE_HUAWEI2 = "HUAWEI";
    // 华为
    public static final String PHONE_HUAWEI3 = "HONOR";
    // 魅族
    public static final String PHONE_MEIZU = "Meizu";
    // 索尼
    public static final String PHONE_SONY = "sony";
    // 三星
    public static final String PHONE_SAMSUNG = "samsung";
    // LG
    public static final String PHONE_LG = "lg";
    // HTC
    public static final String PHONE_HTC = "htc";
    // NOVA
    public static final String PHONE_NOVA = "nova";
    // OPPO
    public static final String PHONE_OPPO = "OPPO";
    // 乐视
    public static final String PHONE_LeMobile = "LeMobile";
    // 联想
    public static final String PHONE_LENOVO = "lenovo";

    /**
     * @param
     * @return ROM_TYPE ROM类型的枚举
     * @datecreate at 2016/5/11 0011 9:46
     * @mehtodgetRomType
     * @description获取ROM类型，MIUI_ROM,    *FLYME_ROM,    * EMUI_ROM,    * OTHER_ROM
     */

    public static ROM_TYPE getRomType() {
        ROM_TYPE rom_type = ROM_TYPE.OTHER_ROM;
        try {
            if (getRom().equalsIgnoreCase(ROM_HUAWEI)){
                Log.i(TAG, "getRomType: EMUI_ROM");
                return ROM_TYPE.EMUI_ROM;
            }

            BuildProperties buildProperties = BuildProperties.getInstance();
            if (buildProperties.containsKey(KEY_EMUI_VERSION_CODE)) {
                Log.i(TAG, "getRomType: EMUI_ROM");
                return ROM_TYPE.EMUI_ROM;
            }

            if (getRom().equalsIgnoreCase(ROM_MI)){
                Log.i(TAG, "getRomType: MIUI_ROM");
                return ROM_TYPE.MIUI_ROM;
            }

            if (buildProperties.containsKey(KEY_MIUI_VERSION_CODE) || buildProperties.containsKey(KEY_MIUI_VERSION_NAME)) {
                Log.i(TAG, "getRomType: MIUI_ROM");
                return ROM_TYPE.MIUI_ROM;
            }

            if (buildProperties.containsKey(KEY_FLYME_ICON_FALG) || buildProperties.containsKey(KEY_FLYME_SETUP_FALG) || buildProperties.containsKey(KEY_FLYME_PUBLISH_FALG)) {
                Log.i(TAG, "getRomType: FLYME_ROM");
                return ROM_TYPE.FLYME_ROM;
            }
            if (buildProperties.containsKey(KEY_FLYME_ID_FALG_KEY)) {
                String romName = buildProperties.getProperty(KEY_FLYME_ID_FALG_KEY);
                if (!TextUtils.isEmpty(romName) && romName.contains(KEY_FLYME_ID_FALG_VALUE_KEYWORD)) {
                    Log.i(TAG, "getRomType: FLYME_ROM");
                    return ROM_TYPE.FLYME_ROM;
                }
            }

            if (getRom().equalsIgnoreCase("oppo")) {
                return ROM_TYPE.OPPO_ROM;
            }
            if (buildProperties.containsKey(KEY_OPPO_VERSION_CODE)) {
                return ROM_TYPE.OPPO_ROM;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "getRomType: other");
        return rom_type;
    }

    public static boolean isNewMiui() {
        Log.i(TAG, "isNewMiui: "+getMiuiVersion());
        if (getRomType() == ROM_TYPE.MIUI_ROM );
        return false;
    }

    /**
     * 获取小米 rom 版本号，获取失败返回 -1
     *
     * @return miui rom version code, if fail , return -1
     */
    public static String getMiuiVersion() {
        try {

            return BuildProperties.getInstance().getProperty("ro.miui.ui.version.code");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }


    public static class BuildProperties {

        private static BuildProperties ourInstance;

        public static BuildProperties getInstance() throws IOException {
            if (ourInstance == null) ourInstance = new BuildProperties();
            return ourInstance;
        }

        private final Properties properties;

        private BuildProperties() throws IOException {
            properties = new Properties();
            FileInputStream in = null;
            try {
                in = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"));
                properties.load(in);
            }catch (Exception e) {

            }finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception e) {

                    }
                }
            }
        }

        public boolean containsKey(final Object key) {
            return properties.containsKey(key);
        }

        public boolean containsValue(final Object value) {
            return properties.containsValue(value);
        }

        public String getProperty(final String name) {
            return properties.getProperty(name);
        }

        public String getProperty(final String name, final String defaultValue) {
            return properties.getProperty(name, defaultValue);
        }

        public Set<Map.Entry<Object, Object>> entrySet() {
            return properties.entrySet();
        }

        public boolean isEmpty() {
            return properties.isEmpty();

        }

        public Enumeration keys() {
            return properties.keys();

        }

        public Set keySet() {
            return properties.keySet();

        }

        public int size() {
            return properties.size();

        }

        public Collection values() {
            return properties.values();
        }

    }


    /**
     *
     */
    public static String getRom(){
        String romType = android.os.Build.MANUFACTURER;
        return romType;//rom定制商的名称
    }
    /**
     * 判断是否为miui
     * Is miui boolean.
     *
     * @return the boolean
     */
    public static boolean isMIUI() {
        String property = getSystemProperty(KEY_MIUI_VERSION_NAME, "");
        return !TextUtils.isEmpty(property);
    }

    /**
     * 判断miui版本是否大于等于6
     * Is miui 6 later boolean.
     *
     * @return the boolean
     */
    public static boolean isMIUI6Later() {
        String version = getMIUIVersion();
        int num;
        if ((!version.isEmpty())) {
            try {
                num = Integer.valueOf(version.substring(1));
                return num >= 6;
            } catch (NumberFormatException e) {
                return false;
            }
        } else
            return false;
    }

    /**
     * 获得miui的版本
     * Gets miui version.
     *
     * @return the miui version
     */
    public static String getMIUIVersion() {
        return isMIUI() ? getSystemProperty(KEY_MIUI_VERSION_NAME, "") : "";
    }

    /**
     * 判断是否为emui
     * Is emui boolean.
     *
     * @return the boolean
     */
    public static boolean isEMUI() {
        String property = getSystemProperty(KEY_EMUI_VERSION_CODE, "");
        return !TextUtils.isEmpty(property);
    }

    /**
     * 得到emui的版本
     * Gets emui version.
     *
     * @return the emui version
     */
    public static String getEMUIVersion() {
        return isEMUI() ? getSystemProperty(KEY_EMUI_VERSION_CODE, "") : "";
    }

    /**
     * 判断是否为emui3.1版本
     * Is emui 3 1 boolean.
     *
     * @return the boolean
     */
    public static boolean isEMUI3_1() {
        String property = getEMUIVersion();
        if ("EmotionUI 3".equals(property) || property.contains("EmotionUI_3.1")) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为emui3.0版本
     * Is emui 3 1 boolean.
     *
     * @return the boolean
     */
    public static boolean isEMUI3_0() {
        String property = getEMUIVersion();
        if (property.contains("EmotionUI_3.0")) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为flymeOS
     * Is flyme os boolean.
     *
     * @return the boolean
     */
    public static boolean isFlymeOS() {
        return getFlymeOSFlag().toLowerCase().contains("flyme");
    }

    /**
     * 判断flymeOS的版本是否大于等于4
     * Is flyme os 4 later boolean.
     *
     * @return the boolean
     */
    public static boolean isFlymeOS4Later() {
        String version = getFlymeOSVersion();
        int num;
        if (!version.isEmpty()) {
            try {
                if (version.toLowerCase().contains("os")) {
                    num = Integer.valueOf(version.substring(9, 10));
                } else {
                    num = Integer.valueOf(version.substring(6, 7));
                }
                return num >= 4;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * 判断flymeOS的版本是否等于5
     * Is flyme os 5 boolean.
     *
     * @return the boolean
     */
    public static boolean isFlymeOS5() {
        String version = getFlymeOSVersion();
        int num;
        if (!version.isEmpty()) {
            try {
                if (version.toLowerCase().contains("os")) {
                    num = Integer.valueOf(version.substring(9, 10));
                } else {
                    num = Integer.valueOf(version.substring(6, 7));
                }
                return num == 5;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }


    /**
     * 得到flymeOS的版本
     * Gets flyme os version.
     *
     * @return the flyme os version
     */
    public static String getFlymeOSVersion() {
        return isFlymeOS() ? getSystemProperty(KEY_DISPLAY, "") : "";
    }

    private static String getFlymeOSFlag() {
        return getSystemProperty(KEY_DISPLAY, "");
    }

    private static String getSystemProperty(String key, String defaultValue) {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            return (String) get.invoke(clz, key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public static List<String> getHuaWeiBrand(){
        List<String> huaweiList = new ArrayList<>();
        huaweiList.add(PHONE_HUAWEI1);
        huaweiList.add(PHONE_HUAWEI2);
        huaweiList.add(PHONE_HUAWEI3);
        return huaweiList;
    }

    public static List<String> getXiaoMiBrand(){
        List<String> xiaomiList = new ArrayList<>();
        xiaomiList.add(PHONE_XIAOMI);
        xiaomiList.add(PHONE_XIAOMI2);
        return xiaomiList;
    }
}