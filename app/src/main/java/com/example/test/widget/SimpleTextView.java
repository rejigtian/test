package com.example.test.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.test.R;


/**
 * 自定义用于展示简单文本的view，可以有效处理大量表情或文字导致的卡顿问题
 * @author rejig
 * date 2020/09/25
 */
public class SimpleTextView extends View {
    TextPaint paint = new TextPaint();
    CharSequence text = "";
    StaticLayout layout;
    int textSize;
    int linePadding;
    int paddingTop = 4;
    int paddingBottom = 4;
    int paddingLeft = 10;
    int paddingRight = 10;
    int gravity;
    int textColor ;
    int width;//SimpleTextView布局宽度
    int height;//SimpleTextView布局高度
    int layoutwidth;//staticLayout的实际宽度
    private Spannable mSpannable;
    private boolean needRefresh = false;
    private int highLightColor = 0x0033B5E5;
    private BackgroundColorSpan highLightSpan = new BackgroundColorSpan(highLightColor);
    private Layout.Alignment aligin = Layout.Alignment.ALIGN_NORMAL;

    public static final int ALIGN_NORMAL=0;
    public static final int ALIGN_OPPOSITE=1;
    public static final int ALIGN_CENTER=2;


    public SimpleTextView(Context context) {
        super(context);
        init(context,null);
    }

    private void init(Context context, AttributeSet attr) {
        if (attr != null) {
            TypedArray array = context.obtainStyledAttributes(attr, R.styleable.SimpleTextView);
            textSize = array.getDimensionPixelSize(R.styleable.SimpleTextView_android_textSize, dip2px(16));
            linePadding = array.getDimensionPixelSize(R.styleable.SimpleTextView_linePadding,dip2px(2));
            text=array.getText(R.styleable.SimpleTextView_android_text);
            textColor = array.getColor(R.styleable.SimpleTextView_android_textColor,0xffffffff);
            gravity = array.getInt(R.styleable.SimpleTextView_android_gravity,ALIGN_NORMAL);
            paddingTop = array.getDimensionPixelSize(R.styleable.SimpleTextView_android_paddingTop, 4);
            paddingBottom = array.getDimensionPixelSize(R.styleable.SimpleTextView_android_paddingBottom, 4);
            paddingLeft = array.getDimensionPixelSize(R.styleable.SimpleTextView_android_paddingLeft, 0);
            paddingRight = array.getDimensionPixelSize(R.styleable.SimpleTextView_android_paddingRight, 0);
            array.recycle();
        }
        paint.setTextSize(textSize);
        paint.setAntiAlias(true);
        paint.setColor(textColor);
        text = text == null ? "" : text;
        setGravity(gravity);
    }

    public void setText(CharSequence text){
        this.text = text == null ? "" : text;
        mSpannable = (text instanceof Spannable) ? (Spannable) text : null;
        refreshView(true);
    }

    /**
     * 设置view的padding，单位为dp
     * @param paddingTop 顶部距离
     * @param paddingBottom 底部距离
     * @param paddingLeft 左边距离
     * @param paddingRight 右边距离
     */
    public void setPadding(int paddingTop, int paddingBottom, int paddingLeft, int paddingRight){
        this.paddingTop = dip2px(paddingTop);
        this.paddingBottom = dip2px(paddingBottom);
        this.paddingLeft = dip2px(paddingLeft);
        this.paddingRight = dip2px(paddingRight);
        refreshView(true);
    }

    public SimpleTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public void setTextColor(int textColor) {
        paint.setColor(textColor);
        refreshView(true);
    }

    public void setTextSize(int textSize){
        paint.setTextSize(dip2px(textSize));
        refreshView(true);
    }

    public void setTextBold(boolean bold){
        paint.setFakeBoldText(bold);
        refreshView(true);
    }

