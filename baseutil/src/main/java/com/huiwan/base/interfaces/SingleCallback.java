package com.huiwan.base.interfaces;

/**
 * date 2020/10/24
 * email july.oloy@qq.com
 *
 * @author leoyuu
 */
public interface SingleCallback<T> {
    void onCall(T data);
    default void onErr(int code, String msg){}
}
