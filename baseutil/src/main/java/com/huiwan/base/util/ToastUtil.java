package com.huiwan.base.util;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.StringRes;

import com.huiwan.base.LibBaseUtil;
import com.huiwan.base.str.ResUtil;
import com.huiwan.base.util.toast.ToastCompat;

public class ToastUtil {

    private static Handler sHandler = new Handler(Looper.getMainLooper());

    public static void debugShow(final String msg, Object... args){
        if(LibBaseUtil.buildDebug()){
            show(StringUtil.formatS(msg, args));
        }
    }

	public static void debugShow(final String msg){
		if(LibBaseUtil.buildDebug()){
            show(msg);
        }
	}

    public static void show(final String msg) {
        if ("".equals(msg) || msg == null) return;
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                //解决7.1 toast崩溃问题
                ToastCompat.makeText(LibBaseUtil.getApplication(), msg, ToastCompat.LENGTH_SHORT).show();
            }
        });
    }

    public static void showLong(final String msg) {
        if ("".equals(msg) || msg == null) return;
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                //解决7.1 toast崩溃问题
                ToastCompat.makeText(LibBaseUtil.getApplication(), msg, ToastCompat.LENGTH_LONG).show();
            }
        });
    }

    public static void show(@StringRes int text) {
	    show(ResUtil.getResource().getString(text));
    }
}
