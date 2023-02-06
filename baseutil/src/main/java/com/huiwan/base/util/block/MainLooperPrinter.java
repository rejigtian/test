package com.huiwan.base.util.block;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.util.Printer;

/**
 * Created by bigwen on 2019-09-06.
 */
public class MainLooperPrinter implements Printer {

    private static final String START = ">>>>> Dispatching";
    private static final String END = "<<<<< Finished";
    private long mStartTimeMillis, mFinishTimeMillis;
    private LogMonitor logMonitor = new LogMonitor();
    private final static long BLOCK_TIME = 300;
    private static final String TAG = "MainLooperPrinter";


    public MainLooperPrinter() {
    }

    @Override
    public void println(String x) {
        if (x.contains(START)) {
            mStartTimeMillis = System.currentTimeMillis();
            logMonitor.startMonitor();
        } else if (x.contains(END)) {
            mFinishTimeMillis = System.currentTimeMillis();
            long duration = mFinishTimeMillis - mStartTimeMillis;
            logMonitor.removeMonitor();
        }
    }

    private boolean isBlock(long duration) {
        return duration > BLOCK_TIME;
    }

    private static class LogMonitor {
        private Handler mHandler;

        private LogMonitor() {
            HandlerThread mHandlerThread = new HandlerThread(TAG);
            mHandlerThread.start();
            mHandler = new Handler(mHandlerThread.getLooper());
        }

        private Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                StringBuilder sb = new StringBuilder();
                StackTraceElement[] stackTrace = Looper.getMainLooper().getThread().getStackTrace();
                for (StackTraceElement s : stackTrace) {
                    sb.append(s.toString());
                    sb.append('\n');
                }
                Log.w(TAG, sb.toString());
            }
        };

        private void startMonitor() {
            mHandler.postDelayed(mRunnable, BLOCK_TIME);
        }

        private void removeMonitor() {
            mHandler.removeCallbacks(mRunnable);
        }

    }
}
