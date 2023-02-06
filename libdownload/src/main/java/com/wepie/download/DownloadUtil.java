package com.wepie.download;

import android.util.Log;
import com.huiwan.base.util.ToastUtil;
import com.huiwan.platform.HWNamedThreadFactory;
import com.huiwan.base.util.log.TimeLogger;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DownloadUtil {
    private static final int MAXIMUM_POOL_SIZE = 4;

    private static ThreadPoolExecutor downloadExecutor = new ThreadPoolExecutor(MAXIMUM_POOL_SIZE, MAXIMUM_POOL_SIZE,1L,
            TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(8192), new HWNamedThreadFactory("download-util"), new DownloadRejectHandler());

    static {
        downloadExecutor.allowCoreThreadTimeOut(true);
    }

    /**
     * 指定一个默认缓存的路径
     */
    public static void initDownloader(String dirPath) {
        Downloader.setDefaultCachePath(dirPath);
        Downloader.setDownloadExecutor(downloadExecutor);
    }

    public static void removeCallback(String url) {
        Downloader.removeCallback(url);
    }

    public static boolean isDownloading(String url) {
        return Downloader.isDownloading(url);
    }

    public static void downloadFile(final String url, com.wepie.download.DownloadCallback callback) {
        Downloader.newBuilder()
                .setUrl(url)
                .download(callback);
    }

    public static void downloadFile(final String url, boolean immediate, com.wepie.download.DownloadCallback callback) {
        Downloader.newBuilder()
                .setUrl(url)
                .setShouldDownloadImmediate(immediate)
                .download(callback);
    }

    public static void downloadFile(final String url, boolean immediate, String cacheDir, com.wepie.download.DownloadCallback callback) {
        Downloader.newBuilder()
                .setUrl(url)
                .setShouldDownloadImmediate(immediate)
                .setCacheFilePath(cacheDir)
                .download(callback);
    }

    public static void downloadFileWithDir(final String url, String cacheDir, com.wepie.download.DownloadCallback callback) {
        Downloader.newBuilder()
                .setUrl(url)
                .setCacheDirPath(cacheDir)
                .download(callback);
    }

    public static void downloadFile(final String url, String cacheFilePath, com.wepie.download.DownloadCallback callback) {
        Downloader.newBuilder()
                .setUrl(url)
                .setCacheFilePath(cacheFilePath)
                .download(callback);
    }

    public static void downloadFileWithRetry(final String url, String cacheFilePath, int retryTimes, com.wepie.download.DownloadCallback callback) {
        Downloader.newBuilder()
                .setUrl(url)
                .setCacheFilePath(cacheFilePath)
                .setRetryTimes(retryTimes)
                .download(callback);
    }

    public static void downloadFileWithRetry(final String url, String cacheFilePath, int retryTimes, boolean immediateDownload, com.wepie.download.DownloadCallback callback) {
        Downloader.newBuilder()
                .setUrl(url)
                .setCacheFilePath(cacheFilePath)
                .setRetryTimes(retryTimes)
                .setShouldDownloadImmediate(immediateDownload)
                .download(callback);
    }

    /**
     * @param multiCallback 开启后，同一个地方使用同一个callback，避免callback过多（例如：列表中渲染中自动下载）
     */
    public static void downloadFileWithRetry(final String url, String cacheFilePath, int retryTimes, boolean immediateDownload, boolean multiCallback, com.wepie.download.DownloadCallback callback) {
        Downloader.newBuilder()
                .setUrl(url)
                .setCacheFilePath(cacheFilePath)
                .setRetryTimes(retryTimes)
                .setShouldDownloadImmediate(immediateDownload)
                .setMultiCallback(multiCallback)
                .download(callback);
    }

    public static void downloadConfig(final String url, String filePath) {
        Downloader.newBuilder()
                .setSingleThread(true)
                .setUrl(url)
                .setCacheFilePath(filePath)
                .setRetryTimes(3)
                .download(new SimpleDownloadCallback());
    }

    static class DownloadRejectHandler implements RejectedExecutionHandler {
        private final AtomicInteger ignoreCount = new AtomicInteger(0);
        private AtomicBoolean showToast = new AtomicBoolean(true);
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            TimeLogger.err("download task queue full, ignore task: " + r + ", ignore count: " + ignoreCount.incrementAndGet());
            if (showToast.get()) {
                showToast.set(false);
                ToastUtil.show("下载队列大小不足，需重新设计容量");
            }
        }
    }

    //清理下载队列
    public static void clearDownloadCacheQueue() {
        Downloader.clearTaskQueue();
        DownloadThreadUtil.clearDownloadCacheQueue();
    }
}
