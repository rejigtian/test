package com.huiwan.base.lifecycle;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.huiwan.base.BuildConfig;

/**
 * 这里模仿代理模式使用 @OnLifecycleEvent 注解，在 activity 生命周期变化的时候会回调相应的方法，再在里面回调 observer 的 onCreate，onStart 等方法。
 * 不是跟随acuity整个生命周期的情况下需要remove，防止内存泄露
 * @author rejig
 * date 2021-01-20
 */
public class FullLifecycleObserverAdapter implements LifecycleObserver {
    private final FullLifecycleObserver mObserver;
    private static final String TAG = "FullLifecycleObserver";

    public FullLifecycleObserverAdapter(FullLifecycleObserver observer) {
        mObserver = observer;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        logI("onCreate: ");
        mObserver.onCreate();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        logI("onStart: ");
        mObserver.onStart();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        logI("onResume: ");
        mObserver.onResume();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        logI("onPause: ");
        mObserver.onPause();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        logI("onStop: ");
        mObserver.onStop();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        logI("onDestroy: ");
        mObserver.onDestroy();
    }

    private void logI(String msg){
        if (BuildConfig.DEBUG){
            Log.i(TAG, msg);
        }
    }

}
