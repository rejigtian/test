package com.example.test.animview;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import androidx.arch.core.util.Function;

import com.tencent.qgame.animplayer.inter.IFetchResource;
import com.tencent.qgame.animplayer.mix.Resource;

import org.jetbrains.annotations.NotNull;


import java.util.List;

public class SimpleFetchResource implements IFetchResource {

    @Override
    public void fetchText(@NotNull Resource resource, kotlin.jvm.functions.@NotNull Function1<? super String, kotlin.Unit> function1) {

    }

    @Override
    public void releaseResource(@NotNull List<Resource> list) {

    }

    @Override
    public void fetchImage(@NotNull Resource resource, kotlin.jvm.functions.@NotNull Function1<? super Bitmap, kotlin.Unit> function) {
    }
}
