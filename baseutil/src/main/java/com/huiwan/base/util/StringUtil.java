
package com.huiwan.base.util;

import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StringUtil {
    private static final String DEEP_LINK = "wespydeeplink://";
    private static final String HASH_ALGORITHM = "MD5";
    private static final int RADIX = 10 + 26; // 10 digits + 26 letters


    public static boolean isCharacter(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        char character = name.charAt(0);
        int i = (int) character;
        if ((i >= 65 && i <= 90) || (i >= 97 && i <= 122)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCharacter(char character) {
        int i = (int) character;
        if ((i >= 65 && i <= 90) || (i >= 97 && i <= 122)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCapital(char letter) {
        if (letter >= 65 && letter <= 90) return true;
        return false;
    }

    public static String getIndexStr(int index) {
        if (index >= 0 && index <= 9) {
            switch (index) {
                case 0:
                    return "零";
                case 1:
                    return "一";
                case 2:
                    return "二";
                case 3:
                    return "三";
                case 4:
                    return "四";
                case 5:
                    return "五";
                case 6:
                    return "六";
                case 7:
                    return "七";
                case 8:
                    return "八";
                case 9:
                    return "九";
            }
        }
        return "";
    }

    /**
     * Creates an string using UTF-8 encoding.
     *
     * @return a string in UTF-8 encoding.
     */
    public static String newUtf8String(byte[] data, int offset, int length) {
        try {
            return new String(data, offset, length, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 is not supported.");
        }
    }

    public static String decimalsFormat(float value, int digit) {
        StringBuilder builder = new StringBuilder();
        builder.append("##0");
        for (int i = 0; i < digit; i++) {
            if (i == 0) {
                builder.append(".");
            }
            builder.append("0");
        }
        DecimalFormat format = new DecimalFormat(builder.toString());
        return format.format(value);
    }

    //判断text为   null 、 null字符串  、 ""
    public static boolean isNull(String text) {
        if (text == null) {
            return true;
        }
        if ("".equals(text)) {
            return true;
        }
        if ("null".equals(text)) {
            return true;
        }
        return false;
    }

    /**
     * number 超过10000，显示1万，不足10000显示全数字
     *
     * @param number
     * @return
     */
    public static String formatInteger(int number) {
        int maxCoin = number;
        String maxCoinText;
        if (maxCoin >= 10000) {
            if (maxCoin % 10000 == 0) {
                maxCoinText = maxCoin / 10000 + "万";
            } else {
                maxCoinText = new DecimalFormat("#.00").format(maxCoin / 10000.0f) + "万";
            }
        } else {
            maxCoinText = maxCoin + "";
        }
        return maxCoinText;
    }

    /**
     * number 超过10000，显示1w，不足10000显示全数字
     *
     * @param number
     * @return
     */
    public static String formatGuardNumber(int number) {
        int absNum = Math.abs(number);
        String maxCoinText;
        long yi = 10000 * 10000;
        long wan = 10000;
        if (absNum >= yi) {
            maxCoinText = new DecimalFormat("#.#").format(absNum * 1.00f / yi) + "y";
        } else if (absNum >= wan * 1000) {
            maxCoinText = absNum / wan + "w";
        } else if (absNum >= wan) {
            maxCoinText = new DecimalFormat("#.#").format(absNum * 1.00f / wan) + "w";
        } else {
            maxCoinText = absNum + "";
        }
        if (number < 0) maxCoinText = "-" + maxCoinText;
        return maxCoinText;
    }

    public static String formatGuardNumber(long number) {
        long absNum = Math.abs(number);
        String maxCoinText;
        long m = 1000_0000;
        long yi = 10000 * 10000;
        long wan = 10000;
        if (absNum >= yi) {
            maxCoinText = new DecimalFormat("#.#").format(absNum * 1.00f / yi) + "y";
        } else if (absNum >= m) {
            maxCoinText = new DecimalFormat("#.#").format(absNum * 1.00f / m) + "m";
        } else if (absNum >= wan) {
            maxCoinText = new DecimalFormat("#.#").format(absNum * 1.00f / wan) + "w";
        } else {
            maxCoinText = absNum + "";
        }
        if (number < 0) maxCoinText = "-" + maxCoinText;
        return maxCoinText;
    }



    /**
     * 整数则去掉小数点，浮点数保留小数点后一位
     *
     * @param number
     * @return
     */
    public static String formatInteger(float number) {
        String text;
        if (number % 1 > 0.1) {
            text = new DecimalFormat("0.0").format(number);
        } else {
            text = (int) number + "";
        }
        return text;
    }

    public static String uidArray2String(List<Integer> uidList) {
        int size = uidList.size();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            int uid = uidList.get(i);
            builder.append(uid).append(",");
        }
        if (builder.length() > 0) builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public static String uidSet2String(Set<Integer> uidList) {
        StringBuilder builder = new StringBuilder();
        for (int uid : uidList) {
            builder.append(uid).append(",");
        }
        if (builder.length() > 0) builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public static String list2String(List<?> list) {
        if (list == null) return "";
        int size = list.size();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            Object o = list.get(i);
            builder.append(o).append(",");
        }
        if (builder.length() > 0) builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public static List<Integer> uidString2Array(String uidStr) {
        List<Integer> uidList = new ArrayList<>();
        if (TextUtils.isEmpty(uidStr)) return uidList;
        String[] uids = uidStr.split(",");
        int len = uids.length;
        for (int i = 0; i < len; i++) {
            try {
                uidList.add(Integer.parseInt(uids[i]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return uidList;
    }

    /**
     * number 超过10000，显示1万，不足10000显示全数字，多余位直接省略
     *
     * @return
     */
    public static String formatDouDouLong(long maxCoin) {
        String maxCoinText;
        long yi = 10000 * 10000;
        long wan = 10000;
        if (maxCoin >= yi) {
            if (maxCoin % yi == 0) {
                maxCoinText = maxCoin / yi + "亿";
            } else {
                maxCoinText = new DecimalFormat("#.00").format(maxCoin * 1.00f / yi) + "亿";
            }
        } else if (maxCoin >= wan) {
            if (maxCoin % wan == 0) {
                maxCoinText = maxCoin / wan + "万";
            } else {
                maxCoinText = new DecimalFormat("#.00").format(maxCoin * 1.00f / wan) + "万";
            }
        } else {
            maxCoinText = maxCoin + "";
        }
        return maxCoinText;
    }

    public static int parseInteger(String integer) {
        int result = 0;
        try {
            result = Integer.parseInt(integer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String formatS(String fmt, Object... args) {
        StringBuilder sb = new StringBuilder(fmt);
        for (Object arg : args) {
            int index = sb.indexOf("{}");
            if (index >= 0) {
                try {
                    sb.replace(index, index + 2, String.valueOf(arg));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
        return sb.toString();
    }

    /**
     * 获取字符串中对应的字符个数
     *
     * @param s 字符串
     * @param c 字符
     * @return 字符个数
     */
    public static int getCharNumber(CharSequence s, char c) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        int number = 0;
        for (int i = 0; i < s.length(); i++) {
            if (c == s.charAt(i)) {
                number++;
            }
        }
        return number;
    }

    public static boolean isValid(String s) {
        if (isNull(s)) {
            return false;
        }
        return getCharNumber(s, ' ') != s.length();
    }

    @Nullable
    public static Uri parseDeeplink(String deeplink) {
        try {
            if (TextUtils.isEmpty(deeplink)) {
                return null;
            }

            if (!deeplink.startsWith(DEEP_LINK)) {
                return null;
            }

            //去掉消息最后的最后一个/
            if (deeplink.endsWith("/")) {
                deeplink = deeplink.substring(0, deeplink.length() - 1);
            }

            return Uri.parse(deeplink);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMd5(String str) {
        byte[] md5 = getMD5(str.getBytes());
        BigInteger bi = new BigInteger(md5).abs();
        return bi.toString(RADIX);
    }

    public static String getLimitedStr(String s, int limit) {
        if (s.length() <= limit) {
            return s;
        }
        return s.substring(0, limit) + "...";
    }

    private static byte[] getMD5(byte[] data) {
        byte[] hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.update(data);
            hash = digest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }

    public static String toCoinString(int num) {
        if (num <= 9_999) {
            return num + "";
        } else if (num <= 9_999_999) {
            int k = num / 1000;
            return k + "K";
        } else {
            int m = num / 1_000_000;
            return m + "M";
        }
    }

    public static String getNumString(float num) {
        if (num < 1) {
            return "";
        } else if (num < 1000F) {
            return String.valueOf((int) num);
        } else if (num < 9500F) {
            return Math.round(num / 1000F) + "k";
        } else {
            return Math.round(num / 10000F) + "w";
        }
    }

    public static String getNum(int num) {
        String str = "x" + num;
        return str;
    }

    public static String cropString(String str, int maxNum) {
        if (str.length() <= maxNum) return str;
        String tmp = str.substring(0, maxNum - 1);
        return tmp + "…";
    }

    public static String getSize(long num) {
        int mb = 1024 * 1024;
        return new DecimalFormat("#0.0").format(num * 1.0f / mb) + "M";
    }
}