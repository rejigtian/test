package com.huiwan.base.interfaces;

/**
 * date 2020/12/29
 * email july.oloy@qq.com
 *
 * @author leoyuu
 */
public interface DoubleCallback<M, T> {
    void onCall(M m, T data);
    default void onErr(int code, String msg){}
}
