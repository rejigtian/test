package com.huiwan.talkingdata;

import android.app.Application;
import android.content.Context;

import com.tendcloud.appcpa.TalkingDataAppCpa;

/**
 * Created by geeksammao on 16/01/2018.
 */

public class TalkingDataManager {

    public static final String APP_ID = BuildConfig.TALKING_DATA_ID;


    public static void onLogin(int uid) {
        TalkingDataAppCpa.onLogin(String.valueOf(uid));
    }

    public static void onRegister(int uid) {
        TalkingDataAppCpa.onRegister(String.valueOf(uid));
    }


    /**
     * 因为要加上deviceid，所以初始化的过程不放在Application中
     * 下面Activity分别check初始化，避免遗漏
     * StartActivity
     * LoginActivity
     * MainActivity
     * 并去重
     */
    public static void init(Application application) {
        TalkingDataAppCpa.init(application, APP_ID, "test_app");
    }

    public static String getTdId(Context context) {
        return TalkingDataAppCpa.getDeviceId(context);
    }

    public static void deepLink(String link) {
        TalkingDataAppCpa.onReceiveDeepLink(link);
    }

    public static String getOAID(Context context){
        return TalkingDataAppCpa.getOAID(context);
    }

    public static String getTDID(Context context){
       return TalkingDataAppCpa.getDeviceId(context);
    }
}
