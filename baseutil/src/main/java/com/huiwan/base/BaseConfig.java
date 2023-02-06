package com.huiwan.base;

/**
 * date 2020/10/24
 * email july.oloy@qq.com
 *
 * @author leoyuu
 */
public class BaseConfig {
    public static final int SERVER_YES = 1;
    public static final int SERVER_NO = 2;
    public static final String MEDIUM_IMAGE_SUFFIX = "?imageView/1/w/148/h/148";
    public final String versionName;
    public final int versionCode;
    public final String channel;
    public final boolean envDebug;
    public final boolean debugBuild;
    public final String commonVersionName;
    public final int commonVersionCode;
    public final boolean mainProcess;
    public final String processName;
    public final String appName;

    public BaseConfig(String versionName,
                      int versionCode,
                      String commonVersionName,
                      int commonVersionCode,
                      String channel,
                      boolean envDebug,
                      boolean debugBuild,
                      String processName,
                      boolean mainProcess,
                      String appName) {
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.commonVersionName = commonVersionName;
        this.commonVersionCode = commonVersionCode;
        this.channel = channel;
        this.envDebug = envDebug;
        this.debugBuild = debugBuild;
        this.processName = processName;
        this.mainProcess = mainProcess;
        this.appName = appName;
    }
}
