package com.wepie.download;

import android.util.Log;

import com.huiwan.base.util.FileUtil;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * date 2020/7/30
 * email july.oloy@qq.com
 *
 * @author leoyuu
 */
class TaskInterceptor implements Runnable {

    private final LinkedBlockingDeque<Task> tasks = new LinkedBlockingDeque<>();
    private final NextTask nextTask;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private static final String TAG = TaskInterceptor.class.getSimpleName();

    TaskInterceptor(NextTask nextTask) {
        this.nextTask = nextTask;
    }

    void addTaskImmediate(Task task){
        if (!running.get()) {
            running.set(true);
            DownloadThreadUtil.runInCacheThread(this);
            Log.d(TAG, "new runner");
        }
        tasks.offerFirst(task);
    }

    void addTask(Task task) {
        if (!running.get()) {
            running.set(true);
            DownloadThreadUtil.runInCacheThread(this);
            Log.d(TAG, "new runner");
        }
        tasks.offer(task);
    }

    @Override
    public void run() {
        Log.d(TAG, "new runner run");
        while (true) {
            Task task;
            try {
                task = tasks.poll(1, TimeUnit.MINUTES);
                if (task == null) {
                    break;
                }
                final FileEntity fileEntity = new FileEntity(task.downloader.getUrl(),
                        task.downloader.getCacheDirPath(), task.downloader.getCacheFilePath());
                if (FileUtil.isValid(fileEntity.cacheFile())) {
                    Log.d(TAG, "check exist and return true");
                    invokeCallbackSuccess(task, fileEntity);
                } else {
                    Log.d(TAG, "check do next download");
                    nextTask.next(task);
                }
            } catch (InterruptedException e) {
                Log.d(TAG, "error when poll", e);
            }
        }
        running.set(false);
        Log.d(TAG, "runner shutdown");
    }

    private void invokeCallbackSuccess(final Task task, final FileEntity fileEntity) {
        DownloadThreadUtil.runInMainThread(new Runnable() {
            @Override
            public void run() {
                if (task.callback != null) {
                    task.callback.onSuccess(fileEntity.getUrl(), fileEntity.cacheFile().getAbsolutePath());
                }
            }
        });
    }

    interface NextTask {
        void next(Task task);
    }

    static class Task {
        final Downloader downloader;
        final DownloadCallback callback;

        Task(Downloader downloader, DownloadCallback callback) {
            this.downloader = downloader;
            this.callback = callback;
        }
    }

    public void clearTaskQueue() {
        tasks.clear();
    }
}
