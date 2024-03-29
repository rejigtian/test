package com.example.test.view;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;

import com.example.test.R;
import com.example.test.lifecycle.FullLifecycleObserver;
import com.example.test.lifecycle.FullLifecycleObserverAdapter;


public class CustomeVideoView extends ConstraintLayout implements Application.ActivityLifecycleCallbacks, FullLifecycleObserver {
    private final Context mContext;
    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private onViewStatusChange onViewStatusChange;
    private LifecycleOwner mLifecycleOwner;
    private final String TAG="LauncherVideoView";

    public CustomeVideoView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public void setLifecycleOwner(LifecycleOwner mLifecycleOwner) {
        this.mLifecycleOwner = mLifecycleOwner;
        addObserver();

    }

    public CustomeVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public CustomeVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void addObserver() {
        if (mLifecycleOwner != null) {
            mLifecycleOwner.getLifecycle().addObserver(new FullLifecycleObserverAdapter(mLifecycleOwner, this) {
            });
        }
    }

    public void setOnViewStatusChange(CustomeVideoView.onViewStatusChange onViewStatusChange) {
        this.onViewStatusChange = onViewStatusChange;
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.launcher_video_view, this);
        surfaceView = findViewById(R.id.surfaceView);
        mediaPlayer = new MediaPlayer();
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mediaPlayer.setDisplay(surfaceView.getHolder());
                if (onViewStatusChange!=null)
                    onViewStatusChange.onCreate();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    public void setSource(String filePath) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置声音播放模式
            mediaPlayer.setOnPreparedListener(mp -> {
                if (onViewStatusChange!=null)
                    onViewStatusChange.onPrepared();
            });
            mediaPlayer.prepare();
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }

    public void startPlay(boolean isLooping){
        mediaPlayer.setLooping(isLooping);
        mediaPlayer.start();
        onViewStatusChange.onPlaying();
    }


    public int getPlayingTime(){
        if (mediaPlayer!=null)
            return mediaPlayer.getCurrentPosition();
        else return 0;
    }

    public void setPlayingTime(int p){
        mediaPlayer.seekTo(p);
    }

    public void pauseVideo(){
        mediaPlayer.pause();
    }

    public void setVolume(int volume){
        mediaPlayer.setVolume(volume,volume);
    }

    public void release(){
        if (mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            onViewStatusChange.onDestroy();
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onActivityCreated: " );
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        Log.e(TAG, "onActivityStarted: " );
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        Log.e(TAG, "onActivityResumed: " );
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        Log.e(TAG, "onActivityPaused: " );
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        Log.e(TAG, "onActivityStopped: " );
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        Log.e(TAG, "onActivitySaveInstanceState: " );
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        Log.e(TAG, "onActivityDestroyed: " );
    }

    @Override
    public void onCreate(LifecycleOwner owner) {
        Log.e(TAG, "onCreate: " );
    }

    @Override
    public void onStart(LifecycleOwner owner) {
        Log.e(TAG, "onStart: " );
    }

    @Override
    public void onResume(LifecycleOwner owner) {
        Log.e(TAG, "onResume: " );
    }

    @Override
    public void onPause(LifecycleOwner owner) {
        Log.e(TAG, "onPause: " );
    }

    @Override
    public void onStop(LifecycleOwner owner) {
        Log.e(TAG, "onStop: " );
    }

    @Override
    public void onDestroy(LifecycleOwner owner) {
        Log.e(TAG, "onDestroy: " );
    }

    public interface onViewStatusChange{
        void onCreate();
        void onPrepared();
        void onPlaying();
        void onDestroy();
    }
}
