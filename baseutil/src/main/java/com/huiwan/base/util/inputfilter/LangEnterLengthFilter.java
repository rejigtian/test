package com.huiwan.base.util.inputfilter;

import android.text.InputFilter;
import android.text.Spanned;

import com.huiwan.base.LibBaseUtil;
import com.huiwan.base.R;
import com.huiwan.base.util.ToastUtil;


/**
 * enter 占 20 字符
 * 英文占 1 个字符
 * 中文占 2 个字符
 */
public class LangEnterLengthFilter implements InputFilter {
    private final int max;

    public LangEnterLengthFilter(int max) {
        this.max = max;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        LibBaseUtil.logInfo("InputFilter","source: {}, start:{}, end:{}, dest:{}, dstart:{}, dend:{}", source, start, end, dest, dstart, dend);
        int cur = getCharSequenceLen(dest, dstart, dend);
        int keep = max - cur;
        if (keep <= 0) {
            if (end - start > 0) {
                ToastUtil.show(R.string.common_cannot_enter_more);
            }
            return "";
        } else {
            int sEnd = start;
            for (int i = start; i < end; i++) {
                char c = source.charAt(i);
                keep -= getCharLen(c);
                if (keep < 0) {
                    break;
                }
                sEnd++;
            }
            if (sEnd != end) {
                ToastUtil.show(R.string.common_cannot_enter_more);
            }
            return source.subSequence(start, sEnd);
        }
    }

    public static int getCharSequenceLen(CharSequence cs) {
        return getCharSequenceLen(cs, -1, -1);
    }

    public static int getCharSequenceLen(CharSequence cs, int filterStart, int filterEnd) {
        int len = 0;
        for (int i = 0; i < cs.length(); i++) {
            if (i >= filterStart && i < filterEnd) {
                continue;
            }
            char c = cs.charAt(i);
            len += getCharLen(c);
        }
        return len;
    }

    public static int getLenExceptSpace(CharSequence cs) {
        int len = 0;
        for (int i = 0; i < cs.length(); i++) {
            char c = cs.charAt(i);
            if (isSpace(c)) {
                continue;
            }
            len += getCharLen(c);
        }
        return len;
    }

    private static boolean isSpace(char c) {
        return c == ' ' || c == '\n' || c == '\u2005' || c == '\u3000';
    }

    private static int getCharLen(char c) {
        if (isEnter(c)) {
            return 20;
        } else if (isChinese(c)) {
            return 2;
        } else if (isACS(c)) {
            return 1;
        } else {
            return 1;
        }
    }


    public static boolean isEnter(char c) {
        return c == '\n';
    }

    public static boolean isACS(char c) {
        return c < 128;
    }

    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }
}
