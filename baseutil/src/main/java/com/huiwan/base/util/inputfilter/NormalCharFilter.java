package com.huiwan.base.util.inputfilter;

import android.text.InputFilter;
import android.text.Spanned;

import com.huiwan.base.util.ToastUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NormalCharFilter implements InputFilter {

    Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
            Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

    String speChat = "[`~!@#_$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）— +|{}【】‘；：”“’。，、？]";
    Pattern markPattern = Pattern.compile(speChat);
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Matcher matcher = markPattern.matcher(source.toString());
        Matcher emojiMatcher = emoji.matcher(source);
        if (matcher.find()) {
            ToastUtil.show("不支持输入符号");
            return "";
        }
        if (emojiMatcher.find()) {
            ToastUtil.show("不支持输入表情");
            return "";
        }
        if (source.equals(" ")) {
            ToastUtil.show("不支持输入空格");
            return "";
        }
        return null;
    }
}
