package com.huiwan.base.util.fitter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.huiwan.base.util.OSUtils;
import com.huiwan.base.util.StatusBarUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * 窗口全屏展示
 * @author rejig
 * date 2020/10/13
 */
public class FullScreenUtil {

    /**
     * 全屏展示带透明状态栏
     * @param window 窗口
     */
    public static void setFullScreenWithStatusBar(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            View decorView = window.getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //全屏
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE //防止系统栏隐藏时内容区域大小发生变化
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;//暗色状态栏字体
            decorView.setSystemUiVisibility(option);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);  //需要设置这个才能设置状态栏颜色
            window.setStatusBarColor(Color.TRANSPARENT);//设置状态栏颜色
        }
        setFullScrrenCompatible(window);
    }

    /**
     * 全屏展示，不带状态栏
     * @param window 窗口
     */
    public static void setFullScreen(Window window) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setFullScrrenCompatible(window);
    }

    /**
     * 兼容版本的全屏展示
     * @param window 窗口
     */
    public static void setFullScrrenCompatible(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            window.setAttributes(lp);
            return;
        }
        if (Build.BRAND.equals(OSUtils.PHONE_XIAOMI) && hasXiaoMiNotch(window.getContext())) {
            setXiaoMiFullScreenInDisplayCutout(window);
        }
        if (OSUtils.getHuaWeiBrand().contains(Build.BRAND) && hasXiaoMiNotch(window.getContext())) {
            setHuaWeiFullScreenInDisplayCutout(window);
        }
    }

    /**
     * 全屏展示带透明状态栏带虚拟按键
     * @param window 窗口
     */
    public static void setStatusBarTrans(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);  //需要设置这个才能设置状态栏颜色
            window.setStatusBarColor(Color.TRANSPARENT);//设置状态栏颜色
        }
    }

    /*小米刘海屏全屏显示FLAG*/
    public static final int FLAG_NOTCH_SUPPORT = 0x00000100; // 开启配置
    public static final int FLAG_NOTCH_PORTRAIT = 0x00000200; // 竖屏配置
    public static final int FLAG_NOTCH_HORIZONTAL = 0x00000400; // 横屏配置

    /**
     * 判断是否有刘海屏
     *
     * @param context 上下文
     * @return true：有刘海屏；false：没有刘海屏
     */
    private static boolean hasXiaoMiNotch(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");
            Method get = SystemProperties.getMethod("getInt", String.class, int.class);
            ret = (Integer) get.invoke(SystemProperties, "ro.miui.notch", 0) == 1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return ret;
        }
    }



    /**
     * 小米android O设置应用窗口在刘海屏手机使用刘海区
     * <p>
     * 通过添加窗口FLAG的方式设置页面使用刘海区显示
     *
     * @param window 应用页面window对象
     */
    private static void setXiaoMiFullScreenInDisplayCutout(Window window) {
        // 竖屏绘制到耳朵区
        int flag = FLAG_NOTCH_SUPPORT | FLAG_NOTCH_PORTRAIT;
        try {
            Method method = Window.class.getMethod("addExtraFlags",
                    int.class);
            method.invoke(window, flag);
        } catch (Exception e) {
            Log.e("test", "addExtraFlags not found.");
        }
    }

    /**
     * 华为判断是否有刘海屏
     *
     * @param context
     * @return true：有刘海屏；false：没有刘海屏
     */
    private static boolean hasHuaWeiNotch(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e("test", "hasNotchInScreen ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("test", "hasNotchInScreen NoSuchMethodException");
        } catch (Exception e) {
            Log.e("test", "hasNotchInScreen Exception");
        } finally {
            return ret;
        }
    }

    /**
     * 华为android O设置应用窗口在刘海屏手机使用刘海区
     * <p>
     * 通过添加窗口FLAG的方式设置页面使用刘海区显示
     *
     * @param window 应用页面window对象
     */
    private static void setHuaWeiFullScreenInDisplayCutout(Window window) {
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        try {
            Class layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx");
            Constructor con = layoutParamsExCls.getConstructor(WindowManager.LayoutParams.class);
            Object layoutParamsExObj = con.newInstance(layoutParams);
            Method method = layoutParamsExCls.getMethod("addHwFlags", int.class);
            method.invoke(layoutParamsExObj, FLAG_NOTCH_SUPPORT);
        } catch (ClassNotFoundException e) {
            Log.e("test", "hw add notch screen flag api error");
        } catch (Exception e) {
            Log.e("test", "other Exception");
        }
    }

}
