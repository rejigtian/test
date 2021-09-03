package com.example.test.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * @author rejig
 * date 2021/04/22
 */
public class AppUtil {
    private static volatile AppUtil instance;
    Application application;
    private int appCount = 0;
    private long initTime;
    private Handler mHandler;
    private WeakReference<Activity> topActivity;
    private String topActivityName = "";
    private SharedPreferences sp;

    public static final String TAG = "WPApplication";
    public static final String PATCH_VERSION_CODE = "patch_version";

    public static AppUtil getInstance() {
        if (instance == null) {
            synchronized (AppUtil.class) {
                if (instance == null) {
                    instance = new AppUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 调用时机应在所有初始化之前，保证其第一时间完成。最好在super.onCreate之后立即调用。
     * @param application 进程的application
     */
    public void init(Application application) {
        this.application = application;
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Nullable
    public Activity getTopActivity() {
        return topActivity.get();
    }


    public boolean isTopActivity(Class<?> activity) {
        if (activity == null) return false;
        return activity.getName().equals(topActivityName);
    }

    public int getResumeCount() {
        return appCount;
    }

    public static Application getApplication() {
        return getInstance().application;
    }

    public long getInitTime() {
        return initTime;
    }

    public SharedPreferences getPatchSp() {
        return sp;
    }

    public String getMetaDataValue(Context context, String meatName) {
        String value = null;
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (applicationInfo != null && applicationInfo.metaData != null) {
                Object object = applicationInfo.metaData.get(meatName);
                if (object != null) {
                    value = object.toString();
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }

}
