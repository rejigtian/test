package com.example.test.GpLog.state;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StateUtil {

    private static final String STR_REG = "\"[^\"]+\"";

    private static final Pattern P = Pattern.compile(STR_REG);

    public static void state() throws IOException{
//        List<String> pathList = listDirs();
        List<String> pathList = new ArrayList<String>(){
            {
                add("/Users/rejig/AndroidxProjects/repo-wespy-bak/wespy-android/wepie/src/com/wepie/wespy/module/voiceroom/main/rootview/AudioMatchRoomView.java");
                add("/Users/rejig/AndroidxProjects/repo-wespy-bak/wespy-android/wepie/src/com/wepie/wespy/module/voiceroom/seat/AudioMatchSeatView.java");
                add("/Users/rejig/AndroidxProjects/repo-wespy-bak/wespy-android/wepie/src/com/wepie/wespy/module/voiceroom/main/bottom/RoomBottomView.java");
                add("/Users/rejig/AndroidxProjects/repo-wespy-bak/wespy-android/wepie/src/com/wepie/wespy/module/match");
                add("/Users/rejig/AndroidxProjects/repo-wespy-bak/wespy-android/wepie/res/layout/game_help_view.xml");
                add("/Users/rejig/AndroidxProjects/repo-wespy-bak/wespy-android/wepie/res/layout/audio_match_ban_view.xml");
                add("/Users/rejig/AndroidxProjects/repo-wespy-bak/wespy-android/wepie/res/layout/audio_match_fail_view.xml");
                add("/Users/rejig/AndroidxProjects/repo-wespy-bak/wespy-android/wepie/res/layout/audio_match_match_view.xml");
                add("/Users/rejig/AndroidxProjects/repo-wespy-bak/wespy-android/wepie/res/layout/audio_match_over_view.xml");
                add("/Users/rejig/AndroidxProjects/repo-wespy-bak/wespy-android/wepie/res/layout/audio_match_prepare_view.xml");
                add("/Users/rejig/AndroidxProjects/repo-wespy-bak/wespy-android/wepie/res/layout/audio_matching_floating_view.xml");
                add("/Users/rejig/AndroidxProjects/repo-wespy-bak/wespy-android/wepie/res/layout/audio_match_room_view.xml");
                add("/Users/rejig/AndroidxProjects/repo-wespy-bak/wespy-android/wepie/res/layout/audio_match_seat_view.xml");


            }
        };
        for (String path:pathList) {
            stat(new File(path));
        }
    }

    private static List<String> listDirs() throws IOException {
        String settingsPath = "/Users/leoyuu/wepie/wejoy-repo/wejoy-android/settings.gradle";
        BufferedReader br = new BufferedReader(new FileReader(settingsPath));
        String s;
        List<String> pathList = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            if (s.startsWith("doInclude")) {
                String[] moduleArr = s.substring("doInclude".length()).split(",");
                for (String module:moduleArr) {
                    String m = module.trim();
                    String mm = m.substring(m.indexOf("'") + 1, m.lastIndexOf("'"));
                    String mPath = mm.replaceAll(":", "/");
                    System.out.println("module: " + m + ", " + mPath);
                    pathList.add("/Users/leoyuu/wepie/wejoy-repo" + mPath + "/src/main/java");
                }
            }
        }
        System.out.println(pathList);
        return pathList;
    }

    public static boolean isStr(String str) {
        System.out.println(str);
        Matcher matcher = P.matcher(str);
        if (matcher.find()) {
            int groupCount = matcher.groupCount();
            System.out.println("count: " + groupCount);
            for (int i = 0; i < groupCount; i++) {
                System.out.printf("group[%d]: %s\n", i, matcher.group(i));
            }
            return true;
        }
        return false;
    }

    public static void stat(File file) {
        if (file.isFile()) {
            statFile(file);
        } else {
            File[] children = file.listFiles();
            if (children != null && children.length > 0) {
                for (File f:children) {
                    stat(f);
                }
            }
        }
    }

    private static void statFile(File file) {
        if (file.isFile() && (file.getAbsolutePath().endsWith("java") || file.getAbsolutePath().endsWith("xml"))) {
            String path = file.getAbsolutePath();
            if (path.contains("ShenceEvent") || path.contains("ShenceGameTypeSource")) {
                return;
            }
            try {
                FileReader fileReader = new FileReader(file);
                BufferedReader br = new BufferedReader(fileReader);
                String line;
                int count = 0;
                while ((line = br.readLine()) != null) {
                    count++;
                    if (hasZh(line)) {
//                        System.out.println(file.getName());
//                        System.out.printf("%s \t\t%s:%d\n", line.trim(), file.getName(), count);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean hasZh(String line) {
        Matcher matcher = P.matcher(line);
        if (matcher.find()) {
            if (isChinese(matcher.group())){
                System.out.println(matcher.group());
                return true;
            }
        }
        return false;
    }

    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }

    public static boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (char c : ch) {
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }
}
