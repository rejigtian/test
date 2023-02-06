package com.wepie.download;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

// Created by bigwen on 2018/12/17.
class DownloadThreadUtil {

    private static Handler handler = new Handler(Looper.getMainLooper());
    private static Executor fixedDownloadExecutor;
    private static Executor cacheDownloadExecutor = Executors.newCachedThreadPool(new NamedThreadFactory("-downloader-cache"));
    private static ExecutorService singleExecutor = Executors.newSingleThreadExecutor(new NamedThreadFactory("-downloader-single"));
    private static ReentrantLock singleExecutorLock = new ReentrantLock();

    static void runInChildThread(Runnable runnable) {
        if (fixedDownloadExecutor == null) fixedDownloadExecutor = Executors.newFixedThreadPool(4, new NamedThreadFactory("-downloader"));
        fixedDownloadExecutor.execute(runnable);
    }

    static void runInCacheThread(Runnable runnable) {
        cacheDownloadExecutor.execute(runnable);
    }

    static void setDownloadExecutor(Executor executor) {
        fixedDownloadExecutor = executor;
    }

    static void runInMainThread(Runnable runnable) {
        handler.post(runnable);
    }

    static class NamedThreadFactory implements ThreadFactory {
        private final ThreadFactory mDefaultThreadFactory;
        private final String mBaseName;
        private final AtomicInteger mCount = new AtomicInteger(0);

        NamedThreadFactory(final String baseName) {
            mDefaultThreadFactory = Executors.defaultThreadFactory();
            mBaseName = baseName;
        }

        @Override
        public Thread newThread(final Runnable runnable) {
            final Thread thread = mDefaultThreadFactory.newThread(runnable);
            thread.setName(mBaseName + "-" + mCount.getAndIncrement());
            return thread;
        }
    }

    static void runInSingleThread(Runnable runnable) {
        singleExecutorLock.lock();
        try {//这里try一下，避免出现异常lock无法释放
            singleExecutor.execute(runnable);
        } finally {
            singleExecutorLock.unlock();
        }
    }

    static void shutdownSingle() {
        singleExecutorLock.lock();
        try {
            ExecutorService lastSingleExecutor = singleExecutor;
            singleExecutor = Executors.newSingleThreadExecutor(new NamedThreadFactory("-downloader-single"));
            lastSingleExecutor.shutdownNow();
        } finally {
            singleExecutorLock.unlock();
        }
    }

    static void clearDownloadCacheQueue() {
        clearThreadPool(fixedDownloadExecutor);//这个execurot可能由外部传入，所以不使用shutdown的方法结束
        clearThreadPool(cacheDownloadExecutor);
        shutdownSingle();
    }

    private static void clearThreadPool(Executor executor) {
        if (executor instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor poolExecutor = (ThreadPoolExecutor) executor;
            BlockingQueue<Runnable> queue = poolExecutor.getQueue();
            if (queue != null) {
                queue.clear();
            }
        }
    }
}
