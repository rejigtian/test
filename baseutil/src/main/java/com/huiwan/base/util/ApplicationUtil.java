package com.huiwan.base.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;

import com.huiwan.base.LibBaseUtil;

import java.util.List;

/**
 * Created by zhuguangwen on 15/2/28.
 * email 979343670@qq.com
 */
public class ApplicationUtil {

    public static boolean isApplicationAtTop(){
        Context context = LibBaseUtil.getApplication();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            if (tasksInfo.get(0).topActivity.getPackageName().startsWith(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isActivityAtTop(Class activityClass){
        Context context = LibBaseUtil.getApplication();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String topActivityClassName = mActivityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        String destActivityClassName = activityClass.getName();
        return topActivityClassName.equals(destActivityClassName);
    }

    public boolean isResumeFromHome(Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(10);
        try {
            if (tasksInfo.size() > 1) {
                String lastApp = tasksInfo.get(1).topActivity.getPackageName();
                String curApp = tasksInfo.get(0).topActivity.getPackageName();
                if (tasksInfo.get(1).topActivity.getPackageName().startsWith(context.getPackageName())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean isPackageInstalled(Context context, String packageName) {
        if (null == context) {
            throw new IllegalArgumentException("context may not be null.");
        }
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    public static boolean isAppInstalled(String pkgName) {
        try {
            PackageManager packageManager = LibBaseUtil.getApplication().getPackageManager();
            packageManager.getPackageInfo(pkgName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
