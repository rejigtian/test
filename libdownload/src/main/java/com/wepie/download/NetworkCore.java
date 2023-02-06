package com.wepie.download;

import android.os.Build;

import android.os.Process;
import android.util.Log;
import com.huiwan.base.util.FileUtil;
import com.huiwan.platform.ThreadUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

class NetworkCore {

    private ConcurrentHashMap<String, FileEntity> downloadMap = new ConcurrentHashMap<>();//正在下载的任务，避免重复下载
    private ConcurrentHashMap<String, CopyOnWriteArrayList<DownloadCallback>> callbackMap = new ConcurrentHashMap<>();

    public void addDownloadTask(Downloader builder, DownloadCallback callback) {
        if (builder.isInChildThread()) {
            executeAsync(builder, callback);
        } else {
            executeSync(builder, callback);
        }
    }

    public void removeCallback(String url) {
        callbackMap.remove(url);
    }

    public boolean isDownloading(String url) {
        return downloadMap.containsKey(url);
    }

    private void executeAsync(final Downloader builder, final DownloadCallback callback) {
        if (builder.shouldDownloadImmediate()) {
            DownloadThreadUtil.runInCacheThread(new Runnable() {
                @Override
                public void run() {
                    ThreadUtil.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    executeSync(builder, callback);
                }
            });
        } else if (builder.isSingleThread()) {
            DownloadThreadUtil.runInSingleThread(new Runnable() {
                @Override
                public void run() {
                    ThreadUtil.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    executeSync(builder, callback);
                }
            });
        } else {
            DownloadThreadUtil.runInChildThread(new Runnable() {
                @Override
                public void run() {
                    ThreadUtil.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    executeSync(builder, callback);
                }
            });
        }
    }

    private void executeSync(Downloader builder, DownloadCallback callback) {
        String url = builder.getUrl();
        String cacheDirPath = builder.getCacheDirPath();

        final FileEntity fileEntity = new FileEntity(url, cacheDirPath, builder.getCacheFilePath());

        if (callback != null) {
            if (callbackMap.containsKey(url)) {
                CopyOnWriteArrayList<DownloadCallback> callbackList = callbackMap.get(url);
                if (callbackList == null) callbackList = new CopyOnWriteArrayList<>();
                if (builder.isMultiCallback()) {
                    if (!callbackList.contains(callback)) {
                        callbackList.add(callback);
                    }
                } else {
                    callbackList.clear();
                    callbackList.add(callback);
                }
            } else {
                CopyOnWriteArrayList<DownloadCallback> callbackList = new CopyOnWriteArrayList<>();
                callbackList.add(callback);
                callbackMap.put(url, callbackList);
            }
        }

        if (FileUtil.isValid(fileEntity.cacheFile())) {//有缓存
            invokeCallbackSuccess(fileEntity);
            return;
        }

        if (isDownloading(fileEntity.getUrl())) {//下载中
            return;
        }

        downloadMap.put(url, fileEntity);
        downloadSync(fileEntity, builder);
    }

    private boolean downloadSync(FileEntity fileEntity, Downloader builder) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(fileEntity.getUrl()).openConnection();

            connection.setConnectTimeout(10000);
            connection.setReadTimeout(60000);

            connection.setRequestMethod("GET");
            connection.connect();

