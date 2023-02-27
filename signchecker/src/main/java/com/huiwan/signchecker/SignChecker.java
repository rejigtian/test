package com.huiwan.signchecker;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.Log;

import com.rejig.base.Base64Util;
import com.rejig.base.SecurityUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;

public class SignChecker {

    private static final String SIGN = "e985b56401ab43c82924545e2ce6fd41";

    public static boolean doNormalSignCheck(Context context) {
        String trueSignMD5 = SIGN;
        String nowSignMD5 = "";
        try {
            // 得到签名的MD5
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            String signBase64 = Base64Util.encode(signs[0].toByteArray());
            nowSignMD5 = SecurityUtil.md5String(signBase64);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.e("SignChecker", "TinkerProxyApplication: " + nowSignMD5);
        return trueSignMD5.equals(nowSignMD5);
    }

    public static String encryptionMD5(byte[] byteStr) {
        MessageDigest messageDigest = null;
        StringBuffer md5StrBuff = new StringBuffer();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(byteStr);
            byte[] byteArray = messageDigest.digest();
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5StrBuff.toString();
    }

    /**
     - 校验 application
     */
    public static boolean checkApplication(Application application){
        Application nowApplication = application;
        String trueApplicationName = "SampleApplication";
        String nowApplicationName = nowApplication.getClass().getSimpleName();
        return trueApplicationName.equals(nowApplicationName);
    }

    /**
     * 检测 PM 代理
     */
    @SuppressLint("PrivateApi")
    public static boolean checkPMProxy(Context context){
        String truePMName = "android.content.pm.IPackageManager$Stub$Proxy";
        String nowPMName = "";
        try {
            // 被代理的对象是 PackageManager.mPM
            PackageManager packageManager = context.getPackageManager();
            Field mPMField = packageManager.getClass().getDeclaredField("mPM");
            mPMField.setAccessible(true);
            Object mPM = mPMField.get(packageManager);
            // 取得类名
            nowPMName = mPM.getClass().getName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 类名改变说明被代理了
        return truePMName.equals(nowPMName);
    }

    /**
     * 使用较新的 API 检测
     */
    @SuppressLint("PackageManagerGetSignatures")
    public static boolean useNewAPICheck(Context context){
        String trueSignMD5 = SIGN;
        String nowSignMD5 = "";
        Signature[] signs = null;
        try {
            // 得到签名 MD5
            if (Build.VERSION.SDK_INT >= 28) {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                        context.getPackageName(),
                        PackageManager.GET_SIGNING_CERTIFICATES);
                signs = packageInfo.signingInfo.getApkContentsSigners();
            } else {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                        context.getPackageName(),
                        PackageManager.GET_SIGNATURES);
                signs = packageInfo.signatures;
            }
            String signBase64 = Base64Util.encode(signs[0].toByteArray());
            nowSignMD5 = SecurityUtil.md5String(signBase64);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.e("SignChecker", "TinkerProxyApplication: " + nowSignMD5);
        return trueSignMD5.equals(nowSignMD5);
    }

    /**
     * 手动构建 Context
     */
    @SuppressLint({"DiscouragedPrivateApi","PrivateApi"})
    public static Context getContext() throws ClassNotFoundException,
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException,
            NoSuchFieldException,
            NullPointerException{

        // 反射获取 ActivityThread 的 currentActivityThread 获取 mainThread
        Class activityThreadClass = Class.forName("android.app.ActivityThread");
        Method currentActivityThreadMethod =
                activityThreadClass.getDeclaredMethod("currentActivityThread");
        currentActivityThreadMethod.setAccessible(true);
        Object mainThreadObj = currentActivityThreadMethod.invoke(null);

        // 反射获取 mainThread 实例中的 mBoundApplication 字段
        Field mBoundApplicationField = activityThreadClass.getDeclaredField("mBoundApplication");
        mBoundApplicationField.setAccessible(true);
        Object mBoundApplicationObj = mBoundApplicationField.get(mainThreadObj);

        // 获取 mBoundApplication 的 packageInfo 变量
        if (mBoundApplicationObj == null) throw new NullPointerException("mBoundApplicationObj 反射值空");
        Class mBoundApplicationClass = mBoundApplicationObj.getClass();
        Field infoField = mBoundApplicationClass.getDeclaredField("info");
        infoField.setAccessible(true);
        Object packageInfoObj = infoField.get(mBoundApplicationObj);

        // 反射调用 ContextImpl.createAppContext(ActivityThread mainThread, LoadedApk packageInfo)
        if (mainThreadObj == null) throw new NullPointerException("mainThreadObj 反射值空");
        if (packageInfoObj == null) throw new NullPointerException("packageInfoObj 反射值空");
        Method createAppContextMethod = Class.forName("android.app.ContextImpl").getDeclaredMethod(
                "createAppContext",
                mainThreadObj.getClass(),
                packageInfoObj.getClass());
        createAppContextMethod.setAccessible(true);
        return (Context) createAppContextMethod.invoke(null, mainThreadObj, packageInfoObj);

    }

}
