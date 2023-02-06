package com.huiwan.base.util.log;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.huiwan.base.BuildConfig;
import com.huiwan.base.util.StringUtil;
import com.huiwan.base.util.ToastUtil;

import java.util.Locale;

/**
 * date 2018/3/29
 * email july.oloy@qq.com
 *
 * @author leoyuu
 */
@SuppressWarnings("unused")
public class TimeLogger {

    private Handler handler = new Handler(Looper.getMainLooper());
    private static final String TAG = TimeLogger.class.getSimpleName();
    private static long startTime = 0;
    private static long lastTime = 0;

    /**
     * 用于记录时间
     */
    public static void t(){
        if (!BuildConfig.DEBUG){
            return;
        }

        if (startTime == 0){
            startTime = System.currentTimeMillis();
            lastTime = startTime;
        }

        StackTraceElement ele = Thread.currentThread().getStackTrace()[3];
        long now = System.currentTimeMillis();
        long divFromLast = now - lastTime;
        long divFromStart = now - startTime;
        lastTime = now;
        String logMsg = String.format(Locale.CHINA, "cause: %5d total: %5d\tat %s.%s(%s:%d)",
                divFromLast, divFromStart, ele.getClassName(), ele.getMethodName(), ele.getFileName(), ele.getLineNumber());
        android.util.Log.i(TAG, logMsg);
    }

    public static void resetTime(){
        startTime = 0;
    }

    public static void msg(String msg) {
        if (!BuildConfig.DEBUG){
            return;
        }
        StackTraceElement ele = Thread.currentThread().getStackTrace()[3];
        String logMsg = String.format(Locale.CHINA, "%s\t\t\nat %s.%s(%s:%d)",
                msg, ele.getClassName(), ele.getMethodName(), ele.getFileName(), ele.getLineNumber());
        android.util.Log.d(ele.getFileName(), logMsg);
    }

    public static void msgNoStack(String msg, Object... args) {
        if (!BuildConfig.DEBUG){
            return;
        }
        StackTraceElement ele = Thread.currentThread().getStackTrace()[3];
        logD(ele.getFileName(), msg, null, args);
    }

    public static void methodStart() {
        if (!BuildConfig.DEBUG){
            return;
        }
        StackTraceElement ele = Thread.currentThread().getStackTrace()[3];
        android.util.Log.d(ele.getFileName(), ele.getMethodName() + " start");
    }

    public static void err(String msg, Object... args) {
        if (!BuildConfig.DEBUG){
            return;
        }
        StackTraceElement ele = Thread.currentThread().getStackTrace()[3];
        String logStr = formatS(msg, args);
        String logMsg = String.format(Locale.CHINA, "%s\t\t\nat %s.%s(%s:%d)",
                logStr, ele.getClassName(), ele.getMethodName(), ele.getFileName(), ele.getLineNumber());
        android.util.Log.e(ele.getFileName(), logMsg);
    }

    private static long lastShowTipTime = 0;
    public static void notifyIfDebug(String msg) {
        if (BuildConfig.DEBUG) {
            if (System.currentTimeMillis() - lastShowTipTime > 60 * 1000) {
                ToastUtil.show(msg);
                lastShowTipTime = System.currentTimeMillis();
            }
        }
        err(msg);
    }


    private static void logE(String tag, String fmt, Throwable t, Object ... args) {
        Log.e(tag, formatS(fmt, args), t);
    }

    private static void logD(String tag, String fmt, Throwable t, Object ... args) {
        Log.d(tag, formatS(fmt, args), t);
    }

    public static String formatS(String fmt, Object... args) {
        return StringUtil.formatS(fmt, args);
    }
}