            //异常处理
            if (connection.getErrorStream() != null || connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                BufferedReader r = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder error = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    error.append(line).append('\n');
                }
                downloadFail(fileEntity);
                invokeCallbackFail(fileEntity, builder, "Unable to fetch " + fileEntity.getUrl() + ". Failed with " + connection.getResponseCode() + "\n" + error);
                return false;
            }

            boolean isZip = "application/zip".equals(connection.getContentType());
            InputStream stream = connection.getInputStream();

            try {
                File temp = fileEntity.tempFile();
                FileUtil.createFile(temp);
                OutputStream output = new FileOutputStream(temp);
                try {
                    byte[] buffer = new byte[1024];
                    int read;

                    long totalLength;
                    if (Build.VERSION_CODES.N <= Build.VERSION.SDK_INT) {
                        totalLength = connection.getContentLengthLong();
                    } else {
                        totalLength = connection.getContentLength();
                    }
                    long currentLength = 0;
                    int currentPercent = 0;

                    while ((read = stream.read(buffer)) != -1) {
                        output.write(buffer, 0, read);
                        currentLength+=read;
                        if (totalLength != 0) {
                            int percent = (int)(currentLength * 100L / totalLength);
                            if (currentPercent != percent) {
                                currentPercent = percent;
                                invokeUpdatePercent(fileEntity, currentPercent);
                            }
                        }
                    }
                    output.flush();
                } finally {
                    output.close();
                }
            } finally {
                stream.close();
            }
            downloadSuccess(fileEntity, isZip, builder);
            invokeCallbackSuccess(fileEntity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            downloadFail(fileEntity);
            invokeCallbackFail(fileEntity, builder, "url = " + fileEntity.getUrl()
                    + "\ntempPath = " + fileEntity.tempFile().getAbsolutePath()
                    + "\ncachePath = " + fileEntity.cacheFile().getAbsolutePath()
                    + "\nerror : " + e.toString());
            return false;
        }
    }

    private void downloadSuccess(FileEntity fileEntity, boolean isZip, Downloader builder) throws IOException {
        if (isZip && builder.isUnpack()) {//解压到目标目录
            Downloader.printLog("unzip");
            FileUtil.unpackSync(new FileInputStream(fileEntity.tempFile()), fileEntity.cacheFile().getAbsolutePath());
        } else {//重命名到目标文件
            Downloader.printLog("tempRename");
            fileEntity.tempRename();
        }
        FileUtil.safeDeleteFile(fileEntity.tempFile());
        downloadMap.remove(fileEntity.getUrl());
    }

    private void downloadFail(FileEntity fileEntity) {
        FileUtil.safeDeleteFile(fileEntity.tempFile());
        downloadMap.remove(fileEntity.getUrl());
    }

    private void invokeCallbackSuccess(final FileEntity fileEntity) {
        DownloadThreadUtil.runInMainThread(new Runnable() {
            @Override
            public void run() {
                CopyOnWriteArrayList<DownloadCallback> callbackList = callbackMap.get(fileEntity.getUrl());
                if (callbackList != null) {
                    for (DownloadCallback callback : callbackList) {
                        if (callback != null) {
                            callback.onSuccess(fileEntity.getUrl(), fileEntity.cacheFile().getAbsolutePath());
                        }
                    }
                }
                callbackMap.remove(fileEntity.getUrl());
            }
        });
    }

    private void invokeCallbackFail(final FileEntity fileEntity, final Downloader downloader, final String msg) {
        DownloadThreadUtil.runInMainThread(new Runnable() {
            @Override
            public void run() {
                if (downloader.getRetryTimes() > 0) {//重试
                    downloader.setRetryTimes(downloader.getRetryTimes() - 1);
                    addDownloadTask(downloader, null);
                    return;
                }

                CopyOnWriteArrayList<DownloadCallback> callbackList = callbackMap.get(fileEntity.getUrl());
                if (callbackList != null) {
                    for (DownloadCallback callback : callbackList) {
                        if (callback != null) {
                            callback.onFail(msg);
                        }
                    }
                }
                callbackMap.remove(fileEntity.getUrl());
            }
        });
    }

    private void invokeUpdatePercent(final FileEntity fileEntity, final int percent) {
        CopyOnWriteArrayList<DownloadCallback> callbackList = callbackMap.get(fileEntity.getUrl());
        if (callbackList != null) {
            for (final DownloadCallback callback : callbackList) {
                if (callback == null) return;//放在外面减少一个主线程消息数
                DownloadThreadUtil.runInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onPercent(percent);
                    }
                });
            }
        }
    }
}
