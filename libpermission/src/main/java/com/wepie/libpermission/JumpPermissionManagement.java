package com.wepie.libpermission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JumpPermissionManagement {
    /**
     * Build.MANUFACTURER
     */
    private static final String MANUFACTURER_HUAWEI = "Huawei";//华为
    private static final String MANUFACTURER_MEIZU = "Meizu";//魅族
    private static final String MANUFACTURER_XIAOMI = "Xiaomi";//小米
    private static final String MANUFACTURER_SONY = "Sony";//索尼
    private static final String MANUFACTURER_OPPO = "OPPO";
    private static final String MANUFACTURER_LG = "LG";
    private static final String MANUFACTURER_VIVO = "vivo";
    private static final String MANUFACTURER_SAMSUNG = "samsung";//三星
    private static final String MANUFACTURER_LETV = "Letv";//乐视
    private static final String MANUFACTURER_ZTE = "ZTE";//中兴
    private static final String MANUFACTURER_YULONG = "YuLong";//酷派
    private static final String MANUFACTURER_LENOVO = "LENOVO";//联想

    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_OPEN_NOTIFICATION = 1;


    /**
     * @param activity
     */
    public static void goToAppSetting(Activity activity) {
        goToAppSetting(activity, TYPE_DEFAULT);
    }

    /**
     * 此函数可以自己定义
     *
     * @param context
     * @param openType 需要跳转界面类型
     */
    public static void goToAppSetting(Context context, int openType) {
        final Activity activity = ContextUtil.getActivityFromContext(context);
        if (activity == null) return;

        if (openType == TYPE_OPEN_NOTIFICATION && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean success = startNotificationActivity(activity);
            if (success) {
                return;
            }
        }

        try {
            switch (Build.MANUFACTURER) {
                case MANUFACTURER_HUAWEI:
                    Huawei(activity);
                    break;
                case MANUFACTURER_MEIZU:
                    Meizu(activity);
                    break;
                case MANUFACTURER_XIAOMI:
                    Xiaomi(activity, openType);
                    break;
                case MANUFACTURER_SONY:
                    Sony(activity);
                    break;
                case MANUFACTURER_OPPO:
                    OPPO(activity);
                    break;
                case MANUFACTURER_LG:
                    LG(activity);
                    break;
                case MANUFACTURER_LETV:
                    Letv(activity);
                    break;
                default:
                    ApplicationInfo(activity);
                    Log.e("goToAppSetting", "目前暂不支持此系统");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ApplicationInfo(activity);
        }
    }

    public static void Huawei(Activity activity) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", activity.getPackageName());
        ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
        intent.setComponent(comp);
        activity.startActivity(intent);
    }

    public static void Meizu(Activity activity) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("packageName", activity.getPackageName());
        activity.startActivity(intent);
    }

    public static void Xiaomi(Activity activity, int openType) {
        Intent intent = null;
        String miuiVersion = getMiuiVersion();

        if (openType == TYPE_OPEN_NOTIFICATION) {
            ApplicationInfo(activity);
        } else {
            if ("V6".equals(miuiVersion) || "V7".equals(miuiVersion)) {
                intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                intent.putExtra("extra_pkgname", activity.getPackageName());
            } else if ("V8".equals(miuiVersion) || "V9".equals(miuiVersion) || "V10".equals(miuiVersion)) {
                intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
                intent.putExtra("extra_pkgname", activity.getPackageName());
            }
        }

        if (null != intent) {
            activity.startActivity(intent);
        } else {
            ApplicationInfo(activity);
        }
    }

    public static void Sony(Activity activity) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", activity.getPackageName());
        ComponentName comp = new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity");
        intent.setComponent(comp);
        activity.startActivity(intent);
    }

    public static void OPPO(Activity activity) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", activity.getPackageName());
        ComponentName comp = new ComponentName("com.coloros.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
        intent.setComponent(comp);
        activity.startActivity(intent);
    }

    public static void LG(Activity activity) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", activity.getPackageName());
        ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
        intent.setComponent(comp);
        activity.startActivity(intent);
    }

    public static void Letv(Activity activity) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", activity.getPackageName());
        ComponentName comp = new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps");
        intent.setComponent(comp);
        activity.startActivity(intent);
    }

    /**
     * 只能打开到自带安全软件
     *
     * @param activity
     */
    public static void _360(Activity activity) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", activity.getPackageName());
        ComponentName comp = new ComponentName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
        intent.setComponent(comp);
        activity.startActivity(intent);
    }

    /**
     * 应用信息界面
     *
     * @param activity
     */
    public static void ApplicationInfo(Activity activity) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivity(localIntent);
    }

    /**
     * 系统设置界面
     *
     * @param activity
     */
    public static void SystemConfig(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        activity.startActivity(intent);
    }

    private static String getMiuiVersion() {
        String line = "";
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop ro.miui.ui.version.name");
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return "";
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
        return line;
    }

    @TargetApi(26)
    public static boolean startNotificationActivity(Context context) {
        try {
            Intent notifyIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            notifyIntent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            context.startActivity(notifyIntent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}