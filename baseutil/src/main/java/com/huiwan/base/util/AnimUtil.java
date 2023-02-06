package com.huiwan.base.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.ScaleAnimation;

import com.huiwan.base.LibBaseUtil;
import com.huiwan.base.R;

public class AnimUtil {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private View view;

    public static final long READY_TIME_ALARM = 5000;
    public static final long READY_TIME_DURATION = 2000;
    private boolean hasAnim = false;

    public void addScaleAnim(View view){
        if (view == null) {
            return;
        }
        ScaleAnimation scaleAnimation = (ScaleAnimation) AnimationUtils.loadAnimation(LibBaseUtil.getApplication(), R.anim.scale_btn_anim);
        scaleAnimation.setInterpolator(new AnticipateInterpolator(3f));
        scaleAnimation.setRepeatCount(1);
        view.startAnimation(scaleAnimation);
        handler.postDelayed(animRunnable, READY_TIME_DURATION);
    }

    public void addScaleAnimDelay(long delayTime, View view){
        if (hasAnim){
            return;
        }
        hasAnim = true;
        this.view = view;
        handler.postDelayed(animRunnable, delayTime);
    }

    public void removeScaleAnim(){
        if (!hasAnim){
            return;
        }
        hasAnim = false;
        handler.removeCallbacks(animRunnable);
        if (view == null) {
            return;
        }
        view.clearAnimation();
    }

    public Runnable animRunnable = () -> addScaleAnim(view);

}
