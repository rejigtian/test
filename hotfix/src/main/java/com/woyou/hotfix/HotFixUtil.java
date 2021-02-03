package com.woyou.hotfix;

import android.app.Application;

import com.tencent.bugly.Bugly;

public class HotFixUtil {
    private static HotFixUtil instance;
    private Application application;

    public static HotFixUtil get(){
        if (instance == null){
            instance = new HotFixUtil();
        }
        return instance;
    }

    public void init(Application application){
        this.application = application;
        Bugly.init(application, "bf9eb26c6b", false);
    }
}
