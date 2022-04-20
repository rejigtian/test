package com.example.test.GpLog.state;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class StateLine {
    public static void state() {
        stateDir();
    }

    private static void stateDir() {
        int c = stat(new File("/Users/leoyuu/wepie/wejoy-repo/component/"));
        int l = stat(new File("/Users/leoyuu/wepie/wejoy-repo/lib/"));
        int s = stat(new File("/Users/leoyuu/wepie/wejoy-repo/service/"));
        int m = stat(new File("/Users/leoyuu/wepie/wejoy-repo/module/"));
        int main = stat(new File("/Users/leoyuu/wepie/wejoy-repo/wejoy-android/"));
        System.out.println(c);
        System.out.println(l);
        System.out.println(s);
        System.out.println(m);
        System.out.println(main);
    }


    private static int stat(File file) {
        int count = 0;
        if (file.isFile()) {
            count = statFile(file);
        } else {
            if (file.getName().equals(".git")) {
                System.out.println("ignore git" + file.getAbsolutePath());
                return 0;
            }
            File[] children = file.listFiles();
            if (children != null && children.length > 0) {
                for (File f:children) {
                    count += stat(f);
                }
            }
        }
        return count;
    }

    private static int statFile(File file) {
        if (file.isFile() && file.getAbsolutePath().endsWith("java")) {
            try {
                FileReader fileReader = new FileReader(file);
                BufferedReader br = new BufferedReader(fileReader);
                String line;
                int count = 0;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    count++;
                }
                return count;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
}
