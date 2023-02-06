package com.huiwan.base.util.calender;

import com.huiwan.base.util.TimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LunarCalender {

    public LunarCalender() {
    }

    private final static int BASE_YEAR = 1901;

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

    public final static String[] chineseMouthNumber = {"正", "二", "三", "四", "五", "六", "七",
            "八", "九", "十", "冬", "腊"};

    public final static String[] chineseDayNumber = {
            "初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十",
            "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
            "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十"};

    // 数组中每一个元素存放1901~2050期间每一年的闰月月份，取值范围0~12（0表示该年没有闰月
    final static int[] hwLeapMonth = {
            0x00, 0x00, 0x05, 0x00, 0x00, 0x04, 0x00, 0x00, 0x02, 0x00, //1910
            0x06, 0x00, 0x00, 0x05, 0x00, 0x00, 0x02, 0x00, 0x07, 0x00, //1920
            0x00, 0x05, 0x00, 0x00, 0x04, 0x00, 0x00, 0x02, 0x00, 0x06, //1930
            0x00, 0x00, 0x05, 0x00, 0x00, 0x03, 0x00, 0x07, 0x00, 0x00, //1940
            0x06, 0x00, 0x00, 0x04, 0x00, 0x00, 0x02, 0x00, 0x07, 0x00, //1950
            0x00, 0x05, 0x00, 0x00, 0x03, 0x00, 0x08, 0x00, 0x00, 0x06, //1960
            0x00, 0x00, 0x04, 0x00, 0x00, 0x03, 0x00, 0x07, 0x00, 0x00, //1970
            0x05, 0x00, 0x00, 0x04, 0x00, 0x08, 0x00, 0x00, 0x06, 0x00, //1980
            0x00, 0x04, 0x00, 0x0A, 0x00, 0x00, 0x06, 0x00, 0x00, 0x05, //1990
            0x00, 0x00, 0x03, 0x00, 0x08, 0x00, 0x00, 0x05, 0x00, 0x00, //2000
            0x04, 0x00, 0x00, 0x02, 0x00, 0x07, 0x00, 0x00, 0x05, 0x00, //2010
            0x00, 0x04, 0x00, 0x09, 0x00, 0x00, 0x06, 0x00, 0x00, 0x04, //2020
            0x00, 0x00, 0x02, 0x00, 0x06, 0x00, 0x00, 0x05, 0x00, 0x00, //2030
            0x03, 0x00, 0x0B, 0x00, 0x00, 0x06, 0x00, 0x00, 0x05, 0x00, //2040
            0x00, 0x02, 0x00, 0x07, 0x00, 0x00, 0x05, 0x00, 0x00, 0x03, //2050
    };

    // 数组中每一个元素存放1901~2050期间每一年的12个月或13个月（有闰月）的月天数
    // 数组元素的低12位或13位（有闰月）分别对应着这12个月或13个月（有闰月），最低位对应着最小月（1月）
    // 如果月份对应的位为1则表示该月有30天，否则表示该月有29天。
    // （注：农历中每个月的天数只有29天或者30天）
    final static long[] hwMonthDay = {
            0x0752, 0x0EA5, 0x164A, 0x064B, 0x0A9B, 0x1556, 0x056A, 0x0B59, 0x1752, 0x0752, //1910
            0x1B25, 0x0B25, 0x0A4B, 0x12AB, 0x0AAD, 0x056A, 0x0B69, 0x0DA9, 0x1D92, 0x0D92, //1920
            0x0D25, 0x1A4D, 0x0A56, 0x02B6, 0x15B5, 0x06D4, 0x0EA9, 0x1E92, 0x0E92, 0x0D26, //1930
            0x052B, 0x0A57, 0x12B6, 0x0B5A, 0x06D4, 0x0EC9, 0x0749, 0x1693, 0x0A93, 0x052B, //1940
            0x0A5B, 0x0AAD, 0x056A, 0x1B55, 0x0BA4, 0x0B49, 0x1A93, 0x0A95, 0x152D, 0x0536, //1950
            0x0AAD, 0x15AA, 0x05B2, 0x0DA5, 0x1D4A, 0x0D4A, 0x0A95, 0x0A97, 0x0556, 0x0AB5, //1960
            0x0AD5, 0x06D2, 0x0EA5, 0x0EA5, 0x064A, 0x0C97, 0x0A9B, 0x155A, 0x056A, 0x0B69, //1970
            0x1752, 0x0B52, 0x0B25, 0x164B, 0x0A4B, 0x14AB, 0x02AD, 0x056D, 0x0B69, 0x0DA9, //1980
            0x0D92, 0x1D25, 0x0D25, 0x1A4D, 0x0A56, 0x02B6, 0x05B5, 0x06D5, 0x0EC9, 0x1E92, //1990
            0x0E92, 0x0D26, 0x0A56, 0x0A57, 0x14D6, 0x035A, 0x06D5, 0x16C9, 0x0749, 0x0693, //2000
            0x152B, 0x052B, 0x0A5B, 0x155A, 0x056A, 0x1B55, 0x0BA4, 0x0B49, 0x1A93, 0x0A95, //2010
            0x052D, 0x0AAD, 0x0AAD, 0x15AA, 0x05D2, 0x0DA5, 0x1D4A, 0x0D4A, 0x0C95, 0x152E, //2020
            0x0556, 0x0AB5, 0x15B2, 0x06D2, 0x0EA9, 0x0725, 0x064B, 0x0C97, 0x0CAB, 0x055A, //2030
            0x0AD6, 0x0B69, 0x1752, 0x0B52, 0x0B25, 0x1A4B, 0x0A4B, 0x04AB, 0x055B, 0x05AD, //2040
            0x0B6A, 0x1B52, 0x0D92, 0x1D25, 0x0D25, 0x0A55, 0x14AD, 0x04B6, 0x05B5, 0x0DAA, //2050
    };

    public int getMouthNum(int yearNum) {
        if (hwLeapMonth[yearNum - BASE_YEAR] > 0) {
            return 13;
        } else {
            return 12;
        }
    }

    public int getDayNum(int yearNum, int monthNum) {
        return (int) (29 + (1L & (hwMonthDay[yearNum - BASE_YEAR] >> (monthNum - 1))));
    }

    public int getLeapMouthIndex(int yearNum) {
        return hwLeapMonth[yearNum - BASE_YEAR];
    }

    public LunarCalendarInfo getLunarCalendarInfo(Calendar calendar) {
        LunarCalendarInfo info = new LunarCalendarInfo();
        try {
            Date startDate = formatter.parse("19010218");
            if (startDate != null) {
                Calendar c = Calendar.getInstance();
                c.setTime(startDate);
                int offset = (int) ((calendar.getTimeInMillis() - c.getTime().getTime()) / 86400000L);
                int i;
                for (i = 0; i < 150; i++) {
                    int temp = getYearDays(1901 + i);
                    if (offset - temp < 1) {
                        break;
                    } else {
                        offset -= temp;
                    }
                }
                info.year = 1901 + i;
                for (i = 1; i <= getMouthNum(info.year); i++) {
                    int temp = getDayNum(info.year, i);
                    if (offset - temp < 1) {
                        break;
                    }
                    offset -= temp;
                }
                info.mouth = i;
                info.day = offset;
            }
        } catch (ParseException e) {

        }
        return info;
    }

    public long getTimeInMillis(int yearNum, int monthNum, int dayNum) {
        int offset = 0;
        for (int i = BASE_YEAR; i < yearNum; i++) {
            int yearDaysCount = getYearDays(i); // 求阴历某年天数
            offset += yearDaysCount;
        }
        for (int i = 1; i < monthNum; i++) {
            int tempMonthDaysCount = getDayNum(yearNum, i);
            offset += tempMonthDaysCount;
        }
        offset += dayNum;
        try {
            Date startDate = formatter.parse("19010218");
            if (startDate != null) {
                Calendar c = Calendar.getInstance();
                c.setTime(startDate);
                return  c.getTime().getTime() + offset * 86400000L;
            }
        } catch (ParseException e) {
            return 0;
        }
        return 0;
    }

    private int getYearDays(int yearNum) {
        long num = hwMonthDay[yearNum - BASE_YEAR];
        num = ((num >> 1) & 0x5555) + (num & 0x5555);
        num = ((num >> 2) & 0x3333) + (num & 0x3333);
        num = ((num >> 4) & 0x0F0F) + (num & 0x0F0F);
        num = ((num >> 8) & 0x00FF) + (num & 0x00FF);
        int monthNum = getMouthNum(yearNum);
        return (int) (monthNum * 29 + num);
    }

    public static class LunarCalendarInfo {
        public int year;
        public int mouth;
        public int day;
    }

    private int getNowYearSpendDays(int year, int month, int day) {
        int res = 0;
        for (int i = 1; i < month; i++) {
            res += getDayNum(year, i);
        }
        return res + day;
    }

    public int getNextBirthDay(Calendar calendar) {
        int res = 0;
        LunarCalender.LunarCalendarInfo info = getLunarCalendarInfo(calendar);
        int oldLeapMonth = hwLeapMonth[info.year - BASE_YEAR];
        boolean isLeapMonth = false;
        int mouth = info.mouth;
        int day = info.day;
        if (oldLeapMonth > 0 && mouth > oldLeapMonth) {
            mouth -= 1;
            if (mouth == oldLeapMonth) isLeapMonth = true;
        }
        calendar.setTimeInMillis(TimeUtil.getServerTime());
        LunarCalender.LunarCalendarInfo nowInfo = getLunarCalendarInfo(calendar);
        int nowSpendDays = getNowYearSpendDays(nowInfo.year, nowInfo.mouth, nowInfo.day);
        for (int i = nowInfo.year; i <= Math.min(nowInfo.year + 10, 2050); i++) {
            int leapMonth = hwLeapMonth[i - BASE_YEAR];
            if (leapMonth > 0) {
                if (leapMonth > mouth) {
                    if (day > getDayNum(i, mouth)) {
                        res += getYearDays(i);
                    } else {
                        int spendDays = getNowYearSpendDays(i, mouth, day);
                        if (spendDays + res >= nowSpendDays) {
                            res += spendDays - nowSpendDays;
                            return res;
                        } else {
                            res += getYearDays(i);
                        }
                    }
                } else if (leapMonth == mouth) {
                    if (isLeapMonth) {
                        if (day <= getDayNum(i, mouth + 1)) {
                            int spendDays = getNowYearSpendDays(i, mouth + 1, day);
                            if (spendDays + res >= nowSpendDays) {
                                res += spendDays - nowSpendDays;
                                return res;
                            } else {
                                res += getYearDays(i);
                            }
                        } else if (day <= getDayNum(i, mouth)) {
                            int spendDays = getNowYearSpendDays(i, mouth, day);
                            if (spendDays + res >= nowSpendDays) {
                                res += spendDays - nowSpendDays;
                                return res;
                            } else {
                                res += getYearDays(i);
                            }
                        } else {
                            res += getYearDays(i);
                        }
                    } else {
                        if (day > getDayNum(i, mouth)) {
                            res += getYearDays(i);
                        } else {
                            int spendDays = getNowYearSpendDays(i, mouth, day);
                            if (spendDays + res >= nowSpendDays) {
                                res += spendDays - nowSpendDays;
                                return res;
                            } else {
                                res += getYearDays(i);
                            }
                        }
                    }
                } else {
                    if (day > getDayNum(i, mouth + 1)) {
                        res += getYearDays(i);
                    } else {
                        int spendDays = getNowYearSpendDays(i, mouth + 1, day);
                        if (spendDays + res >= nowSpendDays) {
                            res += spendDays - nowSpendDays;
                            return res;
                        } else {
                            res += getYearDays(i);
                        }
                    }
                }
            } else {
                if (day > getDayNum(i, mouth)) {
                    res += getYearDays(i);
                } else {
                    int spendDays = getNowYearSpendDays(i, mouth, day);
                    if (spendDays + res >= nowSpendDays) {
                        res += spendDays - nowSpendDays;
                        return res;
                    } else {
                        res += getYearDays(i);
                    }
                }
            }
        }
        return res;
    }
}
