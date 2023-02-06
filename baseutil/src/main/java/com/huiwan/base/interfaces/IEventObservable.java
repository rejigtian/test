package com.huiwan.base.interfaces;

import androidx.annotation.NonNull;

public interface IEventObservable {
    void registerEventObserver(IEventObserver observer);
    void notifyEventObserver(@NonNull String eventName, Object eventInfo);
}
