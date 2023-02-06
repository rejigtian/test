package com.huiwan.base.str;

public enum Language {
    en("en"),
    zh("zh-Hans"),
    zhTw("zh-TW");

    public final String value;

    public boolean isChinese() {
        return this == zh || this == zhTw;
    }

    Language(String value) {
        this.value = value;
    }
}
