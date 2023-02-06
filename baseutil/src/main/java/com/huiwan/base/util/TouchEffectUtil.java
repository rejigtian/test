package com.huiwan.base.util;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by bigwen on 2017/1/6.
 * 变浅按压效果
 */
public class TouchEffectUtil {

    /**
     * 添加touch效果
     * 注意：通过setOnTouchListener方法实现，view已经实现了setOnTouchListener方法请注意
     *
     * 优先使用 PressUtil，深色按压效果
     * @param view
     */
    @Deprecated
    public static void addTouchEffect(final View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    view.setAlpha(0.7f);
                } else if (action == MotionEvent.ACTION_UP
                        || action == MotionEvent.ACTION_CANCEL
                        || action == MotionEvent.ACTION_OUTSIDE) {
                    view.setAlpha(1f);
                }
                return false;
            }
        });

    }

    public static void alphaEffectWithoutScale(final View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    view.setAlpha(0.7f);
                } else if (action == MotionEvent.ACTION_UP
                        || action == MotionEvent.ACTION_CANCEL
                        || action == MotionEvent.ACTION_OUTSIDE) {
                    view.setAlpha(1f);
                }
                return false;
            }
        });
    }

    public static void addLandLordTouchEffect(final View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    view.setScaleX(0.9f);
                    view.setScaleY(0.9f);
                } else if (action == MotionEvent.ACTION_UP
                        || action == MotionEvent.ACTION_CANCEL
                        || action == MotionEvent.ACTION_OUTSIDE) {
                    view.setScaleX(1f);
                    view.setScaleY(1f);
                }
                return false;
            }
        });
    }

    public static void addPressEffect(View touchView, final View changeView) {
        touchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    changeView.setAlpha(0.7f);
                } else if (action == MotionEvent.ACTION_UP
                        || action == MotionEvent.ACTION_CANCEL
                        || action == MotionEvent.ACTION_OUTSIDE) {
                    changeView.setAlpha(1f);
                }
                return false;
            }
        });

    }
}
