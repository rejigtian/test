package com.wepie.libpermission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.provider.Settings;

import androidx.core.content.PermissionChecker;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * date 2018/9/4
 * email july.oloy@qq.com
 *
 * @author leoyuu
 */
public class PermissionUtil {
    /**
     * 是否是6.0以上版本
     */
    public static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 是否是8.0以上版本
     */
    public static boolean isOverOreo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    /**
     * 返回应用程序在清单文件中注册的权限
     */
    static List<String> getManifestPermissions(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            return Arrays.asList(pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 是否有安装权限
     */
    public static boolean hasInstallPermission(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.O ||
                context.getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.O ||
                context.getPackageManager().canRequestPackageInstalls();
    }

    /**
     * 是否有悬浮窗权限
     */
    static boolean hasOverlaysPermission(Context context) {
        if (isOverMarshmallow() && context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    /**
     * 获取没有授予的权限
     *
     * @param context     上下文对象
     * @param permissions 需要请求的权限组
     */
    public static ArrayList<String> getFailPermissions(Context context, List<String> permissions) {
        if (context.getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.M) {
            return new ArrayList<>();
        }

        //如果是安卓6.0以下版本就返回null
        if (!isOverMarshmallow()) {
            return new ArrayList<>();
        }

        ArrayList<String> failPermissions = new ArrayList<>();

        for (String permission : permissions) {
            //把没有授予过的权限加入到集合中
            if (!hasPermission(context, permission)) {
                failPermissions.add(permission);
            }
        }

        return failPermissions;
    }

    /**
     * 是否还能继续申请没有授予的权限
     *
     * @param activity              Activity对象
     * @param failPermissions       失败的权限
     */
    static boolean isRequestDeniedPermission(Activity activity, List<String> failPermissions) {
        for (String permission : failPermissions) {
            //检查是否还有权限还能继续申请的（这里指没有被授予的权限但是也没有被永久拒绝的）
            if (!checkSinglePermissionPermanentDenied(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 在权限组检查某个权限是否被永久拒绝
     *
     * @param activity              Activity对象
     * @param permissions            请求的权限
     */
    static boolean checkMorePermissionPermanentDenied(Activity activity, List<String> permissions) {
        for (String permission : permissions) {
            if (checkSinglePermissionPermanentDenied(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查某个权限是否被永久拒绝
     *
     * @param context              Activity context对象
     * @param permission           请求的权限
     */
    static boolean checkSinglePermissionPermanentDenied(Context context, String permission) {
        if (isOverMarshmallow()) {
            if (context instanceof Activity) {
                Activity activity = (Activity)context;
                if (!hasPermission(context, permission)) {
                    return !activity.shouldShowRequestPermissionRationale(permission);
                }
            }
        }
        return false;
    }

    public static boolean hasPermission(Context context, String permission) {
        if (PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED) {
            return moreCheckIfNeed(context, permission);
        }
        return false;
    }

    /**
     * 获取没有授予的权限
     *
     * @param permissions  需要请求的权限组
     * @param grantResults 允许结果组
     */
    static List<String> getFailPermissions(String[] permissions, int[] grantResults) {
        List<String> failPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {

            //把没有授予过的权限加入到集合中，-1表示没有授予，0表示已经授予
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                failPermissions.add(permissions[i]);
            }
        }
        return failPermissions;
    }

    /**
     * 获取已授予的权限
     *
     * @param permissions  需要请求的权限组
     * @param grantResults 允许结果组
     */
    static List<String> getSucceedPermissions(String[] permissions, int[] grantResults) {
        List<String> succeedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                succeedPermissions.add(permissions[i]);
            }
        }
        return succeedPermissions;
    }

    private static boolean moreCheckIfNeed(Context context, String permission) {
        return true;

    }

    private static boolean hasRecordPermission(Context context) {
        File file = new File(context.getApplicationContext().getCacheDir() + ".lib_permission_" + System.currentTimeMillis() + ".record");
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            MediaRecorder mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setAudioEncodingBitRate(1000 * 32);
            mediaRecorder.setOutputFile(file.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();
            mediaRecorder.release();
            file.delete();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
