package com.example.test.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.test.R;
import com.example.test.gift.GiftInterpolator;
import com.example.test.util.ScreenUtil;

import java.util.Random;


/**
 * 公式：y = -g * (t - 根号(h/g)) * (t - 根号(h/g)) + h
 * 烟花颜色值：蓝：#63CCF5
 红：#F97171
 黄：#EEAF46
 绿：#58DA9B
 紫：#B080DE

 烟花尺寸：宽8dp，有圆角，长44dp
 * Created by three on 15/9/12.
 */
public class GiftPlayView extends RelativeLayout {
    private final Context mContext;
    private final int[] shapes = new int[]{
            R.drawable.gift_play_shape_0, R.drawable.gift_play_shape_1, R.drawable.gift_play_shape_2,
            R.drawable.gift_play_shape_3, R.drawable.gift_play_shape_4,
    };

    public GiftPlayView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public GiftPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        sh = ScreenUtil.getScreenHeight(getContext());
        lineMaxH = ScreenUtil.dip2px(getContext(),36);
        lineWidth = ScreenUtil.dip2px(getContext(),8);
    }

    private int sh;
    private int lineMaxH;
    private int lineWidth;
    public void playGiftAnim() {

        final float g = sh * 400f / 568;
        final float maxH = sh * GiftInterpolator.maxhRatio;
        final float terminalH = sh * GiftInterpolator.maxhRatio;
        final float upTimeRatio=GiftInterpolator.getTimeRatio(true);
        final float downTimeRatio=GiftInterpolator.getTimeRatio(false);
        final int leftMargin = ScreenUtil.getScreenWidth(getContext())/2;

        final ImageView imageView = new ImageView(mContext);
        Drawable drawable= ContextCompat.getDrawable(mContext,shapes[new Random().nextInt(5)]);
        imageView.setBackground(drawable);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(lineWidth, lineMaxH);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.bottomMargin = 0;
        params.leftMargin = leftMargin;
        addView(imageView, params);
        final float animCount = 100;
        ValueAnimator lineAnim = ValueAnimator.ofFloat(0, animCount);

        lineAnim.addUpdateListener(valueAnimator -> {
            float value = (float) valueAnimator.getAnimatedValue();
            float fraction = value * 1.0f / animCount;
            float lineh = (0.2f+0.8f*(1 - fraction) ) * lineMaxH;
            if (drawable != null) {
                drawable.setBounds(0,0,lineWidth, (int) lineh);
            }
            if(fraction > upTimeRatio-downTimeRatio) imageView.setAlpha( 1f - (fraction-upTimeRatio+downTimeRatio) / (4*downTimeRatio) );
            imageView.setTranslationY(-maxH*fraction);
        });
        lineAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                removeView(imageView);
                startAnim2( maxH, terminalH, g);
            }
        });
        lineAnim.setInterpolator(new GiftInterpolator(true));
        lineAnim.setDuration((int)GiftInterpolator.getDuration(g,sh)).start();
    }

    private void startAnim2(final float maxH, final float startH, final float g) {

        final int leftMargin = ScreenUtil.getScreenWidth(getContext())/2;

        final int gw = ScreenUtil.dip2px(getContext(),150);
        final int gh = (int) (gw * 205f / 241);

        Random r = new Random();
        int ro = r.nextInt(17) - 8;

        final RelativeLayout giftView=new RelativeLayout(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(gw, gh);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.bottomMargin = (int)startH - gh/2;
        params.leftMargin = leftMargin - gw/2;
        final ImageView imageView = new ImageView(mContext);
        RelativeLayout.LayoutParams paramv = new RelativeLayout.LayoutParams(gw, gh);
        paramv.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        paramv.bottomMargin = 0;
        paramv.leftMargin = 0;
        giftView.addView(imageView, paramv);
        final TextView textView = new TextView(mContext);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setTextColor(0xff333333);
        String num = "X 1";
        textView.setText(num);
        textView.setRotation(ro);
        RelativeLayout.LayoutParams paramt = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        paramt.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        paramt.bottomMargin = ScreenUtil.dip2px(getContext(),10);
        paramt.leftMargin = ScreenUtil.dip2px(getContext(),100);
        giftView.addView(textView,paramt);
        addView(giftView, params);
        imageView.setImageResource(R.drawable.rain_red_packet);
        imageView.setRotation(ro);

        final float animCount = 100;
        ValueAnimator lineAnim = ValueAnimator.ofFloat(0, animCount);
        lineAnim.addUpdateListener(valueAnimator -> {
            float value = (float) valueAnimator.getAnimatedValue();
            giftView.setTranslationY((maxH)*(value/animCount));
        });
        lineAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                removeView(giftView);
            }
        });
        lineAnim.setInterpolator(new GiftInterpolator(false));
        lineAnim.setDuration((int) GiftInterpolator.getDuration(g,sh)).start();
    }



}
