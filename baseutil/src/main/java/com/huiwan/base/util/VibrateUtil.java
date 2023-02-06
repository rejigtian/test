package com.huiwan.base.util;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

// Created by bigwen on 2018/9/17.
public class VibrateUtil {

    private Vibrator vib;

    public VibrateUtil(final Activity activity) {
        vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
    }

    public void shortVibrate() {
        if (vib != null) {
            vib.vibrate(20);
        }
    }

    public void vibrate2(int time) {
        if (vib != null) {
            vib.vibrate(time);
        }
    }
}
