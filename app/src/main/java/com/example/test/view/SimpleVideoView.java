package com.example.test.view;

import android.content.Context;
import android.graphics.Outline;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleObserver;

import com.example.test.R;


/**
 * 用于播放视频的view，需要注意资源的释放
 * 支持生命周期的绑定，需要调用{@link androidx.lifecycle.Lifecycle#addObserver(LifecycleObserver)}
 * 若未绑定，则需要自己根据生命周期进行资源的回收
 * @author rejig
 * date 2020/09/21
 */

public class SimpleVideoView extends ConstraintLayout implements FullLifecycleObserver {
    private final Context mContext;
    public SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private boolean needReplay = false;
    private boolean isVideoPrepare = false;
    private boolean isSurfaceCreated = false;
    private boolean isLooping = false;
    private boolean needPlaying = false;
    public String currentPath = "";
    private int volume = 100;
    private Callback callback;

    public SimpleVideoView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }


    public SimpleVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public SimpleVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setConner(int conner){
        surfaceView.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), conner);
            }
        });
        surfaceView.setClipToOutline(true);
    }

    /**
     * 参数的设置需要在调用{@link SimpleVideoView#startPlay()}之前
     * @param volume 音量大小，0～100
     */
    public void setVolume(int volume) {
        this.volume = volume;
    }

    /**
     * 参数的设置需要在调用{@link SimpleVideoView#startPlay()}之前
     * @param looping 是否循环
     */
    public void setLooping(boolean looping) {
        isLooping = looping;
    }

    /**
     * 参数的设置需要在调用{@link SimpleVideoView#startPlay()}之前
     * @param needReplay 是否需要重新播放，如果为true会在onResume的时候重播，
     *                   否则app进入后台后会销毁资源。
     */
    public void setNeedReplay(boolean needReplay) {
        this.needReplay = needReplay;
    }

    public void init() {
        LayoutInflater.from(mContext).inflate(R.layout.launcher_video_view, this);
        surfaceView = findViewById(R.id.surfaceView);
        mediaPlayer = new MediaPlayer();
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                isSurfaceCreated = true;
                if (needPlaying){
                    startPlay();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                isSurfaceCreated = false;
            }
        });
    }

    /**
     * 设置播放路径，同时完成播放器的初始化。 //todo 目前多线程调用会产生bug
     * @param filePath 视频路径
     */
    public void setSource(String filePath) {
        if (filePath.equals(currentPath) && isVideoPrepare){
            if (needPlaying) {
                startPlay();
            }
            return;
        }
        isVideoPrepare = false;
        currentPath = filePath;
        try {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.release();
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置声音播放模式
            mediaPlayer.setOnPreparedListener(mp -> {
                isVideoPrepare = true;
                if (needPlaying){
                    startPlay();
                }
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e("AdvertVideoView", "setSource: " + e.getMessage());
        }
    }

    /**
     * 播放器的初始化在{@link SimpleVideoView#setSource(String)}中，
     * 需要先调用setSource才可以正常播放
     */
    public void startPlay() {
        if (mediaPlayer == null ||mediaPlayer.isPlaying()){
            return;
        }
        if (isVideoPrepare && isSurfaceCreated) {
            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.setLooping(isLooping);
            mediaPlayer.setDisplay(surfaceView.getHolder());
            mediaPlayer.start();
            if (callback != null) {
                callback.onStart();
            }
            mediaPlayer.setOnCompletionListener(mp -> {
                if (callback != null) callback.onCompletion();
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                if (callback != null) callback.onError();
                return false;
            });
            needPlaying = false;
        } else {
            needPlaying = true;
        }
    }

    /**
     * 调整播放时间，调用时机在{@link Callback#onStart()}之后
     * @param p 需要跳转到的时间点
     */
    public void setPlayingTime(int p) {
        if (mediaPlayer != null && mediaPlayer.isPlaying() ) {
            mediaPlayer.seekTo(p);
        }
    }

    /**
     * 暂停视频播放
     */
    public void pauseVideo() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    /**
     * 释放播放器资源
     */
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        isVideoPrepare = false;
        currentPath = "";
    }

    public void replay() {
        if (!needReplay) {
            return;
        }
        if (mediaPlayer == null) {
            return;
        }
        startPlay();
    }

    @Override
    public void onResume() {
        replay();
    }

    @Override
    public void onPause() {
        if (needReplay) {
            pauseVideo();
        } else {
            release();
        }
    }

    @Override
    public void onDestroy() {
        release();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        release();
    }

    public interface Callback{
        default void onStart(){}
        default void onCompletion(){}
        default void onError(){}
    }
}
