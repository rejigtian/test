package com.huiwan.base.interfaces;

import androidx.annotation.NonNull;

public interface IEventObserver {
    @NonNull String eventName();
    void onEvent(Object event);
}
