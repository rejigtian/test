package com.example.test.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleObserver;

/**
 * 用于播放视频的view，需要注意资源的释放
 * 使用TextureView实现，用于列表视频的播放
 * 支持生命周期的绑定，需要调用{@link androidx.lifecycle.Lifecycle#addObserver(LifecycleObserver)}
 * 若未绑定，则需要自己根据生命周期进行资源的回收
 * 关注性能可以使用{@link SimpleVideoView}
 * @author rejig
 * date 2021/07/01
 */
public class TextureVideoView extends ConstraintLayout implements FullLifecycleObserver {
    private final Context mContext;
    private TextureView textureView;
    private SurfaceTexture surfaceTexture;
    private MediaPlayer mediaPlayer;
    private boolean needReplay = false;
    private boolean isVideoPrepare = false;
    private boolean isSurfaceCreated = false;
    private boolean isLooping = false;
    private boolean needPlaying = false;
    private String currentPath = "";
    private int scaleType;
    private int volume = 100;
    private Callback callback;

    public static final int NORMAL = 0;
    public static final int TOP = 1;
    public static final int CENTER_CROP = 2;
    public static final int BOTTOM = 3;

    public TextureVideoView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }


    public TextureVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public TextureVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setConner(int conner) {
        textureView.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), conner);
            }
        });
        textureView.setClipToOutline(true);
    }

    /**
     * 参数的设置需要在调用{@link TextureVideoView#startPlay()}之前
     *
     *
     */
    public void setScaleType(int scaleType) {
        this.scaleType = scaleType;
    }

    private void updateTextureViewSize(int mScaleType, int mVideoWidth, int mVideoHeight) {
        float viewWidth = getWidth();
        float viewHeight = getHeight();

        float scaleX = viewWidth * 1.0f / mVideoWidth;
        float scaleY = viewHeight * 1.0f / mVideoHeight;
        float maxScale = Math.max(scaleX, scaleY);

        // Calculate pivot points, in our case crop from center
        int pivotPointX;
        int pivotPointY;

        switch (mScaleType) {
            case TOP:
                pivotPointX = 0;
                pivotPointY = 0;
                break;
            case BOTTOM:
                pivotPointX = (int) (viewWidth);
                pivotPointY = (int) (viewHeight);
                break;
            case CENTER_CROP:
            default:
                pivotPointX = (int) (viewWidth / 2);
                pivotPointY = (int) (viewHeight / 2);
                break;
        }

        Matrix matrix = new Matrix();
        matrix.setScale(maxScale / scaleX , maxScale / scaleY, pivotPointX, pivotPointY);

        textureView.setTransform(matrix);
    }

    /**
     * 参数的设置需要在调用{@link TextureVideoView#startPlay()}之前
     *
     * @param volume 音量大小，0～100
     */
    public void setVolume(int volume) {
        this.volume = volume;
    }

    /**
     * 参数的设置需要在调用{@link TextureVideoView#startPlay()}之前
     *
     * @param looping 是否循环
     */
    public void setLooping(boolean looping) {
        isLooping = looping;
    }

    /**
     * 参数的设置需要在调用{@link TextureVideoView#startPlay()}之前
     *
     * @param needReplay 是否需要重新播放，如果为true会在onResume的时候重播，
     *                   否则app进入后台后会销毁资源。
     */
    public void setNeedReplay(boolean needReplay) {
        this.needReplay = needReplay;
    }

    private void init() {
        textureView = new TextureView(getContext());
        addView(textureView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setBackgroundColor(Color.TRANSPARENT);
        mediaPlayer = new MediaPlayer();
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                isSurfaceCreated = true;
                surfaceTexture = surface;
                if (needPlaying) {
                    startPlay();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
                isSurfaceCreated = true;
                surfaceTexture = surface;
                if (needPlaying) {
                    startPlay();
                }
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                isSurfaceCreated = false;
                surfaceTexture = null;
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
                isSurfaceCreated = true;
                surfaceTexture = surface;
                if (needPlaying) {
                    startPlay();
                }
            }
        });
    }

    /**
     * 设置播放路径，同时完成播放器的初始化。
     *
     * @param filePath 视频路径
     */
    public void setSource(String filePath) {
        if (filePath.equals(currentPath) && isVideoPrepare) {
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
            if (scaleType != NORMAL) {
                mediaPlayer.setOnVideoSizeChangedListener(
                        (mp, width, height) -> updateTextureViewSize(scaleType, width, height)
                );
            }
            mediaPlayer.setOnPreparedListener(mp -> {
                isVideoPrepare = true;
                if (needPlaying) {
                    startPlay();
                }
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {

        }
    }

    /**
     * 播放器的初始化在{@link TextureVideoView#setSource(String)}中，
     * 需要先调用setSource才可以正常播放
     */
    public void startPlay() {
        if (mediaPlayer == null || mediaPlayer.isPlaying()) {
            return;
        }
        if (isVideoPrepare && isSurfaceCreated) {
            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.setLooping(isLooping);
            mediaPlayer.setSurface(new Surface(surfaceTexture));
            mediaPlayer.start();
            if (callback != null) {
                callback.onStart();
            }

            needPlaying = false;
        } else {
            needPlaying = true;
        }
    }

    /**
     * 调整播放时间，调用时机在{@link Callback#onStart()}之后
     *
     * @param p 需要跳转到的时间点
     */
    public void setPlayingTime(int p) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
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

    public interface Callback {
        void onStart();
    }
}
