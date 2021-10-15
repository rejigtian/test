package com.wepie.libpermission;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.drawable.Drawable;
import android.view.WindowManager;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * date 2018/12/27
 * email july.oloy@qq.com
 *
 * @author leoyuu
 */
public class ContextUtil {


    public static void finishActivity(Context context) {
        Activity activity = getActivityFromContext(context);
        if (activity != null) {
            activity.finish();
        }
    }

    /**
     * get activity from context wrapper
     * if the base context is an activity
     *
     * @param context the context
     * @return the activity,
     * null if the base context is not an activity
     */
    @Nullable
    public static Activity getActivityFromContext(Context context) {
        if (context instanceof Activity) {
            return (Activity)context;
        } else {
            while (context instanceof ContextWrapper) {
                if (context instanceof Activity) {
                    return (Activity) context;
                }
                context = ((ContextWrapper) context).getBaseContext();
            }
        }
        return null;
    }

    public static void setKeepScreenOn(Context context, boolean keepScreenOn) {
        Activity activity = getActivityFromContext(context);
        if (activity != null) {
            if (keepScreenOn) {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }
    }

    public static Drawable getDrawable(Context context, @DrawableRes int res, int width, int height) {
        Drawable drawable = ContextCompat.getDrawable(context, res);
        if (drawable != null) {
            drawable.setBounds(0, 0, width, height);
        }
        return drawable;
    }

}
