package com.huiwan.base.util;

import java.util.List;

/**
 * Created by bigwen on 2020-04-07.
 */
public class ByteUtil {

    public static byte[] mergeByte(byte[] byte1, byte[] byte2) {
        byte[] byte3 = new byte[byte1.length + byte2.length];
        System.arraycopy(byte1, 0, byte3, 0, byte1.length);
        System.arraycopy(byte2, 0, byte3, byte1.length, byte2.length);
        return byte3;
    }

    public static byte[] mergeByteList(List<byte[]> byteList, int totalSampleSize) {
        byte[] sampleBytes = new byte[totalSampleSize];
        int currentSize = 0;
        for (byte[] bytes : byteList){
            System.arraycopy(bytes, 0, sampleBytes, currentSize, bytes.length);
            currentSize += bytes.length;
        }
        return sampleBytes;
    }

    public static int getTotalSize(List<byte[]> byteList){
        int totalSize = 0;
        for (byte[] bytes : byteList){
            totalSize += bytes.length;
        }
        return totalSize;
    }
}
