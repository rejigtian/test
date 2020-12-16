package com.example.test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class Empty extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Service", "OnCreate");
    }

    @Override
    public void onDestroy() {
        Log.e("Service", "onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("Service", "onUnbind");
        return super.onUnbind(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Service", "onBind");
        return null;
    }
}
