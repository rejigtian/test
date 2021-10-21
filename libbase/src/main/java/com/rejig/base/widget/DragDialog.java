package com.rejig.base.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;


/**
 * 支持使用顶部控件拖动的dialog
 * @author rejig
 * date 2021-10-20
 */
public abstract class DragDialog extends ConstraintLayout {
    /**
     * 动画时间
     */
    private final static int ANIM_DURATION = 300;
    private View controlView;
    private View rootLay;
    private float startY;
    private long startTime;
    private float limitSpeed = 100f;

    public DragDialog(@NonNull Context context) {
        super(context);
    }

    /**
     * 想要实现拖动功能必须调用该方法设置控件
     * @param controlView 控制拖动的view
     * @param rootLay 被控制的视图
     */
    public void setControlView(View controlView, View rootLay){
        this.controlView = controlView;
        this.rootLay = rootLay;
        setListener();
    }

    /**
     * 设置滑动速度大于多少，即关闭页面。
     * 默认为100。人手速度一般在10以内。因此默认不会滑动过快关闭
     * @param limitSpeed 最大速度
     */
    public void setLimitSpeed(float limitSpeed) {
        this.limitSpeed = limitSpeed;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListener() {
        controlView.setOnTouchListener((v, event) -> {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    startY = event.getRawY();
                    startTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float y = event.getRawY() - startY;
                    if (y<0) y =0;
                    requestMoveLay(y);
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    float lastY = event.getRawY() - startY;
                    if (lastY<0) lastY =0;
                    float speed = lastY/(System.currentTimeMillis() - startTime);
                    requestMoveLay(lastY);
                    judgePosition(lastY, speed);
                    break;
            }
            return true;
        });
    }

    private void requestMoveLay(float y) {
        rootLay.setTranslationY(y);
    }

    private void judgePosition(float lastY, float speed) {
        if (lastY < rootLay.getMeasuredHeight()/3f && speed < limitSpeed){
            moveLayWithAnim(-lastY,false);
        } else {
            moveLayWithAnim(rootLay.getMeasuredHeight()-lastY, true);
        }
    }

    private void moveLayWithAnim(float y, boolean needClose) {
        rootLay.animate().setDuration(ANIM_DURATION).translationYBy(y).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (needClose){
                    onClose();
                }
            }
        });
    }

    /**
     * 拖动关闭页面的时候会触发此回调
     */
    public abstract void onClose();

}
