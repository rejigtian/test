package com.example.test.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.example.test.lifecycle.FullLifecycleObserver;
import com.example.test.lifecycle.FullLifecycleObserverAdapter;

public class LifecycleHandler extends Handler implements FullLifecycleObserver, Application.ActivityLifecycleCallbacks {


    private LifecycleOwner mLifecycleOwner;
    private String TAG="LifecycleHandler not LauncherVideoView";

    public LifecycleHandler(LifecycleOwner lifecycleOwner) {
        mLifecycleOwner = lifecycleOwner;
        addObserver();
    }

    public LifecycleHandler(LifecycleOwner lifecycleOwner, Callback callback) {
        super(callback);
        mLifecycleOwner = lifecycleOwner;
        addObserver();
    }

    public LifecycleHandler(LifecycleOwner lifecycleOwner, Looper looper) {
        super(looper);
        mLifecycleOwner = lifecycleOwner;
        addObserver();
    }

    public LifecycleHandler(LifecycleOwner lifecycleOwner, Looper looper, Callback callback) {
        super(looper, callback);
        mLifecycleOwner = lifecycleOwner;
        addObserver();
    }

    private void addObserver() {
        if (mLifecycleOwner != null) {
            mLifecycleOwner.getLifecycle().addObserver(new FullLifecycleObserverAdapter(mLifecycleOwner, this));
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
}


