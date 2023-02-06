package com.huiwan.base.util;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;

/**
 * Created by zhuguangwen on 15/4/8.
 * email 979343670@qq.com
 */
public class TextUtil {

    private static final char[] EMPTY_CHARS = new char[]{
            ' ', '\u2005', '\u3000'
    };

    public static int getLineCount(String text, int textPerLine){
        if(TextUtils.isEmpty(text)) return 0;
        int lineNum = 1;
        int currentLineFirstIndex = 0;
        for(int i = 0;i < text.length();i ++){
            if(text.charAt(i) == '\n' || i-currentLineFirstIndex >= textPerLine-1){
                currentLineFirstIndex = i+1;
                lineNum++;
            }
        }
        return lineNum;
    }

    public static boolean isEmpty(String text) {
        boolean hasChar =  !TextUtils.isEmpty(text);
        if (hasChar) {
            String check = text.trim();
            return TextUtils.isEmpty(check) ||
                    check.charAt(0) == '\u2005' ||
                    check.charAt(0) == '\u3000';
        } else {
            return true;
        }
    }

    public static void setMaxLine(SpannableStringBuilder ssb, String text, int textPerLine, int maxLineCount, String maxReplacement, Object maxLineSpan) {
        if(TextUtils.isEmpty(text)) {
            return;
        }
        int lineNum = 1;
        int currentLineFirstIndex = 0;
        for(int i = 0;i < text.length();i ++){
            char c = text.charAt(i);
            if(c == '\n' || i-currentLineFirstIndex >= textPerLine-1){
                lineNum++;
                if (lineNum > maxLineCount) {
                    if (i-currentLineFirstIndex >= textPerLine-6 && ssb.length() > 3) {
                        ssb.delete(ssb.length()-3, ssb.length());
                    }
                    ssb.append("...");
                    int start = ssb.length();
                    ssb.append(maxReplacement);
                    ssb.setSpan(maxLineSpan, start, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                } else {
                    ssb.append(c);
                }
                currentLineFirstIndex = i+1;
            } else {
                ssb.append(c);
            }
        }

    }

    public static int getEnterLength(CharSequence sequence) {
        int len = 0;
        for (int i = 0; i < sequence.length(); i++) {
            if (sequence.charAt(i) == '\n') {
                len += 20;
            } else {
                len++;
            }
        }
        return len;
    }

    public static boolean isEmptyChar(char tc) {
        for (char c:EMPTY_CHARS) {
            if (c == tc) {
                return true;
            }
        }
        return false;
    }

    public static void setTextColorGradient(TextView textView, @ColorRes int startColor, @ColorRes int endColor) {
        if (textView == null || textView.getContext() == null) {
            return;
        }
        Context context = textView.getContext();
        @ColorInt final int sc = context.getResources().getColor(startColor);
        @ColorInt final int ec = context.getResources().getColor(endColor);
        final float x = textView.getPaint().getTextSize() * textView.getText().length();
        LinearGradient gradient = new LinearGradient(0, 0, x, 0, sc, ec, Shader.TileMode.CLAMP);
        textView.getPaint().setShader(gradient);
        textView.invalidate();
    }

    public static void setTextColorGradient(TextView textView, int[] colors, float[] positions) {
        if (textView == null || textView.getContext() == null) {
            return;
        }

        String text = textView.getText().toString();
        // 方法1：getTextBounds
        Rect rect = new Rect();
        textView.getPaint().getTextBounds(text, 0, text.length(), rect);
        // 方法2：measureText
//        float measuredWidth = textView.getPaint().measureText(text);
        float textWidth = rect.width();
        LinearGradient linearGradient = new LinearGradient(0, 0, textWidth, 0,
                colors,
                positions,
                Shader.TileMode.REPEAT);
        textView.getPaint().setShader(linearGradient);
        textView.invalidate();
    }
}
