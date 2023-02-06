package com.huiwan.base.activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityTaskBuilderHolder {
    private static final ActivityTaskBuilderHolder instance = new ActivityTaskBuilderHolder();

    private final List<ActivityTask.Builder> builderList = new ArrayList<>();

    ActivityTaskBuilderHolder() {}
    public static ActivityTaskBuilderHolder get() {
        return instance;
    }

    public void register(ActivityTask.Builder builder) {
        builderList.add(builder);
    }

    public void unregister(ActivityTask.Builder builder) {
        builderList.remove(builder);
    }

    public List<ActivityTask.Builder> getBuilderList() {
        return builderList;
    }
}
