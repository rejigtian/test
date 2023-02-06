package com.huiwan.base.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ActivityObserver {
    private final AppCompatActivity activity;
    private final List<ActivityTask> activityTaskList = new ArrayList<>(ActivityTaskBuilderHolder.get().getBuilderList().size());

    public ActivityObserver(AppCompatActivity activity) {
        this.activity = activity;
        for (ActivityTask.Builder builder : ActivityTaskBuilderHolder.get().getBuilderList()) {
            activityTaskList.add(builder.build(activity));
        }
    }


    public void onCreate(Bundle savedInstanceState) {
        for (ActivityTask task : activityTaskList) {
            task.onCreate(activity, savedInstanceState);
        }
    }


    public void onStart() {
        for (ActivityTask task : activityTaskList) {
            task.onStart(activity);
        }
    }

    public void onResume() {
        for (ActivityTask task : activityTaskList) {
            task.onResume(activity);
        }
    }

    @Nullable
    public <T extends ActivityTask> T getTask(Class<T> c) {
        for (ActivityTask task: activityTaskList) {
            if (task.getClass().isAssignableFrom(c)) {
                return c.cast(task);
            }
        }
        return null;
    }

    public void onPause() {
        for (ActivityTask task : activityTaskList) {
            task.onPause(activity);
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        for (ActivityTask task : activityTaskList) {
            task.onRestoreInstanceState(activity, savedInstanceState);
        }
    }

    public void onStop() {
        for (ActivityTask task : activityTaskList) {
            task.onStop(activity);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (ActivityTask task : activityTaskList) {
            task.onActivityResult(activity, requestCode, resultCode, data);
        }
    }

    public void onDestroy() {
        for (ActivityTask task : activityTaskList) {
            task.onDestroy(activity);
        }
    }

    public void onNewIntent(Intent intent) {
        for (ActivityTask task : activityTaskList) {
            task.onNewIntent(activity, intent);
        }
    }
}
