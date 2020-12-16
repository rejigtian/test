package com.example.test.widget;

public class TaskItem implements Runnable {
    public String desc;
    Runnable realTask;

    public TaskItem(String desc, Runnable task) {
        this.desc = desc;
        this.realTask = task;
    }

    @Override
    public void run() {
        if (realTask != null) {
            realTask.run();
        }
    }
}
