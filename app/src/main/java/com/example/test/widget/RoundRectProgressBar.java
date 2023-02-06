package com.example.test.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;

import com.example.test.R;

/**
 * 自定义圆角矩形进度条view
 *
 * @author rejig
 */
public class RoundRectProgressBar extends View {
    private long endTime = 0;
    private long starTime = 0;
    private float duration = 0;

    /**
     * 画笔对象的引用
     */
    private final Paint mBorderPaint;
    private final Paint progressPaint;

    /**
     * 圆角环的颜色
     */
    private int starColor;

    /**
     * 进度的颜色
     */
    private int endColor;

    /**
     * 圆角矩形宽度
     */
    private int viewWidth;

    /**
     * 圆角矩形高度
     */
    private int viewHeight;

    private int barStartLocation = 0;

    /**
     * 进度条最大值
     */
    private float max = 1;

    /**
     * 进度条当前值
     */
    private float progress = 0f;

    private float barHeight;
    private float barWidth;
    LinearGradient lg;
    private int corner;
    private float strokeWidth;
    private int totalLength;
    private int circleLength;
    private int rectLength;
    private float widthSecPercent;//二分之一的宽度所占百分比
    private float heightSecPercent;//二分之一的高度所占百分比
    private float circleSecPercent;//四分之一的圆弧所占百分比
    private float widthSecLen;//需要真实绘制的二分之一的宽度
    private float heightSecLen;//需要真实绘制的二分之一的高度
    RectF rf = new RectF();

    public RoundRectProgressBar(Context context) {
        this(context, null);
    }

