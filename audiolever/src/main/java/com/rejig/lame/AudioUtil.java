package com.rejig.lame;

import android.util.Log;

import java.io.File;

public class AudioUtil {
    private static AudioUtil instance = null;
    private Callback callback = null;

    public static AudioUtil getInstance() {
        if (instance == null){
            instance = new AudioUtil();
        }
        return instance;
    }

    /**
     * 是否压缩中的标识
     */
    private volatile boolean isCompress = false;

    static {
        System.loadLibrary("audio-lever");
    }

    public AudioUtil setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    /**
     * java层调用方法--wav转换mp3
     */
    public void wavToMp3(String wav, String mp3) throws Exception {
        wavToMp3(wav, mp3, false);
    }

    /**
     * java层调用方法--wav转换mp3
     */
    public void wavToMp3(String wav, String mp3, boolean deleteSource) throws Exception {
        if (isCompress) {
            throw new Exception("正在压缩...");
        }
        isCompress = true;
        if (!new File(wav).exists()) {
            throw new Exception("源文件不存在");
        }
        File targetFile = new File(mp3);
        if (!targetFile.exists()) {
            if (!targetFile.createNewFile()) {
                throw new Exception("目标文件不存在");
            }
        }
        convertToMp3(wav, mp3);
        if (deleteSource) {
            new File(wav).delete();
        }
        isCompress = false;
    }

    public static native String getLameVersion();

    /**
     * wav转换成mp3的本地方法
     *
     * @param wav
     * @param mp3
     */
    private native void convertToMp3(String wav, String mp3);

    /**
     * 设置进度条的进度，提供给C语言调用
     *
     * @param progress
     */
    public void setConvertProgress(long progress, long total, String path) {
        Log.d(this.getClass().getSimpleName(), "回调的进度:" + progress + "----总进度:" + total + "----path:" + path);
        if (callback != null){
            callback.onProgress(progress, total);
        }
        if (callback != null && progress >= total){
            callback.onFinish();
        }
    }

    public interface Callback{
        default void onProgress(long cur, long total){};
        default void onFinish(){}
    }
}
