package com.huiwan.base.util.inputfilter;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * author dsc
 * 输入限制类，换行符占20字符，并设置最大字符数
 * 2020/10/12
 */

public class EnterLengthFilter  implements InputFilter {
    private final int max;

    public EnterLengthFilter(int max) {
        this.max = max;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                               int dstart, int dend) {
        int count = 0;
        int dCount = 0;
        for (int i = 0; i < dest.length(); i++) {
            if (i >= dstart && i < dend) {
                if (dest.charAt(i) == '\n') {
                    dCount += 20;
                } else {
                    dCount++;
                }
            }
            if (dest.charAt(i) == '\n') {
                count += 20;
            } else {
                count++;
            }
        }
        int sCount = start;
        for (int i = start; i < source.length(); i++) {
            char c = source.charAt(i);
            if (Character.isHighSurrogate(c)) {
                break;
            }
            if (c == '\n') {
                sCount += 20;
            } else {
                sCount++;
            }
        }
        int keep = max - (count - dCount);
        if (keep <= 0) {
            return "";
        } else if (keep >= sCount) {
            return null;
        } else {
            int index = start;
            while (keep >= 0 && index < source.length()) {
                char c = source.charAt(index++);
                if (Character.isHighSurrogate(c)) {
                    break;
                }
                if (c == '\n') {
                    keep -= 20;
                } else {
                    keep -= 1;
                }
            }
            if (start >= index - 1) {
                return "";
            } else {
                return source.subSequence(start, index -1);
            }
        }
    }
}
