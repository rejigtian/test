package com.huiwan.base.interfaces;

/**
 * date 2020/7/31
 * email july.oloy@qq.com
 *
 * @author leoyuu
 */
public interface IAsyncCallback<T, R> {
    void onCall(T t, SingleCallback<R> callback);
    default void onCancel() {}
}
