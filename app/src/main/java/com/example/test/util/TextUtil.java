package com.example.test.util;

public class TextUtil {
    public static boolean isEmpty(String s){
        if (s == null) return true;
        return s.equals("");
    }

    public static String getString(String s){
        if (isEmpty(s)) return "null";
        else return s;
    }
}
