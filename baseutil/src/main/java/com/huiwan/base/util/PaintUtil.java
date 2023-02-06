package com.huiwan.base.util;

import android.graphics.Paint;

/**
 * Created by zhuguangwen on 15/1/14.
 */
public class PaintUtil {

    public static int getTextBaseLine(Paint textPaint, int textRectTop, int textRectBottom){
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        return textRectTop + (textRectBottom - textRectTop - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
    }

}
