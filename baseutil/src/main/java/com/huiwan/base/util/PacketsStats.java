package com.huiwan.base.util;

import android.net.TrafficStats;
import android.os.Process;

/**
 * date 2019-12-25
 * email july.oloy@qq.com
 *
 * @author leoyuu
 */
public class PacketsStats {
    private static long startLogTime = 0;
    private static long startSendBytes = 0;
    private static long startReceiveBytes = 0;

    public static void resetStats() {
        startLogTime = System.currentTimeMillis();
        startSendBytes = TrafficStats.getUidTxBytes(Process.myUid());
        startReceiveBytes = TrafficStats.getUidRxBytes(Process.myUid());
    }

    public static long getStartReceiveBytes() {
        return startReceiveBytes;
    }

    public static long getStartSendBytes() {
        return startSendBytes;
    }

    public static long getStartLogTime() {
        return startLogTime;
    }

    public static long getReceiveBytesSinceReset() {
        return TrafficStats.getUidRxBytes(Process.myUid()) - startReceiveBytes;
    }


    public static long getSendBytesSinceReset() {
        return TrafficStats.getUidTxBytes(Process.myUid()) - startSendBytes;
    }
}
