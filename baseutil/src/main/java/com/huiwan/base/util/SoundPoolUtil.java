package com.huiwan.base.util;

import android.media.AudioManager;
import android.media.SoundPool;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RawRes;

import com.huiwan.base.LibBaseUtil;

import java.util.HashMap;

/**
 * Created by three on 15/10/26.
 */
public class SoundPoolUtil {

    private static SoundPoolUtil instance = new SoundPoolUtil();
    private SoundPool mSoundPool;
    private HashMap<String, Integer> mSourceMapExtra = new HashMap<String, Integer>();
    private static final String EXTRA_SRC_KEY = "extra_src";

    private SoundPoolUtil() {
        mSoundPool = new SoundPool(1000, AudioManager.STREAM_MUSIC, 10);
    }

    public static SoundPoolUtil getInstance() {
        if (instance == null) {
            instance = new SoundPoolUtil();
        }
        return instance;
    }

    public int playRaw(@RawRes final int rawId) {
        final String key = EXTRA_SRC_KEY + rawId;
        if(mSourceMapExtra.containsKey(key)) {
            int streamId = mSourceMapExtra.get(key);
            mSoundPool.play(streamId, 1, 1, 1, 0, 1);
            return streamId;
        }

        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mSourceMapExtra.put(key, sampleId);
                mSoundPool.play(mSourceMapExtra.get(key), 1, 1, 1, 0, 1);
            }
        });
        int steamId = mSoundPool.load(LibBaseUtil.getApplication(), rawId, 0);
        return steamId;
    }

    public void playAndLoadLocalFile(final String path) {
        if(TextUtils.isEmpty(path)) return;

        final String key = EXTRA_SRC_KEY + path;
        if(mSourceMapExtra.containsKey(key)) {
            mSoundPool.play(mSourceMapExtra.get(key), 1, 1, 1, 0, 1);
            return;
        }

        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mSourceMapExtra.put(key, sampleId);
                mSoundPool.play(mSourceMapExtra.get(key), 1, 1, 1, 0, 1);
            }
        });
        mSoundPool.load(path, 0);

    }

    public void cancelSoundStream(int streamId){
        try {
            if (mSoundPool != null){
                mSoundPool.stop(streamId);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
