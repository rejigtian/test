package com.wepie.download;


import android.text.TextUtils;
import android.util.Log;

import java.io.File;

class FileEntity {

    private static final String TAG = "FileEntity";

    private String url;
    private String cacheDirPath;
    private String cacheFilePath;

    FileEntity(String url, String cacheDirPath, String cacheFilePath) {
        this.url = url;
        this.cacheDirPath = cacheDirPath;
        this.cacheFilePath = cacheFilePath;
    }

    public String getUrl() {
        return url;
    }

    public File tempFile() {
        if (!TextUtils.isEmpty(cacheFilePath)) {//指定储存的文件路径
            File cacheFile = cacheFile();
            return new File(cacheFile.getParent(), cacheFile.getName() + ".temp");
        }

        return new File(cachePath(), fileNameForUrl(true));
    }

    public File cacheFile() {
        if (!TextUtils.isEmpty(cacheFilePath)) {//指定储存的文件路径
            return new File(cacheFilePath);
        }

        return new File(cachePath(), fileNameForUrl(false));
    }

    private String fileNameForUrl(boolean isTemp) {
        return "file" + String.valueOf(url.hashCode()) + (isTemp ? ".temp" : ".cache");
    }

    private String cachePath() {
        return cacheDirPath;
    }

    public void tempRename() {
        File file = tempFile();
        File newFile = cacheFile();
        boolean rename = file.renameTo(newFile);
        Log.d(TAG, "tempTransferFile: " + rename + file.getAbsolutePath() + "-----" + newFile.getAbsolutePath());
    }
}
