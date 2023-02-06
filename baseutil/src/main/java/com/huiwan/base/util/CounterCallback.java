package com.huiwan.base.util;

import android.os.Handler;
import android.os.Looper;

import com.huiwan.base.interfaces.SingleCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CounterCallback<T> implements SingleCallback<T> {
    public final int total;
    private final SingleCallback<T> callback;
    private final List<String> failedReason = Collections.synchronizedList(new ArrayList<>());
    private final List<T> successItem = Collections.synchronizedList(new ArrayList<>());

    public CounterCallback(int total, SingleCallback<T> callback) {
        this.total = total;
        this.callback = callback;
    }

    @Override
    synchronized public void onCall(T data) {
        successItem.add(data);
        checkFinished();
    }

    @Override
    synchronized public void onErr(int code, String msg) {
        failedReason.add(msg);
        checkFinished();
    }

    private void checkFinished() {
        int curCount = successItem.size() + failedReason.size();
        if (curCount == total && callback != null) {
            new Handler(Looper.getMainLooper()).post(()->{
                if (failedReason.isEmpty()) {
                    callback.onCall(successItem.get(0));
                } else {
                    callback.onErr(500, failedReason.get(0));
                }
            });
        }
    }

    public List<String> getFailedReason() {
        return failedReason;
    }

    public List<T> getSuccessItem() {
        return successItem;
    }
}