    public void setGravity(int gravity){
        this.gravity = gravity;
        switch (gravity){
            case ALIGN_NORMAL:
                this.aligin = Layout.Alignment.ALIGN_NORMAL;
                break;
            case ALIGN_CENTER:
                this.aligin = Layout.Alignment.ALIGN_CENTER;
                break;
            case ALIGN_OPPOSITE:
                this.aligin = Layout.Alignment.ALIGN_OPPOSITE;
                break;
            default:
                this.aligin = Layout.Alignment.ALIGN_NORMAL;
                break;
        }
        refreshView(true);
    }



    public int getHighLightColor() {
        return highLightColor;
    }

    public void setHighLightColor(int highLightColor) {
        this.highLightColor = highLightColor;
        this.highLightSpan = new BackgroundColorSpan(highLightColor);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mSpannable ==null || layout == null) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();

        x -= paddingLeft;
        y -= paddingTop;

        x += getScrollX();
        y += getScrollY();
        int line = layout.getLineForVertical(y);
        int off = layout.getOffsetForHorizontal(line, x);
        ClickableSpan[] links = mSpannable.getSpans(off, off, ClickableSpan.class);
        if (links.length != 0) {
            ClickableSpan link = links[0];
            if (event.getAction() == MotionEvent.ACTION_UP) {
                link.onClick(this);
                removeHighLight();
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mSpannable.setSpan(highLightSpan, mSpannable.getSpanStart(link), mSpannable.getSpanEnd(link), Spanned.SPAN_INCLUSIVE_INCLUSIVE );
                refreshView(false);
            }else if (!isInViewZone(this,event.getRawX(),event.getRawY())){
                removeHighLight();
            }
            return true;
        }else {
            removeHighLight();
        }
        if (event.getAction() == MotionEvent.ACTION_UP ||event.getAction() == MotionEvent.ACTION_CANCEL){
            removeHighLight();
        }

        return super.onTouchEvent(event);
    }
    private void removeHighLight(){
        mSpannable.removeSpan(highLightSpan);
        refreshView(false);
    }

    /**
     * 刷新页面
     * @param isForce 是否需要强制重绘。在布局宽高变化或者文本内容变化时则不需要强制重绘。
     */
    private void refreshView(boolean isForce){
        this.needRefresh = isForce;
        requestLayout();
        invalidate();
    }

    private boolean isInViewZone(View view, float x, float y) {
        Rect mRect = new Rect();
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        mRect.left = location[0];
        mRect.top = location[1];
        mRect.right = mRect.left + width;
        mRect.bottom = mRect.top + height;
        return mRect.contains((int)x, (int)y);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        heightSize = heightSize + paddingTop + paddingBottom;

        if (widthMode == MeasureSpec.EXACTLY) {
            // Parent has told us how big to be. So be it.
            width = widthSize;
        } else {
            width = widthSize;
            if (widthMode == MeasureSpec.AT_MOST) {
                int desired = text.length() * textSize + paddingLeft + paddingRight;
                width = Math.min(desired, widthSize);
            }
        }
        layoutwidth = width - paddingLeft - paddingRight ;
        if (layout == null || layout.getWidth() != layoutwidth || layout.getText() != text || needRefresh) {
            layout = new StaticLayout(text, paint, layoutwidth, aligin, 1, linePadding, true);
            needRefresh = false;
        }
        if (layout.getLineCount()==1)
            width = (int) layout.getLineWidth(0) + paddingLeft + paddingRight;

        if (heightMode == MeasureSpec.EXACTLY) {
            // Parent has told us how big to be. So be it.
            height = heightSize;
        } else {
            int desired = layout.getHeight() + paddingTop + paddingBottom;
            height = desired;

            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(desired, heightSize);
            }
        }
        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (layout != null) {
            canvas.save();
            canvas.translate(paddingLeft,paddingTop);
            layout.draw(canvas);
            canvas.restore();
        }
    }
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public CharSequence getText() {
        return text;
    }
}
