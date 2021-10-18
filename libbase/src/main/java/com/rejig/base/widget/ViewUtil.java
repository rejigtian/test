package com.rejig.base.widget;

import android.view.View;

public class ViewUtil {
    /**
     * (x,y)是否在view的区域内
     *
     */
    public static boolean isPointInView(View view, float x, float y) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1] - ScreenUtil.getStatusBarHeight(view.getContext());
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();

        return y <= top && y >= bottom && x >= left && x <= right;
    }
}
