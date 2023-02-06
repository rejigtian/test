package com.huiwan.base.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import androidx.core.graphics.ColorUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import com.huiwan.base.util.OSUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


// Created by bigwen on 2018/4/28.
public class StatusBarUtil {

    public static void initStatusBar(Activity activity) {
        if (isSupportFullScreen()) {
            Window mWindow = activity.getWindow();
            mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            View decorView = mWindow.getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //全屏
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE //防止系统栏隐藏时内容区域大小发生变化
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;//暗色状态栏字体
            decorView.setSystemUiVisibility(option);
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);  //需要设置这个才能设置状态栏颜色
            mWindow.setStatusBarColor(Color.TRANSPARENT);//设置状态栏颜色

            if (OSUtils.isMIUI6Later()) {
                setMIUIStatusBarDarkFont(mWindow, true);
            }
        }
    }

    public static void showStatusBar(Activity activity) {
        WindowInsetsController insetsController;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            insetsController = activity.getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.show(WindowInsets.Type.statusBars());
            }
        } else {
            activity.getWindow().clearFlags(1024);
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //全屏
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE; //防止系统栏隐藏时内容区域大小发生变化
            activity.getWindow().getDecorView().setSystemUiVisibility(option);
        }
        initStatusBar(activity);
    }

    public static void fillNavigationBar(Activity activity) {
        if (isSupportFullScreen()) {
            Window mWindow = activity.getWindow();
            View decorView = mWindow.getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            mWindow.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    public static boolean isSupportFullScreen() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static void changeStatusFontColor(int color, Context context) {
        if (ColorUtils.calculateLuminance(color) < 0.5) {
            setStatusFontWhiteColor(context);
        }
    }

    public static void setStatusFontWhiteColor(Context context) {
        if (isSupportFullScreen()) {
            Activity activity = (Activity) context;
            View decorView = activity.getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decorView.setSystemUiVisibility(flags);
        }
    }

    public static void setStatusFontDarkColor(Context context) {
        if (isSupportFullScreen()) {
            Activity activity = (Activity) context;
            View decorView = activity.getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decorView.setSystemUiVisibility(flags);
        }
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     *
     * @return boolean 成功执行返回true
     */
    private static void setMIUIStatusBarDarkFont(Window window, boolean darkFont) {
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (darkFont) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
