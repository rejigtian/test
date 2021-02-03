package com.example.test.animview;

import android.util.Log;

import com.tencent.qgame.animplayer.AnimConfig;
import com.tencent.qgame.animplayer.inter.IAnimListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleAnimeListener implements IAnimListener {
    String TAG = "SimpleAnimeListener";
    @Override
    public void onFailed(int i, @Nullable String s) {
        Log.e(TAG, "onFailed: " + i + s);
    }

    @Override
    public void onVideoComplete() {

    }

    @Override
    public boolean onVideoConfigReady(@NotNull AnimConfig animConfig) {
        return true;
    }

    @Override
    public void onVideoDestroy() {

    }

    @Override
    public void onVideoRender(int i, @Nullable AnimConfig animConfig) {

    }

    @Override
    public void onVideoStart() {

    }
}
