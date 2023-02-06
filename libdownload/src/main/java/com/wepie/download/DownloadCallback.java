package com.wepie.download;

// Created by bigwen on 2018/12/17.

/**
 * {@link SimpleDownloadCallback} is suggested
 */
public interface DownloadCallback {
    void onSuccess(String url, String path);
    void onFail(String msg);
    void onPercent(int percent);
}
