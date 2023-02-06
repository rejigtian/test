package com.huiwan.base.util.executors;

import java.util.ArrayDeque;
import java.util.concurrent.Executor;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

/**
 * date 2020/6/1
 * email july.oloy@qq.com
 *
 * @author leoyuu
 */
public class SafeSerialExecutor implements Executor {
    private final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
    private Runnable mActive;

    public synchronized void execute(final Runnable r) {
        mTasks.offer(new Runnable() {
            public void run() {
                try {
                    r.run();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    scheduleNext();
                }
            }
        });
        if (mActive == null) {
            scheduleNext();
        }
    }

    private synchronized void scheduleNext() {
        if ((mActive = mTasks.poll()) != null) {
            THREAD_POOL_EXECUTOR.execute(mActive);
        }
    }
}
