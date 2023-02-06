package com.huiwan.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huiwan.base.util.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class ActivityTaskManager {

    private static final ActivityTaskManager sInstance = new ActivityTaskManager();
    private int appCount = 0;
    private int checkAppNum = 0;
    public static boolean notCheck = false;


    private final Stack<Activity> activityStack = new Stack<>();
    private WeakReference<Activity> topActivityRef;
    private WeakReference<Activity> lastCreateActivityRef;

    private ActivityTaskManager() {
    }

    public static ActivityTaskManager getInstance() {
        return sInstance;
    }

    public static boolean isBackground() {
        if (getInstance().appCount > 0) {
            return false;
        }
        return true;
    }

    public static boolean isBackgroundNew() {
        return getInstance().checkAppNum <= 0;
    }

    //将当前Activity推入栈中
    void pushActivity(Activity activity) {
        activityStack.add(activity);
    }

    void popActivity(Activity activity) {
        activityStack.remove(activity);
    }

    //退出栈中所有Activity
    public void finishAllActivities() {
        while (!activityStack.empty()) {
            activityStack.pop().finish();
        }
    }

    @Nullable
    public Activity getLastCreateActivity() {
        if (lastCreateActivityRef == null) {
            return null;
        }
        return lastCreateActivityRef.get();
    }

    @Nullable
    public Activity getTopActivity() {
        if (activityStack.size() == 0) return null;
        if (topActivityRef != null) {
            Activity activity = topActivityRef.get();
            if (activity != null) {
                return activity;
            }
        }
        return activityStack.peek();
    }

    public int getResumeCount() {
        return appCount;
    }


    public boolean isStack(Class classz) {
        for (Activity activity : activityStack) {
            if (activity != null && activity.getClass().getName().equals(classz.getName())) {
                return true;
            }
        }
        return false;
    }

    public void finishAllWithoutTop() {
        if (activityStack.size() == 0) return;
        Activity topActivity = activityStack.pop();
        while (!activityStack.empty()) {
            activityStack.pop().finish();
        }
        activityStack.push(topActivity);
    }

    public void finishActivity(Class<?> classz) {
        for (Activity activity : activityStack) {
            if (activity != null && activity.getClass().getName().equals(classz.getName())) {
                activity.finish();
                break;
            }
        }
    }

    static void install(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                getInstance().lastCreateActivityRef = new WeakReference<>(activity);
                getInstance().pushActivity(activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                getInstance().appCount++;
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                getInstance().topActivityRef = new WeakReference<>(activity);
                getInstance().checkAppNum++;
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                if (activity.isFinishing()) {
                    getInstance().popActivity(activity);
                }
                getInstance().checkAppNum--;
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                getInstance().appCount--;
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                getInstance().popActivity(activity);
            }
        });
    }

    /**
     * 该判断支持多进程使用
     * @param var0
     * @return
     */
    public static boolean isForeground(Context var0) {
        List<ActivityManager.RunningAppProcessInfo> cachedInfos;
        ActivityManager var1 = (ActivityManager)var0.getSystemService(Context.ACTIVITY_SERVICE);
        cachedInfos = var1.getRunningAppProcesses();

        Iterator<ActivityManager.RunningAppProcessInfo> var6 = cachedInfos.iterator();

        ActivityManager.RunningAppProcessInfo processInfo;
        String processName;
        do {
            if (!var6.hasNext()) {
                return false;
            }

            int index;
            if ((index = (processName = (processInfo = (ActivityManager.RunningAppProcessInfo)var6.next()).processName).indexOf(":")) != -1) {
                processName = processName.substring(0, index);
            }
        } while(!processName.equals(var0.getPackageName()));

        int importance;
        return (importance = processInfo.importance) == 100 || importance == 200;
    }

    public static void checkHijack(Context context) {
        if(isBackgroundNew()) {
            if (!notCheck) {
                ToastUtil.show(LibBaseUtil.getAppName() + "已进入后台运行");
            }
        }
    }
}
