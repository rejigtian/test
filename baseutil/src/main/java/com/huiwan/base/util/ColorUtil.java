package com.huiwan.base.util;

import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class ColorUtil {

    public static int toHexColor(int colorInt10) {
        try {
            String hex = Integer.toHexString(colorInt10);
            return Color.parseColor("#" + hex);
        } catch (Exception var2) {
            return Color.parseColor("#ffffff");
        }
    }

    public static int getColor(String hex) {
        try {
            hex = hex.replace("0x", "");
            hex = hex.replace("#", "");
            return Color.parseColor("#" + hex);
        } catch (Exception var2) {
            return Color.parseColor("#ffffff");
        }
    }
    
    public static int getARGB(int color) {
        return color | 0xff000000;
    }

    public static int getAnimatedColor(int startColor, int endColor, float animatedValue) {
        int sGreen = Color.green(startColor);
        int sRed = Color.red(startColor);
        int sBlue= Color.blue(startColor);
        int sAlpha = Color.alpha(startColor);
        int eGreen = Color.green(endColor);
        int eRed = Color.red(endColor);
        int eBlue = Color.blue(endColor);
        int eAlpha = Color.alpha(endColor);

        Color.argb(1,2,3,4);

        return Color.argb(getMidValue(sAlpha, eAlpha, animatedValue),
                getMidValue(sRed, eRed, animatedValue),
                getMidValue(sGreen, eGreen, animatedValue),
                getMidValue(sBlue, eBlue, animatedValue));
    }

    private static int getMidValue(int var1, int var2, float potion) {
        return var1 + (int) ((var2 - var1) * potion);
    }
}