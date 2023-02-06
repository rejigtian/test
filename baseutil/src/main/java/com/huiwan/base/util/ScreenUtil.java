package com.huiwan.base.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.LayoutDirection;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.WindowMetrics;

import com.huiwan.base.ActivityTaskManager;
import androidx.core.text.TextUtilsCompat;

import com.huiwan.base.LibBaseUtil;
import com.huiwan.base.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ScreenUtil {

    private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";
    private static final String NAV_BAR_HEIGHT_RES_NAME = "navigation_bar_height";
    private static final String NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME = "navigation_bar_height_landscape";
    private static final String NAV_BAR_WIDTH_RES_NAME = "navigation_bar_width";
    public static boolean isShowKeyBorad = false;
    public static float density = 0;
    public static long lastUpdateTime = 0;

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        if (density > 0) return (int) (dpValue * density + 0.5f);
        density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    public static int dip2px(float dpValue) {
        if (density > 0) return (int) (dpValue * density + 0.5f);
        Activity activity = ActivityTaskManager.getInstance().getTopActivity();
        if (activity != null){
            density = ActivityTaskManager.getInstance().getTopActivity().getResources().getDisplayMetrics().density;
            return (int) (dpValue * density + 0.5f);
        }
        density = LibBaseUtil.getApplication().getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param context
     * @param pxValue
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param context
     * @param spValue
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static float px2dp(float px) {
        if (density > 0) {
            return px / density;
        }
        Activity activity = ActivityTaskManager.getInstance().getTopActivity();
        if (activity != null){
            density = ActivityTaskManager.getInstance().getTopActivity().getResources().getDisplayMetrics().density;
            return px / density;
        }
        density = LibBaseUtil.getApplication().getResources().getDisplayMetrics().density;
        return px / density;
    }
    /**
     * 获取屏幕宽度
     */
    private static int screenWidth = 0;

    public static int getScreenWidth(Context context) {
        return getScreenWidth();
    }

    public static int getScreenWidthDp() {
        final float scale = LibBaseUtil.getApplication().getResources().getDisplayMetrics().density;
        return (int) (getScreenWidth() / scale);
    }

    public static int getScreenWidth() {
        if (screenWidth > 0 && System.currentTimeMillis() - lastUpdateTime < 60000) return screenWidth;
        return getScreenWidthRefresh();
    }

    public static int getScreenWidthRefresh() {
        lastUpdateTime = System.currentTimeMillis();
        Activity activity = ActivityTaskManager.getInstance().getTopActivity();
        if (activity != null) {
            DisplayMetrics metrics =  activity.getResources().getDisplayMetrics();
            screenWidth = activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? metrics.widthPixels : metrics.heightPixels;
        } else {
            Display display = ((WindowManager) LibBaseUtil.getApplication().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }
        return screenWidth;
    }

    public static int getScreenHeight() {
        if (screenHeight > 0 && System.currentTimeMillis() - lastUpdateTime < 60000) return screenHeight;
        return getScreenHeightRefresh();
    }

    public static int getScreenHeightRefresh() {
        lastUpdateTime = System.currentTimeMillis();
        Activity activity = ActivityTaskManager.getInstance().getTopActivity();
        if (activity != null) {
            DisplayMetrics metrics =  activity.getResources().getDisplayMetrics();
            screenHeight = activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ?  metrics.heightPixels : metrics.widthPixels;
        } else {
            Display display = ((WindowManager) LibBaseUtil.getApplication().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
        }
        return screenHeight;
    }

    //处理横竖屏
    public static int getScreenWidthWithOrientation(Context context) {
        if(isLandScape(context)){//横屏
            return Math.max(getScreenHeight(), getScreenWidth());
        } else {//竖屏
            return Math.min(getScreenHeight(), getScreenWidth());
        }
    }

    //处理横竖屏
    public static int getScreenHeightWithOrientation(Context context) {
        if(isLandScape(context)){//横屏
            return Math.min(getScreenHeight(), getScreenWidth());
        } else {//竖屏
            return Math.max(getScreenHeight(), getScreenWidth());
        }
    }

    public static boolean isLandScape(Context context) {
        Configuration cf = context.getResources().getConfiguration(); //获取设置的配置信息
        return cf.orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static boolean isLongScreen() {
        return getScreenHeight() * 1f / getScreenWidth() > 16 * 1f / 9;
    }

    /**
     * 获取屏幕高度
     */
    private static int screenHeight = 0;

    public static int getScreenHeight(Context context) {
        return getScreenHeight();
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight() {
        return getStatusBarHeight(LibBaseUtil.getApplication());
    }

    public static boolean isFullScreen(Activity context) {
        WindowManager.LayoutParams attrs = context.getWindow().getAttributes();
        return (attrs.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) ==
                WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }

    public static boolean existFlag(Activity activity, int flags){
        WindowManager.LayoutParams attrs= activity.getWindow().getAttributes();
        if(attrs.flags ==( (attrs.flags&~flags) | (flags&flags))){
            return true;
        }
        return false;
    }

    public static boolean isHdpiDevice(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.densityDpi <= DisplayMetrics.DENSITY_HIGH;
    }

    /**
     * 屏幕是否在点亮状态
     */
    public static boolean isScreenOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }

    public static int getRealHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getRealMetrics(dm);
        return dm.heightPixels;
    }


    public static int getNavigationBarHeight(Context context) {
        Resources res = context.getResources();
        int result = 0;
        boolean mInPortrait = (res.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
        if (hasNavBar((Activity) context)) {
            String key;
            if (mInPortrait) {
                key = NAV_BAR_HEIGHT_RES_NAME;
            } else {
                key = NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME;
            }
            return getInternalDimensionSize(res, key);
        }
        return result;
    }

    //方法不适用所有手机
    private static boolean isNavigationBarShow(Activity context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = context.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y!=size.y;
        }else {
            boolean menu = ViewConfiguration.get(context).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if(menu || back) {
                return false;
            }else {
                return true;
            }
        }
    }

    //方法不适用所有手机
    private static boolean isNavigationShow(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
        }
        return hasNavigationBar;
    }


    //方法不适用所有手机
    private static boolean hasNavBar(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }


    private static int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        try {
            Class clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int resourceId = Integer.parseInt(clazz.getField(key).get(object).toString());
            if (resourceId > 0)
                result = res.getDimensionPixelSize(resourceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int ios2dp(int ios) {
        int size = (int) LibBaseUtil.getApplication().getResources().getDimension(R.dimen.screen_size);
        return ios * size / 375;
    }

    public static int ios2px(int ios) {
       return ScreenUtil.dip2px(ScreenUtil.ios2dp(ios));
    }

    public static float getRefreshRate(Activity activity){
        Display display;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display = activity.getDisplay();
        } else {
            display =  ((WindowManager)activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        }
        return display.getRefreshRate();
    }

    public static boolean isShowKeyBorad() {
        return isShowKeyBorad;
    }

    public static void setIsShowKeyBorad(boolean isShowKeyBorad) {
        ScreenUtil.isShowKeyBorad = isShowKeyBorad;
    }

    /**
     * 异常情况下直接返回屏幕全图
     * @param activity activity
     * @param rect 需要截取的区域
     * @return 截取的图片Bitmap
     */
    public static Bitmap getExactScreenShoot(Activity activity, Rect rect) {
        Bitmap bitmap = getWindowScreenShoot(activity);
        if (rect.top + rect.height() > bitmap.getHeight() || rect.left + rect.width() > bitmap.getWidth()){
            return bitmap;
        }
        if (rect.height() < 0 || rect.width() < 0){
            return bitmap;
        }
        return Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(),rect.height());
    }

    public static Bitmap getWindowScreenShoot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        return view.getDrawingCache();
    }

    /**
     * 该方法检测一个点击事件是否落入在一个View内，换句话说，检测这个点击事件是否发生在该View上。
     *
     * @param view
     * @param x
     * @param y
     * @return
     */
    public static boolean touchEventInView(View view, float x, float y) {
        if (view == null) {
            return false;
        }

        int[] location = new int[2];
        view.getLocationOnScreen(location);

        int left = location[0];
        int top = location[1];

        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();

        if (y >= top && y <= bottom && x >= left && x <= right) {
            return true;
        }

        return false;
    }

    //获取竖屏宽
    public static int getScreenWidthWithPortrait() {
        return Math.min(getScreenHeight(), getScreenWidth());
    }

    //获取横屏高
    public static int getScreenHeightWithLandScape() {
        return Math.min(getScreenHeight(), getScreenWidth());
    }

    //获取横屏宽
    public static int getScreenWidthWithLandScape() {
        return Math.max(getScreenHeight(), getScreenWidth());
    }

    public static void updateScreenWH() {
        getScreenHeightRefresh();
        getScreenWidthRefresh();
    }
}
