package com.huiwan.base.util.inputfilter;

import android.text.InputFilter;
import android.text.Spanned;
/**
 * author dsc
 * 输入限制类，中文占2个字符，英文占1个字符，并设置最大字符数
 * 2020/10/12
 */
public class ACSIIInputFilter implements InputFilter {
    private final int textLength;

    public ACSIIInputFilter(int textLength) {
        this.textLength = textLength;
    }

    @Override
    public CharSequence filter(CharSequence src, int start, int end, Spanned dest, int dStart, int dEnd) {
        int dIndex = 0;
        int count = 0;

        while (count <= textLength && dIndex < dest.length()) {
            if (dStart < dEnd && dIndex >= dStart && dIndex <= dEnd) {
                break;
            }
            char c = dest.charAt(dIndex++);
            //这里是根据ACSII值进行判定的中英文，其中中文及中文符号的ACSII值都是大于128的
            if (c <= 128) {
                count = count + 1;
            } else {
                count = count + 2;
            }
        }

        if (count > textLength) {
            return dest.subSequence(0, dIndex - 1);
        }

        int sIndex = 0;
        while (count <= textLength && sIndex < src.length()) {
            char c = src.charAt(sIndex++);
            if (c < 128) {
                count = count + 1;
            } else {
                count = count + 2;
            }
        }

        if (count > textLength) {
            sIndex--;
            return src.subSequence(0, sIndex);
        } else if (count == textLength) {
            return src.subSequence(0, sIndex);
        }  else {
            return null;
        }
    }
}
