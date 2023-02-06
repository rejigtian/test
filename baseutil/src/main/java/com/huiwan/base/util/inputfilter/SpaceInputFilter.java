package com.huiwan.base.util.inputfilter;

import android.text.InputFilter;
import android.text.Spanned;

import com.huiwan.base.util.TextUtil;

/**
 * author dsc
 * 输入限制类，去除所有空格
 * 2020/10/12
 */
public class SpaceInputFilter  implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        boolean hasEmptyChar = false;
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char c = source.charAt(i);
            if (c < ' ' || TextUtil.isEmptyChar(c)) {
                hasEmptyChar = true;
                break;
            }
        }
        if (hasEmptyChar) {
            StringBuilder sb = new StringBuilder(source);
            int index = 0;
            while (index < sb.length()) {
                char c = sb.charAt(index);
                if (c < ' ' || TextUtil.isEmptyChar(c)) {
                    sb.deleteCharAt(index);
                } else {
                    index++;
                }
            }
            return sb;
        }
        return null;
    }
}
