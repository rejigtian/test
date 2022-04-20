package com.example.test.GpLog.state;

import java.io.File;

public class StateDir {
    public static void stateDir() {
        folder(new File("/Users/leoyuu/wepie/wejoy-repo/component/"));
        folder(new File("/Users/leoyuu/wepie/wejoy-repo/lib/"));
        folder(new File("/Users/leoyuu/wepie/wejoy-repo/service/"));
        folder(new File("/Users/leoyuu/wepie/wejoy-repo/module/"));
        folder(new File("/Users/leoyuu/wepie/wejoy-repo/wejoy-android/"));
    }

    private static void folder(File file) {
        if (file.isDirectory()) {
            String path = file.getAbsolutePath();
            if (path.contains(".git")) {
                return;
            }
            if (path.contains("rtw")) {
                System.out.println(path);
                return;
            }
            File[] children = file.listFiles();
            if (children == null) {
                return;
            }
            for (File child:children) {
                folder(child);
            }
        }
    }

    private static void delFolder(File file) {

    }
}
