package com.woyou.hotfix;

import android.app.Application;

import com.tencent.bugly.beta.Beta;

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
    }

    public static void checkUpgrade(){
        Beta.checkUpgrade();
    }

    public static void cleanPatch(){
        Beta.cleanTinkerPatch(true);
    }
}
