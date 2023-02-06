package com.huiwan.base.util;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.huiwan.base.R;

/**
 * Created by geeksammao on 24/10/2017.
 * 变深按压效果
 */
public class PressUtil {
    /**
     * R' = R * mul.R / 0xff + add.R
     * G' = G * mul.G / 0xff + add.G
     * B' = B * mul.B / 0xff + add.B
     */
    public static LightingColorFilter filter = new LightingColorFilter(0xcccccc, 0x111111);

    public static void addPressEffect(final View view) {
        final Drawable drawable = getDrawable(view);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(drawable != null) {
                            drawable.setColorFilter(filter);
                        } else {
                            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.color_transparent15));
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if(drawable != null) {
                            drawable.clearColorFilter();
                        } else {
                            view.setBackground(null);
                        }
                        break;
                }
                return false;
            }
        });
    }

    public static void addPressEffectWithScale(final View... views) {
        for (View v: views) {
            addPressEffectWithScale(v);
        }
    }

    public static void addPressEffectWithScale(final View view) {
        final Drawable drawable = getDrawable(view);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(drawable != null) {
                            drawable.setColorFilter(filter);
                        } else {
                            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.color_transparent15));
                        }
                        view.setScaleX(0.95f);
                        view.setScaleY(0.95f);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if(drawable != null) {
                            drawable.clearColorFilter();
                        } else {
                            view.setBackground(null);
                        }
                        view.setScaleX(1f);
                        view.setScaleY(1f);
                        break;
                }
                return false;
            }
        });
    }

    public static void addPressEffect(final View touchView, final View changeView) {
        touchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final Drawable drawable = getDrawable(changeView);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(drawable != null) {
                            drawable.setColorFilter(filter);
                        } else {
                            changeView.setBackgroundColor(changeView.getContext().getResources().getColor(R.color.color_transparent15));
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if(drawable != null) {
                            drawable.clearColorFilter();
                        } else {
                            changeView.setBackground(null);
                        }
                        break;
                }
                return false;
            }
        });
    }

    private static Drawable getDrawable(View view) {
        Drawable drawable = view.getBackground();
        if(drawable == null && view instanceof ImageView) {
            drawable = ((ImageView)view).getDrawable();
        }
        return drawable;
    }

    public static void setEnable(ImageView imageView, boolean enable) {
        if (enable) {
            imageView.setColorFilter(null);
        } else {
            ColorMatrix saturation = new ColorMatrix();
            saturation.setSaturation(0.5f);
            ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(saturation);
            imageView.setColorFilter(colorFilter);
        }
    }
}