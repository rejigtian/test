package com.huiwan.base.util;

import android.app.Dialog;

import com.huiwan.base.interfaces.SingleCallback;

public class DialogDismissCallback<T> implements SingleCallback<T> {
    private final SingleCallback<T> callback;
    private final Dialog dialog;

    public DialogDismissCallback(SingleCallback<T> callback, Dialog dialog) {
        this.callback = callback;
        this.dialog = dialog;
    }

    @Override
    public void onCall(T data) {
        dialog.dismiss();
        if (callback != null) callback.onCall(data);
    }

    @Override
    public void onErr(int code, String msg) {
        dialog.dismiss();
        if (callback != null) callback.onErr(code, msg);
    }
}
