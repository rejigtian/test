package com.huiwan.base.util;

import android.graphics.drawable.Drawable;
import androidx.annotation.DimenRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huiwan.base.LibBaseUtil;

/**
 * Created by bigwen on 2019-08-24.
 */
public class ViewUtil {

    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public static void setLeftMargins(View v, int l) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, p.topMargin, p.rightMargin, p.bottomMargin);
            v.requestLayout();
        }
    }

    public static void setTopMargins(View v, int t) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(p.leftMargin, t, p.rightMargin, p.bottomMargin);
            v.requestLayout();
        }
    }


    public static void setRightMargins(View v, int r) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(p.leftMargin, p.topMargin, r, p.bottomMargin);
            v.requestLayout();
        }
    }

    public static void setBottomMargins(View v, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, b);
            v.requestLayout();
        }
    }

    public static int getTopMargins(View v) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            return p.topMargin;
        }
        return 0;
    }

    public static int getBottomMargins(View v) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            return p.bottomMargin;
        }
        return 0;
    }

    public static int getLeftMargins(View v) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            return p.leftMargin;
        }
        return 0;
    }

    public static int getRightMargins(View v) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            return p.rightMargin;
        }
        return 0;
    }

    public static void setPaddingLeft(View v, int left) {
        v.setPadding(left, v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
    }

    public static void setPaddingTop(View v, int top) {
        v.setPadding(v.getPaddingLeft(), top, v.getPaddingRight(), v.getPaddingBottom());
    }

    public static void setPaddingRight(View v, int right) {
        v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), right, v.getPaddingBottom());
    }

    public static void setPaddingBottom(View v, int bottom) {
        v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), bottom);
    }

    public static void clearAllTextDrawable(TextView textView) {
        textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    public static void setTextLeftDrawable(TextView textView, Drawable drawable) {
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    public static void setTextRightDrawable(TextView textView, Drawable drawable) {
        textView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
    }

    public static void setTextTopDrawable(TextView textView, Drawable drawable) {
        textView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
    }

    public static void setTextBottomDrawable(TextView textView, Drawable drawable) {
        textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
    }

    public static void setTextEmptyDrawable(TextView textView, Drawable drawable) {
        textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    public static void setViewHeight(View view, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
    }

    public static void setViewWidth(View view, int width) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = width;
        view.setLayoutParams(layoutParams);
    }

    public static int getDimen(@DimenRes int dimenRes) {
        return (int) (LibBaseUtil.getApplication().getResources().getDimension(dimenRes));
    }

    public static void removeFromParent(View view) {
        if (view.getParent() instanceof ViewGroup) {
            view.clearAnimation();
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    public static void addView(ViewGroup viewGroup, View childView) {
        if (viewGroup == null) return;
        if (childView == null) return;
        if (childView.getParent() != null) {
            ViewGroup parentView = (ViewGroup) childView.getParent();
            if (parentView != viewGroup) {
                parentView.removeView(childView);
                viewGroup.addView(childView, 0);
            } else {
                //已经添加
            }
        } else {
            viewGroup.addView(childView, 0);
        }
    }
}