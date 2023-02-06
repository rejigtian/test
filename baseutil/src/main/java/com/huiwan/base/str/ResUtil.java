package com.huiwan.base.str;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import androidx.annotation.ArrayRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntegerRes;
import androidx.annotation.PluralsRes;
import androidx.annotation.StringRes;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.huiwan.base.ActivityTaskManager;
import com.huiwan.base.LibBaseUtil;

import java.util.Locale;

/**
 * date 2020/10/12
 * email july.oloy@qq.com
 *
 * @author leoyuu
 */
public final class ResUtil {
    private ResUtil(){}

    public static Resources getResource() {
        Activity activity = ActivityTaskManager.getInstance().getLastCreateActivity();
        if (activity != null) {
            return activity.getResources();
        }
        return LibBaseUtil.getApplication().getResources();
    }

    public static String getStr(@StringRes int resId) {
        return getResource().getString(resId);
    }

    public static String getQuantityStr(@PluralsRes int resId, int quantity, Object... args) {
        return getResource().getQuantityString(resId, quantity, args);
    }

    public static String[] getStrArr(@ArrayRes int resId) {
        return getResource().getStringArray(resId);
    }

    public static String getStr(@StringRes int resId, Object... formatArgs) {
        return getResource().getString(resId, formatArgs);
    }

    public static String getStrX(@StringRes int resId, @StringRes int argStrRes) {
        return getStr(resId, getStr(argStrRes));
    }

    public static String getString(@StringRes int resId) {
        return getResource().getString(resId);
    }

    public static int getInteger(@IntegerRes int resId) {
        return getResource().getInteger(resId);
    }

    @ColorInt
    public static int getColor(@ColorRes int res) {
        return getResource().getColor(res);
    }


    public static Drawable getDrawable(@DrawableRes int res) {
        return ResourcesCompat.getDrawable(getResource(), res, null);
    }

    public static Context attachBaseContext(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //7.0及以后
            return updateResources(context);
        } else {
            //7.0之前
            Configuration configuration = context.getResources().getConfiguration();
            configuration.locale = LibBaseUtil.getLocal();
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            context.getResources().updateConfiguration(configuration, displayMetrics);
            return context;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = LibBaseUtil.getLocal();
        return context.createConfigurationContext(configuration);
    }

    public static Drawable getDrawableWithTint(@DrawableRes int res, @ColorRes int color){
        Drawable drawable = DrawableCompat.wrap(getDrawable(res));
        DrawableCompat.setTint(drawable, getColor(color));
        return drawable;
    }

    public static ColorStateList getColorStateList(@ColorRes int res) {
        return getResource().getColorStateList(res);
    }
}
