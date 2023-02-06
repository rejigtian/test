package com.example.test.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.graphics.ColorUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


// Created by bigwen on 2018/4/28.
public class StatusBarUtil {

    public static void initStatusBar(Activity activity) {
        if (isSupportFullScreen()) {
            Window mWindow = activity.getWindow();
            initStatusBar(mWindow);
        }
    }

    public static void initStatusBar(Window mWindow) {
        if (isSupportFullScreen()) {
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
            Activity activity = ContextUtil.getActivityFromContext(context);
            if (activity == null) return;
            View decorView = activity.getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decorView.setSystemUiVisibility(flags);
        }
    }

    public static void setStatusFontDarkColor(Context context) {
        if (isSupportFullScreen()) {
            Activity activity = ContextUtil.getActivityFromContext(context);
            if (activity == null) return;
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

    //隐藏刘海儿屏顶部栏
    public static void hideBangStatus(Window window) {
        if (Build.VERSION.SDK_INT >= 28 && null != window) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            window.setAttributes(lp);
        }
    }

    public static void hideBottomBar(Window window) {
        try {
            // 隐藏地步bar https://stackoverflow.com/questions/21724420/how-to-hide-navigation-bar-permanently-in-android-activity
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            final View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(flags);
            decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(flags);
                }
            });
        } catch (Exception e) {
        }
    }
}
