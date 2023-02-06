package com.huiwan.base.util;

import android.app.Activity;
import android.view.Choreographer;


/**
 * @author : rejig
 * date 2020-08-07
 * 用于检测屏幕丢帧以及刷新加载情况
 */
public class FPSDetectCallback implements Choreographer.FrameCallback {
    private long mLastFrameTimeNanos = 0;
    private long mFrameIntervalNanos;//刷新间隔
    private OnFrameDetect onFrameDetect;
    public float SCREEN_FPS=60;//默认刷新频率

    public FPSDetectCallback(Activity activity, long lastFrameTimeNanos, final OnFrameDetect onFrameDetect) {
        mLastFrameTimeNanos = lastFrameTimeNanos;
        SCREEN_FPS = ScreenUtil.getRefreshRate(activity);
        mFrameIntervalNanos = (long)(1000000000 / SCREEN_FPS);
        this.onFrameDetect=onFrameDetect;
    }

    @Override
    public void doFrame(long frameTimeNanos) {

        //初始化时间
        if (mLastFrameTimeNanos == 0) {
            mLastFrameTimeNanos = frameTimeNanos;
        }
        final long jitterNanos = frameTimeNanos - mLastFrameTimeNanos;
        if (jitterNanos >= mFrameIntervalNanos) {
            final long skippedFrames = jitterNanos / mFrameIntervalNanos;
            if(skippedFrames>1){
                onFrameDetect.onFrameLoss();
            }else {
                onFrameDetect.onFrameNormal();
            }
        }
        onFrameDetect.onFrameRefresh();
    }

    public interface OnFrameDetect{
        void onFrameLoss();
        void onFrameNormal();
        void onFrameRefresh();
    }

    public static class SimpleCallback implements OnFrameDetect{

        @Override
        public void onFrameLoss() {

        }

        @Override
        public void onFrameNormal() {

        }

        @Override
        public void onFrameRefresh() {

        }
    }
}
