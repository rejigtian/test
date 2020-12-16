package com.example.test;

import android.text.InputFilter;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;

/**
 * Created by bigwen on 2020/11/6.
 */
public class CapsLetterFilter implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        for (char c: source.subSequence(start,end).toString().toUpperCase().toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                stringBuilder.append(c);
            }
        }
        return stringBuilder;
    }
}
