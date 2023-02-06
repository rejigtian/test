package com.huiwan.base.util.inputfilter;

import android.text.InputFilter;
import android.text.Spanned;

import com.huiwan.base.util.ToastUtil;

public class WPLengthFilter implements InputFilter {
    private final int mMax;

    public WPLengthFilter(int max) {
        mMax = max;
    }

    public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                               int dstart, int dend) {
        int keep = mMax - (dest.length() - (dend - dstart));
        if (keep <= 0) {
            ToastUtil.show("最多只能输入" + mMax + "个字符");
            return "";
        } else if (keep >= end - start) {
            return null; // keep original
        } else {
            keep += start;
            ToastUtil.show("最多只能输入" + mMax + "个字符");
            if (Character.isHighSurrogate(source.charAt(keep - 1))) {
                --keep;
                if (keep == start) {
                    return "";
                }
            }
            return source.subSequence(start, keep);
        }
    }

    /**
     * @return the maximum length enforced by this input filter
     */
    public int getMax() {
        return mMax;
    }
}
