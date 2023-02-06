package com.huiwan.base;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.huiwan.base.str.Language;
import com.huiwan.base.str.ResUtil;
import com.huiwan.base.util.IMMHelper;
import com.huiwan.platform.ThreadUtil;

import java.util.Locale;

/**
 * Created by bigwen on 2020/10/10.
 * 模块间公共的工具集合
 */
public class LibBaseUtil {

    private static final String LANG_PREF = "pf_lang";
    private static final String PREF_KEY_LANG = "lang";
    private static Application application;
    private static BaseConfig baseConfig = new BaseConfig("1.0.0", 1, "1.0.0", 1, "official", false, false, "",true, "app");
    private static int uid;
    private static Logger logger;
    private static boolean langFromPref = false;
    private static Language lang = Language.zhTw;
    private static Locale locale = Locale.getDefault();
    private static long initTime;
    private static SharedPreferences sp;

    public static void init(Application context, BaseConfig baseConfig, Logger logger) {
        application = context;
        sp = context.getSharedPreferences(LANG_PREF, Context.MODE_PRIVATE);
        initLang();
        LibBaseUtil.logger = logger;
        LibBaseUtil.baseConfig = baseConfig;
        ThreadUtil.init(new HwAndroid());
        ActivityTaskManager.install(context);
        checkUpdateLang();
        initTime = System.currentTimeMillis();
    }

    public static Application getApplication() {
        return application;
    }

    public static boolean envDebug() {
        return baseConfig.envDebug;
    }

    public static boolean buildDebug() {
        return baseConfig.debugBuild;
    }

    public static BaseConfig getBaseConfig() {
        return baseConfig;
    }

    public static void logInfo(String tag, String fmt, Object... args) {
        if (logger != null) {
            logger.logInfo(tag, fmt, args);
        }
    }

    private static void initLang() {
        String language = sp.getString(PREF_KEY_LANG, "");
        if (!TextUtils.isEmpty(language)) {
            langFromPref = true;
            try {
                setLang(Language.valueOf(language));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (Locale.ENGLISH == getLocal()
                    || getLocal().toString().contains(Locale.ENGLISH.toString())){
                setLang(Language.en);
            } else if (Locale.TRADITIONAL_CHINESE == getLocal()
                    || getLocal().toString().contains("Hant")
                    || getLocal().toString().contains("zh_TW")
                    || getLocal().toString().contains("zh_HK")){
                setLang(Language.zhTw);
            } else {
                setLang(Language.zh);
            }
        }
    }

    public static void setLang(Language lang) {
        LibBaseUtil.lang = lang;
        savePref(lang);
        if (lang == Language.en) {
            locale = Locale.ENGLISH;
        } else if (lang == Language.zh) {
            locale = Locale.CHINESE;
        } else if (lang == Language.zhTw) {
            locale = Locale.TRADITIONAL_CHINESE;
        }
    }

    private static void savePref(Language lang) {
        sp.edit().putString(PREF_KEY_LANG, lang.name()).apply();
    }

    public static void checkUpdateLang() {
        if (langFromPref) {
            return;
        }
        String language = Locale.getDefault().toString();
        checkUpdateLang(language);
    }

    private static void checkUpdateLang(String language) {
        if (language != null){
            if (language.contains("en")) {
                lang = Language.en;
            } else if (language.equals("zh_CN")) {
                lang = Language.zh;
            } else {
                lang = Language.zhTw;
            }
        } else {
            lang = Language.zhTw;
        }
    }

    public static Language getLang() {
        if (isBuildChina()) return Language.zh;
        return lang;
    }

    public static Locale getLocal() {
        if (isBuildChina()) return Locale.CHINESE;
        return locale;
    }

    public static void setLangFromPref(boolean isFromPref){
        langFromPref = isFromPref;
    }

    private static void logErr(String tag, String fmt, Object... args) {
        if (logger != null) {
            logger.logErr(tag, fmt, args);
        }
    }

    public interface Logger {
        void logInfo(String tag, String fmt, Object... args);
        void logErr(String tag, String fmt, Object... args);
    }

    public static boolean isBuildChina() {
        return BuildConfig.BUILD_AREA.equals(BuildConfig.AREA_CHINA);
    }

    public static boolean isBuildOverSeas() {
        return BuildConfig.BUILD_AREA.equals(BuildConfig.AREA_OVERSEAS);
    }

    public static String getAppName() {
        return baseConfig.appName;
    }

    public static long getInitTime() {
        return initTime;
    }

    public static void hideKeyBoard(Context context, View searchEt) {
        searchEt.setFocusable(true);
        searchEt.requestFocus();
        IMMHelper.hideSoftInput(context, searchEt.getWindowToken());
    }
}
