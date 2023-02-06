package com.huiwan.base;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import com.huiwan.platform.Hw;
import com.huiwan.platform.exec.UIRunner;

class HwAndroid implements Hw {
    @Override
    public UIRunner uiRunner() {
        return new UIRunner() {
            private final Handler mainHandler = new Handler(Looper.getMainLooper());

            @Override
            public void run(Runnable runnable) {
                mainHandler.post(runnable);
            }

            @Override
            public void runDelay(Runnable runnable, long delayTimeInMill) {
                mainHandler.postDelayed(runnable, delayTimeInMill);
            }
        };
    }

    @Override
    public void setThreadPriority(int priority) {
        try {
            Process.setThreadPriority(priority);
        } catch (SecurityException ignored) {

        }
    }
}
