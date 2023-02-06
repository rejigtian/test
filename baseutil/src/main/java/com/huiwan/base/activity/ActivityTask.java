package com.huiwan.base.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public interface ActivityTask {

    default void onNewIntent(AppCompatActivity activity, Intent intent) {}

    default void onCreate(AppCompatActivity activity, Bundle savedInstanceState){}

    default void onStart(AppCompatActivity activity){}

    default void onResume(AppCompatActivity activity){}

    default void onPause(AppCompatActivity activity){}

    default void onRestoreInstanceState(AppCompatActivity activity, Bundle savedInstanceState){}

    default void onStop(AppCompatActivity activity){}

    default void onActivityResult(AppCompatActivity activity, int requestCode, int resultCode, Intent data){}

    default void onDestroy(AppCompatActivity activity){}

    interface Builder {
        ActivityTask build(Activity activity);
    }
}