    public RoundRectProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundRectProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundRectProgressBar);
        //获取自定义属性和默认值
        starColor = mTypedArray.getColor(R.styleable.RoundRectProgressBar_barStartColor, Color.RED);
        endColor = mTypedArray.getColor(R.styleable.RoundRectProgressBar_barEndColor, Color.GREEN);
        barWidth = mTypedArray.getDimensionPixelSize(R.styleable.RoundRectProgressBar_barWidth, Color.BLUE);
        barHeight = mTypedArray.getDimensionPixelSize(R.styleable.RoundRectProgressBar_barHeight, 0);
        barStartLocation = mTypedArray.getInt(R.styleable.RoundRectProgressBar_barStartLocation, 0);
        //目前仅支持从顶部中间开始
        barStartLocation = 0;
        corner = mTypedArray.getDimensionPixelSize(R.styleable.RoundRectProgressBar_corner, 0);
        strokeWidth = mTypedArray.getDimensionPixelSize(R.styleable.RoundRectProgressBar_strokeWidth, 0);
        //获取画笔
        mBorderPaint = new Paint();
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(starColor);
        mBorderPaint.setStrokeWidth(strokeWidth);
        lg = new LinearGradient(0, barHeight / 2, barWidth, barHeight, starColor, endColor, Shader.TileMode.MIRROR);
        mBorderPaint.setShader(lg);
        progressPaint = new Paint();
        progressPaint.setStyle(Paint.Style.FILL);
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(0xff000000);
        progressPaint.setStrokeWidth(strokeWidth);
        final Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        progressPaint.setXfermode(xfermode);
        //回收TypedArray资源
        mTypedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int csl = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        if (endTime > 0 && starTime > 0) {
            progress = (System.currentTimeMillis() - starTime) / duration;
        }
        //进度方向
        here:
        if (barStartLocation == 0) {
            float padding = strokeWidth / 2;
            //水平，向右
            try {
                //RectF：绘制矩形，四个参数分别是left,top,right,bottom,类型是单精度浮点数
                rf.set(padding, padding, viewWidth - padding, viewHeight - padding);
                //绘制圆角矩形，背景色为画笔颜色
                canvas.drawRoundRect(rf, corner, corner, mBorderPaint);
                //1,从顶部中间开始倒计时，第一小段
                if (progress > widthSecPercent) {
                    canvas.drawLine(viewWidth / 2f, padding, viewWidth - corner, padding, progressPaint);
                } else if (progress < widthSecPercent) {
                    canvas.drawLine(viewWidth / 2f, padding, viewWidth / 2f + (progress / widthSecPercent * widthSecLen), padding, progressPaint);
                    break here;
                }
                //2,第一个半圆弧，右上角
                if (progress > (widthSecPercent + circleSecPercent)) {
                    canvas.drawArc(viewWidth - 2 * corner, 0, viewWidth, 2 * corner, 270, 90, true, progressPaint);
                } else {
                    canvas.drawArc(viewWidth - 2 * corner, 0, viewWidth, 2 * corner, 270, ((progress - widthSecPercent) / circleSecPercent * 90), true, progressPaint);
                    break here;
                }
                //3,右侧第一段
                if (progress > (widthSecPercent + circleSecPercent + heightSecPercent)) {
                    canvas.drawLine(rf.right, corner, rf.right, viewHeight / 2f, progressPaint);
                } else {
                    canvas.drawLine(rf.right, corner, rf.right, corner + ((progress - widthSecPercent - circleSecPercent) / heightSecPercent * heightSecLen), progressPaint);
                    break here;
                }
                //4,右侧第二段
                if (progress > (widthSecPercent + circleSecPercent + heightSecPercent * 2)) {
                    canvas.drawLine(rf.right, viewHeight / 2f, rf.right, viewHeight - corner, progressPaint);
                } else {
                    canvas.drawLine(rf.right, viewHeight / 2f, rf.right, viewHeight / 2f + ((progress - widthSecPercent - circleSecPercent - heightSecPercent) / heightSecPercent * heightSecLen), progressPaint);
                    break here;
                }
                //5，第二个半圆弧，右下角
                if (progress > (widthSecPercent + circleSecPercent * 2 + heightSecPercent * 2)) {
                    canvas.drawArc(viewWidth - 2 * corner, viewHeight - 2 * corner, viewWidth, viewHeight, 0, 90, true, progressPaint);
                } else {
                    canvas.drawArc(viewWidth - 2 * corner, viewHeight - 2 * corner, viewWidth, viewHeight, 0, ((progress - widthSecPercent - circleSecPercent - 2 * heightSecPercent) / circleSecPercent * 90), true, progressPaint);
                    break here;
                }
                //6,底部第一段
                if (progress > (widthSecPercent * 2 + circleSecPercent * 2 + heightSecPercent * 2)) {
                    canvas.drawLine(viewWidth - corner, rf.bottom, viewWidth / 2f, rf.bottom, progressPaint);
                } else {
                    canvas.drawLine(viewWidth - corner, rf.bottom, rf.right - corner - ((progress - widthSecPercent - 2 * circleSecPercent - 2 * heightSecPercent) / widthSecPercent * widthSecLen), rf.bottom, progressPaint);
                    break here;
                }
                //7,底部第二段
                if (progress > (widthSecPercent * 3 + circleSecPercent * 2 + heightSecPercent * 2)) {
                    canvas.drawLine(viewWidth / 2f, rf.bottom, corner, rf.bottom, progressPaint);
                } else {
                    canvas.drawLine(viewWidth / 2f, rf.bottom, viewWidth / 2f - ((progress - 2 * widthSecPercent - 2 * circleSecPercent - 2 * heightSecPercent) / widthSecPercent * widthSecLen), rf.bottom, progressPaint);
                    break here;
                }
                //8，第三个半圆弧，左下角
                if (progress > (widthSecPercent * 3 + circleSecPercent * 3 + heightSecPercent * 2)) {
                    canvas.drawArc(0, viewHeight - 2 * corner, 2 * corner, viewHeight, 90, 90, true, progressPaint);
                } else {
                    canvas.drawArc(0, viewHeight - 2 * corner, 2 * corner, viewHeight, 90, ((progress - 3 * widthSecPercent - 2 * circleSecPercent - 2 * heightSecPercent) / circleSecPercent * 90), true, progressPaint);
                    break here;
                }
                //9,左侧第一段
                if (progress > (widthSecPercent * 3 + circleSecPercent * 3 + heightSecPercent * 3)) {
                    canvas.drawLine(padding, viewHeight - corner, padding, viewHeight / 2f, progressPaint);
                } else {
                    canvas.drawLine(padding, viewHeight - corner, padding, viewHeight - corner - ((progress - 3 * widthSecPercent - 3 * circleSecPercent - 2 * heightSecPercent) / heightSecPercent * heightSecLen), progressPaint);
                    break here;
                }
                //10,左侧第二段
                if (progress > (widthSecPercent * 3 + circleSecPercent * 3 + heightSecPercent * 4)) {
                    canvas.drawLine(padding, viewHeight / 2f, padding, corner, progressPaint);
                } else {
                    canvas.drawLine(padding, viewHeight / 2f, padding, viewHeight / 2f - ((progress - 3 * widthSecPercent - 3 * circleSecPercent - 3 * heightSecPercent) / heightSecPercent * heightSecLen), progressPaint);
                    break here;
                }
                //11,第四个圆弧，左上角圆弧
                if (progress > (widthSecPercent * 3 + circleSecPercent * 4 + heightSecPercent * 4)) {
                    canvas.drawArc(0, 0, 2 * corner, 2 * corner, 180, 90, true, progressPaint);
                } else {
                    canvas.drawArc(0, 0, 2 * corner, 2 * corner, 180, ((progress - 3 * widthSecPercent - 3 * circleSecPercent - 4 * heightSecPercent) / circleSecPercent * 90), true, progressPaint);
                    break here;
                }
                //12,顶部左侧段
                if (progress >= 1f) {
                    canvas.drawLine(corner, padding, viewWidth / 2f, padding, progressPaint);
                } else {
                    canvas.drawLine(corner, padding, corner + ((progress - 3 * widthSecPercent - 4 * circleSecPercent - 4 * heightSecPercent) / widthSecPercent * widthSecLen), padding, progressPaint);
                    break here;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        canvas.restoreToCount(csl);
        if (endTime > 0 && starTime > 0){
            invalidate();
        }
    }

    /**
     * 指定自定义控件在屏幕上的大小,onMeasure方法的两个参数是由上一层控件
     * 传入的大小，而且是模式和尺寸混合在一起的数值，需要MeasureSpec.getMode(widthMeasureSpec)
     * 得到模式，MeasureSpec.getSize(widthMeasureSpec)得到尺寸
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        //MeasureSpec.EXACTLY，精确尺寸
        if (widthSpecMode == MeasureSpec.EXACTLY || widthSpecMode == MeasureSpec.AT_MOST) {
            viewWidth = widthSpecSize;
        } else {
            viewWidth = 0;
        }
        if (heightSpecMode == MeasureSpec.EXACTLY || heightSpecMode == MeasureSpec.AT_MOST) {
            viewHeight = heightSpecSize;
        } else {
            viewHeight = 0;
        }
        rectLength = (viewHeight - 2 * corner + viewWidth - 2 * corner) * 2;
        circleLength = (int) (2 * corner * Math.PI);
        totalLength = rectLength + circleLength;
        widthSecLen = (viewWidth - 2 * corner) / 2f;
        heightSecLen = (viewHeight - 2 * corner) / 2f;
        widthSecPercent = (viewWidth - 2 * corner) / (2f * totalLength);
        heightSecPercent = (viewHeight - 2 * corner) / (2f * totalLength);
        circleSecPercent = circleLength / (4f * totalLength);
        setMeasuredDimension(viewWidth, viewHeight);
    }


    /**
     * 设置进度
     *
     * @param progress 进度
     */
    public synchronized void setProgress(float progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("value can not be negative");
        }
        this.progress = Math.min(progress, max);
        invalidate();
    }

    /**
     * 设置最大值
     *
     * @param max 最大进度
     */
    public synchronized void setMax(float max) {
        if (max < 0) {
            throw new IllegalArgumentException("value can not be negative");
        }
        this.max = max;
    }

    public void setStarColor(int starColor) {
        this.starColor = starColor;
        invalidate();
    }

    public void setEndColor(int endColor) {
        this.endColor = endColor;
        invalidate();
    }

    public void setViewWidth(int viewWidth) {
        this.viewWidth = viewWidth;
        invalidate();
    }

    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
        invalidate();
    }

    public void star(int duration) {
        this.duration = duration;
        this.starTime = System.currentTimeMillis();
        this.endTime = this.starTime + duration;
        invalidate();
    }

    public void starByProgress(int duration, int leftTime) {
        this.duration = duration;
        this.starTime = System.currentTimeMillis() + leftTime -duration;
        this.endTime = this.starTime + leftTime;
        invalidate();
    }
}