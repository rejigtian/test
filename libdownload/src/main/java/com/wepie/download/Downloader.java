package com.wepie.download;

import android.text.TextUtils;
import android.util.Log;

import java.util.concurrent.Executor;

// Created by bigwen on 2018/12/17.
public class Downloader {

    private static final String TAG = "Downloader";
    private static NetworkCore networkCore = new NetworkCore();
    private static String defaultCachePath;
    private String url;
    private int retryTimes = 0;
    private String cacheDirPath;
    private boolean unpack = false;//解压
    private String cacheFilePath;//指定缓存文件目录
    private boolean inChildThread = true;
    private boolean shouldDownloadImmediate; // 是否立刻下载，而不排队等待别的下载任务
    private boolean multiCallback = false;//支持多个callback，不要在列表中调中，导致callback非常多
    private boolean singleThread;//在一个单线程下载

    public Downloader setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * 下载失败重试次数，不论重试几次，只回调一次
     *
     * @param retryTimes
     * @return
     */
    public Downloader setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
        return this;
    }

    /**
     * 指定缓存文件夹 临时文件 需要缓存的文件 (设置的全局缓存目录将失效)
     * 根据url获取hash值作为缓存文件名
     *
     * @param cacheDirPath
     * @return
     */
    public Downloader setCacheDirPath(String cacheDirPath) {
        this.cacheDirPath = cacheDirPath;
        return this;
    }

    /**
     * 是否在子线程下载，默认为true
     * @param inChildThread
     * @return
     */
    public Downloader setInChildThread(boolean inChildThread) {
        this.inChildThread = inChildThread;
        return this;
    }

    /**
     *  与setSingleThread互斥
     */
    public Downloader setShouldDownloadImmediate(boolean shouldDownloadImmediate) {
        this.shouldDownloadImmediate = shouldDownloadImmediate;
        return this;
    }

    /**
     * 直接指定缓存文件路径 (设置的缓存目录将失效)
     *
     * @param cacheFilePath
     * @return
     */
    public Downloader setCacheFilePath(String cacheFilePath) {
        this.cacheFilePath = cacheFilePath;
        return this;
    }

    /**
     * 下载完成同时解压
     * @param unpack
     * @return
     */
    public Downloader setUnpack(boolean unpack) {
        this.unpack = unpack;
        return this;
    }

    /**
     * 开始下载
     * 适当的时候调用 removeCallback 接口，避免泄漏
     * 多次下载，仅最后一个callback生效
     * @param callback 回调始终在主线程
     */
    public void download(final DownloadCallback callback) {
        if (TextUtils.isEmpty(url)) {
            if (callback != null) {
                DownloadThreadUtil.runInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) callback.onFail("url is empty");
                    }
                });
            }
            return;
        }
        if (shouldDownloadImmediate){
            interceptor.addTaskImmediate(new TaskInterceptor.Task(this, callback));
        }else {
            interceptor.addTask(new TaskInterceptor.Task(this, callback));
        }
    }

    private static TaskInterceptor interceptor = new TaskInterceptor(new TaskInterceptor.NextTask() {
        @Override
        public void next(TaskInterceptor.Task task) {
            networkCore.addDownloadTask(task.downloader, task.callback);
        }
    });

    public static Downloader newBuilder() {
        return new Downloader();
    }

    public static void setDefaultCachePath(String path) {
        defaultCachePath = path;
    }

    public static void removeCallback(String url) {
        networkCore.removeCallback(url);
    }

    public static boolean isDownloading(String url) {
        return networkCore.isDownloading(url);
    }

    public static void setDownloadExecutor(Executor executor) {
        DownloadThreadUtil.setDownloadExecutor(executor);
    }

    public static void clearTaskQueue() {
        interceptor.clearTaskQueue();
    }

    String getUrl() {
        return url;
    }

    int getRetryTimes() {
        return retryTimes;
    }

    String getCacheDirPath() {
        return TextUtils.isEmpty(cacheDirPath) ? defaultCachePath : cacheDirPath;
    }

    boolean isUnpack() {
        return unpack;
    }

    String getCacheFilePath() {
        return cacheFilePath;
    }

    boolean isInChildThread() {
        return inChildThread;
    }

    static void printLog(String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, msg);
        }
    }

    public boolean shouldDownloadImmediate() {
        return shouldDownloadImmediate;
    }

    public boolean isMultiCallback() {
        return multiCallback;
    }

    /**
     * 不要在列表渲染中调中，导致非常多callback
     */
    public Downloader setMultiCallback(boolean multiCallback) {
        this.multiCallback = multiCallback;
        return this;
    }

    /**
     *  与shouldDownloadImmediate互斥
     */
    public Downloader setSingleThread(boolean singleThread) {
        this.singleThread = singleThread;
        return this;
    }

    public boolean isSingleThread() {
        return singleThread;
    }
}
