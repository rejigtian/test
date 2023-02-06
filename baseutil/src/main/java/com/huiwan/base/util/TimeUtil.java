package com.huiwan.base.util;


import android.os.SystemClock;

import com.huiwan.base.LibBaseUtil;
import com.huiwan.base.R;
import com.huiwan.base.str.Language;
import com.huiwan.base.str.ResUtil;
import com.huiwan.base.util.calender.LunarCalender;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by zhuguangwen on 15/3/2.
 * email 979343670@qq.com
 */
public class TimeUtil {
    private static final String TAG = "TimeUtil";
    public static final long TIME_MIN = 60 * 1000;
    public static final long TIME_HOUR = 60 * 60 * 1000;
    public static final long TIME_DAY = TIME_HOUR * 24;
    public static final long TIME_MONTH = TIME_DAY * 30;
    public static long serverTimeOffset = 0;
    private static final SimpleDateFormat format01 = new SimpleDateFormat("MM-dd");
    private static final SimpleDateFormat format02 = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat format03 = new SimpleDateFormat(ResUtil.getStr(R.string.time_format_mon_day));
    private static final SimpleDateFormat format04 = new SimpleDateFormat("yyyy年MM月dd日");
    private static final SimpleDateFormat format05 = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat format06 = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat format07 = new SimpleDateFormat("MM-dd HH:mm");
    private static final SimpleDateFormat format08 = new SimpleDateFormat("yyyy/MM/dd");
    private static final SimpleDateFormat format09 = new SimpleDateFormat("MM月dd日HH点");
    private static final SimpleDateFormat format10 = new SimpleDateFormat("MM/dd/HH:00");
    private static final SimpleDateFormat format11 = new SimpleDateFormat("HH:mm:SS");
    private static final SimpleDateFormat format12 = new SimpleDateFormat("yyyy-MM");
    private static final SimpleDateFormat format13 = new SimpleDateFormat("yyyy.MM.dd");
    private static final SimpleDateFormat format14 = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
    private static final SimpleDateFormat format15 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private static long elapsedRealtimeOffset = Long.MIN_VALUE;

    private static Calendar calendar = Calendar.getInstance();

    public static int getDiffDay(long time) {
        calendar.setTimeInMillis(TimeUtil.getServerTime());
        int cur_year = calendar.get(Calendar.YEAR);
        int cur_day_of_year = calendar.get(Calendar.DAY_OF_YEAR);

        calendar.setTimeInMillis(time);
        int target_year = calendar.get(Calendar.YEAR);
        int target_day_of_year = calendar.get(Calendar.DAY_OF_YEAR);

        int diff_day = cur_day_of_year - target_day_of_year + (cur_year - target_year) * 365;
        return diff_day;
    }

    public static String longToTimeString(long time) {
        long curTime = System.currentTimeMillis();

        if (curTime - time < 0) {
            return format01.format(time);//date

        } else if (curTime - time < 60 * 1000) {
            return ResUtil.getString(R.string.just_now);

        } else if (curTime - time < 3600 * 1000) {
            long minute = (curTime - time) / (1000 * 60);
            return minute + ResUtil.getString(R.string.minutes_ago);

        } else if (curTime - time < 3600 * 1000 * 2) {
            return "1" + ResUtil.getStr(R.string.hours_ago);

        } else if (curTime - time > 48L * 3600 * 1000) {
            if (curTime - time > 180 * 24L * 3600 * 1000) {
                //超过半年显示年份
                int curYear = getYear(System.currentTimeMillis());
                int timeYear = getYear(time);
                if (curYear != timeYear) return format06.format(time);
            }
            return format01.format(time);//date
        }


        int curDate = getDateMothDay(System.currentTimeMillis());
        int timeDate = getDateMothDay(time);
        if (timeDate == curDate) {
            return format02.format(time);//today
        } else {
            int lastDate = getLastDate();
            if (timeDate == lastDate) {
                return ResUtil.getString(R.string.yesterday) + format02.format(time);//last day
            } else {
                return format01.format(time);//date
            }
        }
    }

    /**
     * 返回时间
     * 2022-12-02
     */
    public static String getCurTime() {
        long curTime = TimeUtil.getServerTime();
        return format06.format(curTime);
    }

    /**
     * 返回时间
     * 2022—12-21
     */
    public static String timeToYearAndMonthAndDay(long time) {
        return format06.format(time);
    }

    /**
     * 返回时间
     * 2022-12-02
     */
    public static String longToTimeHMS(long time) {
        return format11.format(time);
    }

    public static String longToTimeStringSec(long time) {
        long curTime = System.currentTimeMillis();

        if (curTime - time < 0) {
            return format01.format(time);//date

        } else if (curTime - time < 60 * 1000) {
            long sec = (curTime - time) / 1000 + 1;
            return sec + ResUtil.getStr(R.string.second_ago);

        } else if (curTime - time < 3600 * 1000) {
            long minute = (curTime - time) / (1000 * 60);
            return minute + ResUtil.getString(R.string.minutes_ago);

        } else if (curTime - time < 3600 * 1000 * 24) {
            long hour = (curTime - time) / (1000 * 3600);
            return hour + ResUtil.getStr(R.string.hours_ago);

        } else if (curTime - time > 48L * 3600 * 1000) {
            if (curTime - time > 180 * 24L * 3600 * 1000) {
                //超过半年显示年份
                int curYear = getYear(System.currentTimeMillis());
                int timeYear = getYear(time);
                if (curYear != timeYear) return format06.format(time);
            }
            return format01.format(time);//date
        }


        int curDate = getDateMothDay(System.currentTimeMillis());
        int timeDate = getDateMothDay(time);
        if (timeDate == curDate) {
            return format02.format(time);//today
        } else {
            int lastDate = getLastDate();
            if (timeDate == lastDate) {
                return ResUtil.getString(R.string.yesterday) + format02.format(time);//last day
            } else {
                return format01.format(time);//date
            }
        }
    }


    public static String longToTimeStringChatList(long time) {
        if (TimeUtil.isToday(time)) {
            return TimeUtil.longToMin(time);
        } else if (TimeUtil.IsYesterday(time)) {
            return ResUtil.getString(R.string.yesterday) + " " + TimeUtil.longToMin(time);
        } else if (TimeUtil.isBeforeDays(time, 7)) {
            return TimeUtil.longToWeek(time);
        } else {
            return format08.format(time);
        }
    }

    public static int getDateMothDay(long time) {
        calendar.clear();
        calendar.setTimeInMillis(time);
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        return d;
    }

    public static int getYear(long time) {
        calendar.clear();
        calendar.setTimeInMillis(time);
        int y = calendar.get(Calendar.YEAR);
        return y;
    }

    public static int getLastDate() {
        calendar.clear();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        int lastDate = calendar.get(Calendar.DAY_OF_MONTH);
        return lastDate;
    }

    public static String longToYearMonth(long time) {
        return new SimpleDateFormat("yyyy-MM").format(time);
    }

    public static String longToHourMin(long time) {
        return new SimpleDateFormat("HH:mm").format(time);
    }


    public static String longToStandardTimeString(long time) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
    }

    public static String longToStandardTimeStringNoSecond(long time) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(time);
    }

    public static String longToMinAndSec(long time) {
        return new SimpleDateFormat("mm:ss").format(time);
    }

    public static String longToYearMonthAndDay(long time) {
        if (LibBaseUtil.getLang().isChinese()) {
            return format04.format(time);
        } else {
            return format08.format(time);
        }
    }

    public static String longToYearMonthAndDayFormat13(long time) {
        return format13.format(time);
    }

    public static String longToYearMonthAndDayFormat14(long time) {
        return format14.format(time);
    }

    public static String longToMonthDayAndHour(long time) {
        if (LibBaseUtil.getLang().isChinese()) {
            return format09.format(time);
        } else {
            return format08.format(time);
        }
    }

    public static String longToHourMinSec(long time) {
        long hour = time / (60 * 60);
        long min = time % (60 * 60) / 60;
        long sec = time % (60 * 60) % 60;
        return ResUtil.getString(R.string.left_time_tips) + hour + ":" + min + ":" + sec;
    }

    public static String longToHourMinSecString(int time) {
        String hour = String.valueOf((time / (60 * 60)));
        if (hour.length() == 1) {
            hour = "0" + hour;
        }
        String min = String.valueOf((time % (60 * 60) / 60));
        if (min.length() == 1) {
            min = "0" + min;
        }
        String sec = String.valueOf((time % (60 * 60) % 60));
        if (sec.length() == 1) {
            sec = "0" + sec;
        }
        return hour + ":" + min + ":" + sec;
    }

    public static String longToYearMonthDay(long time) {
        return format05.format(time);
    }

    /**
     * 判断日期是否是指定的某一天
     * 调用本地时间比对，若本地时间不准则会有误差
     *
     * @param time   查询的时间
     * @param addDay 1：查询日期是否是明天      2 为后天  -1 为昨天
     * @return
     */
    public static boolean isNextDay(long time, int addDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, addDay);
        int year = calendar.get(Calendar.YEAR);
        int mouth = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.YEAR) == year
                && calendar.get(Calendar.MONTH) == mouth
                && calendar.get(Calendar.DAY_OF_MONTH) == day;
    }

    public static String longToMin(long time) {
        return new SimpleDateFormat("HH:mm").format(time);
    }

    public static String longToHour(long time) {
        if (LibBaseUtil.getLang() == Language.en) {
            return new SimpleDateFormat("HH").format(time);
        }
        return new SimpleDateFormat("HH点").format(time);
    }

    public static String longToYearAndDayAndMin(long time) {
        if (LibBaseUtil.getLang() == Language.en) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(time);
        }
        return new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(time);
    }

    public static String longToDayAndMin(long time) {
        if (LibBaseUtil.getLang() == Language.en) {
            return new SimpleDateFormat("MM-dd HH:mm").format(time);
        }
        return new SimpleDateFormat("MM月dd日 HH:mm").format(time);
    }

    public static int longToDay(long time) {
        if (time < 0) return 1;
        return (int) (time / (1000 * 60 * 60 * 24) + 1);
    }

    public static String longToMonthAndDay(long time) {
        return format03.format(time);
    }

    public static String longToYearAndDay(long time) {
        if (LibBaseUtil.getLang() == Language.en) {
            return format08.format(time);
        }
        return format04.format(time);
    }

    public static String longToWeek(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return ResUtil.getStr(R.string.time_monday_str);
            case Calendar.TUESDAY:
                return ResUtil.getStr(R.string.time_tuesday_str);
            case Calendar.WEDNESDAY:
                return ResUtil.getStr(R.string.time_wednesday_str);
            case Calendar.THURSDAY:
                return ResUtil.getStr(R.string.time_thursday_str);
            case Calendar.FRIDAY:
                return ResUtil.getStr(R.string.time_friday_str);
            case Calendar.SATURDAY:
                return ResUtil.getStr(R.string.time_saturday_str);
            case Calendar.SUNDAY:
                return ResUtil.getStr(R.string.time_sunday_str);
            default:
                return format06.format(time);
        }
    }

    public static boolean isCurrentMonth(long time) {
        calendar.setTimeInMillis(System.currentTimeMillis());
        int cur_year = calendar.get(Calendar.YEAR);
        int cur_month = calendar.get(Calendar.MONTH);

        calendar.setTimeInMillis(time);
        int target_year = calendar.get(Calendar.YEAR);
        int target_month = calendar.get(Calendar.MONTH);

        return cur_year == target_year && cur_month == target_month;
    }

    public static boolean isTargetMonth(long time, long targetTime) {
        calendar.setTimeInMillis(time);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        calendar.setTimeInMillis(targetTime);
        int target_year = calendar.get(Calendar.YEAR);
        int target_month = calendar.get(Calendar.MONTH);

        return year == target_year && month == target_month;
    }

    public static String longToMonth(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        switch (cal.get(Calendar.MONTH)) {
            case Calendar.JANUARY:
                return ResUtil.getStr(R.string.time_month_str_1);
            case Calendar.FEBRUARY:
                return ResUtil.getStr(R.string.time_month_str_2);
            case Calendar.MARCH:
                return ResUtil.getStr(R.string.time_month_str_3);
            case Calendar.APRIL:
                return ResUtil.getStr(R.string.time_month_str_4);
            case Calendar.MAY:
                return ResUtil.getStr(R.string.time_month_str_5);
            case Calendar.JUNE:
                return ResUtil.getStr(R.string.time_month_str_6);
            case Calendar.JULY:
                return ResUtil.getStr(R.string.time_month_str_7);
            case Calendar.AUGUST:
                return ResUtil.getStr(R.string.time_month_str_8);
            case Calendar.SEPTEMBER:
                return ResUtil.getStr(R.string.time_month_str_9);
            case Calendar.OCTOBER:
                return ResUtil.getStr(R.string.time_month_str_10);
            case Calendar.NOVEMBER:
                return ResUtil.getStr(R.string.time_month_str_11);
            case Calendar.DECEMBER:
                return ResUtil.getStr(R.string.time_month_str_12);
            default:
                return format06.format(time);
        }
    }

    public static String getMonthStr(int monInt) {
        switch (monInt) {
            case 1:
                return ResUtil.getStr(R.string.time_month_str_1);
            case 2:
                return ResUtil.getStr(R.string.time_month_str_2);
            case 3:
                return ResUtil.getStr(R.string.time_month_str_3);
            case 4:
                return ResUtil.getStr(R.string.time_month_str_4);
            case 5:
                return ResUtil.getStr(R.string.time_month_str_5);
            case 6:
                return ResUtil.getStr(R.string.time_month_str_6);
            case 7:
                return ResUtil.getStr(R.string.time_month_str_7);
            case 8:
                return ResUtil.getStr(R.string.time_month_str_8);
            case 9:
                return ResUtil.getStr(R.string.time_month_str_9);
            case 10:
                return ResUtil.getStr(R.string.time_month_str_10);
            case 11:
                return ResUtil.getStr(R.string.time_month_str_11);
            case 12:
                return ResUtil.getStr(R.string.time_month_str_12);
            default:
                return ResUtil.getStr(R.string.time_month_str_1);
        }
    }

    public static boolean isToday(long time) {
        calendar.setTimeInMillis(System.currentTimeMillis());
        int cur_year = calendar.get(Calendar.YEAR);
        int cur_day_of_year = calendar.get(Calendar.DAY_OF_YEAR);

        calendar.setTimeInMillis(time);
        int target_year = calendar.get(Calendar.YEAR);
        int target_day_of_year = calendar.get(Calendar.DAY_OF_YEAR);

        return cur_year == target_year && cur_day_of_year == target_day_of_year;
    }

    public static boolean IsYesterday(long day) {
        Calendar pre = Calendar.getInstance();
        pre.setTimeInMillis(System.currentTimeMillis());

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(day);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == -1) {
                return true;
            }
        }
        return false;
    }

    //判断日期是否在days天以内
    public static boolean isBeforeDays(long time, int days) {
        Calendar pre = Calendar.getInstance();
        pre.setTimeInMillis(System.currentTimeMillis());

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = pre.get(Calendar.DAY_OF_YEAR)
                    - cal.get(Calendar.DAY_OF_YEAR);

            return diffDay < days;
        }
        return false;
    }

    /*
     *
     *获得当天0点时间
     */
    public static long getTimes0(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime().getTime();
    }

    public static boolean isThisYear(long time) {
        calendar.setTimeInMillis(System.currentTimeMillis());
        int cur_year = calendar.get(Calendar.YEAR);

        calendar.setTimeInMillis(time);
        int target_year = calendar.get(Calendar.YEAR);

        return target_year == cur_year;
    }

    public static String getToday() {
        return format03.format(System.currentTimeMillis());
    }

    public static String formatMouthDay(long time) {
        return format03.format(time);
    }


    /**
     * 判断前天，昨天，今天，明天，后天
     *
     * @param time 输入的时间
     * @param now  现在
     * @return -2: 前天 -1：昨天： 0 ： 今天， 1： 明天， 2：后天，
     */
    public static int getDayTime(long time, long now) {
        int offSet = Calendar.getInstance().getTimeZone().getRawOffset();
        long today = (now + offSet) / TIME_DAY;
        long start = (time + offSet) / TIME_DAY;
        return (int) (start - today);
    }

    /**
     * @param timeInSec 秒
     * @return 00:00:00
     */
    public static String formTotalTime(int timeInSec) {
        int s = timeInSec % 60;
        int m = timeInSec / 60 % 60;
        int h = timeInSec / 3600;
        StringBuilder stringBuffer = new StringBuilder();

        if (h < 10) {
            stringBuffer.append("0").append(h);
        } else {
            stringBuffer.append(h);
        }

        stringBuffer.append(":");

        if (m < 10) {
            stringBuffer.append("0").append(m);
        } else {
            stringBuffer.append(m);
        }

        stringBuffer.append(":");

        if (s < 10) {
            stringBuffer.append("0").append(s);
        } else {
            stringBuffer.append(s);
        }

        return stringBuffer.toString();
    }

    /**
     * @param timeInSec 秒
     * @return 00:00
     */
    public static String formTimeMS(int timeInSec) {
        int s = timeInSec % 60;
        int m = timeInSec / 60;
        StringBuilder stringBuffer = new StringBuilder();

        if (m < 10) {
            stringBuffer.append("0").append(m);
        } else {
            stringBuffer.append(m);
        }

        stringBuffer.append(":");

        if (s < 10) {
            stringBuffer.append("0").append(s);
        } else {
            stringBuffer.append(s);
        }

        return stringBuffer.toString();
    }


    public static String getRankDate(long timeSecond) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String time = format.format(new Date(timeSecond * 1000L));
        String result;
        if (time.length() == 10) {
            String m = time.substring(5, 7);
            String d = time.substring(8, 10);
            if (m.startsWith("0")) m = m.substring(1);
            if (d.startsWith("0")) d = d.substring(1);
            if (LibBaseUtil.getLang().isChinese()) {
                result = ResUtil.getStr(R.string.rank_mon_day, m, d);
            } else {
                int mon = 1;
                try {
                    mon = Integer.parseInt(m);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                result = getMonthStr(mon) + " " + d;
            }
        } else {
            result = ResUtil.getStr(R.string.unkonw_date);
        }
        return result;
    }

    public static int getDayOfYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_YEAR);
    }

    public static String getTimeLeftString(long expiredTime) {
        long now = getServerTime();
        long div = expiredTime - now;
        if (div <= 0) {
            return ResUtil.getStr(R.string.expired_text);
        }
        if (div > TIME_MONTH - TIME_DAY) {
            // 29 天一个小时显示一个月
            div += TIME_DAY;
            long month = div / TIME_MONTH;
            long day = (div - month * TIME_MONTH) / TIME_DAY;
            if (day != 0) {
                return ResUtil.getStr(R.string.left_mon_day_str, month, day);
            } else {
                return ResUtil.getStr(R.string.left_mon_str, month);
            }
        } else if (div > TIME_DAY) {
            div += TIME_DAY;
            long day = div / TIME_DAY;
            return ResUtil.getStr(R.string.left_day_str, day);
        } else {
            div += TIME_HOUR;
            long hour = div / TIME_HOUR;
            return ResUtil.getStr(R.string.left_hour_str, hour);
        }
    }

    public static String getTimeLeftStringToMin(long expiredTime) {
        long now = getServerTime();
        long div = expiredTime - now;
        if (div <= 0) {
            return ResUtil.getStr(R.string.expired_text);
        }
        if (div > TIME_MONTH - TIME_DAY) {
            // 29 天一个小时显示一个月
            div += TIME_DAY;
            long month = div / TIME_MONTH;
            long day = (div - month * TIME_MONTH) / TIME_DAY;
            if (day != 0) {
                return ResUtil.getStr(R.string.left_mon_day_str, month, day);
            } else {
                return ResUtil.getStr(R.string.left_mon_str, month);
            }
        } else if (div > TIME_DAY) {
            div += TIME_DAY;
            long day = div / TIME_DAY;
            return ResUtil.getStr(R.string.left_day_str, day);
        } else if (div > TIME_HOUR) {
            div += TIME_HOUR;
            long hour = div / TIME_HOUR;
            return ResUtil.getStr(R.string.left_hour_str, hour);
        } else {
            div += TIME_MIN;
            long min = div / TIME_MIN;
            return ResUtil.getStr(R.string.left_min_str, min);
        }
    }


    public static String getTimeValidString(long validTime) {
        long now = getServerTime();
        long div = validTime - now;
        if (div <= 0) {
            return "";
        }
        if (div > TIME_MONTH - TIME_DAY) {
            // 29 天一个小时显示一个月
            div += TIME_DAY;
            long month = div / TIME_MONTH;
            long day = (div - month * TIME_MONTH) / TIME_DAY;
            if (day != 0) {
                return ResUtil.getStr(R.string.valid_mon_day_str, month, day);
            } else {
                return ResUtil.getStr(R.string.valid_mon_str, month);
            }
        } else if (div > TIME_DAY) {
            div += TIME_DAY;
            long day = div / TIME_DAY;
            return ResUtil.getStr(R.string.valid_day_str, day);
        } else if (div > TIME_HOUR) {
            div += TIME_HOUR;
            long hour = div / TIME_HOUR;
            return ResUtil.getStr(R.string.valid_hour_str, hour);
        } else {
            div += TIME_MIN;
            long min = div / TIME_MIN;
            return ResUtil.getStr(R.string.valid_min_str, min);
        }
    }


    /**
     * @param expiredTime 时间秒
     * @return s
     */
    public static String getTagTimeLeftString(long expiredTime) {
        if (expiredTime <= 0) return "";
        int day = (int) Math.ceil(expiredTime * 1f / 60 / 60 / 24);
        return ResUtil.getStr(R.string.left_days, day);
    }

    public static long getServerTime() {
        return System.currentTimeMillis() - serverTimeOffset;
    }

    public static void updateElapsedServerTime(long serverTime) {
        elapsedRealtimeOffset = serverTime - SystemClock.elapsedRealtime();
    }

    public static long getElapsedServerTime() {
        if (elapsedRealtimeOffset == Long.MIN_VALUE) {
            updateElapsedServerTime(System.currentTimeMillis());
        }
        return SystemClock.elapsedRealtime() + elapsedRealtimeOffset;
    }

    public static String getNextDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        return ResUtil.getStr(R.string.rank_mon_day_d, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static String getHourWithMinute(long timeMs) {
        long minute = timeMs / 1000 / 60;
        long hour = minute / 60;
        long remainMinute = minute % 60;
        if (hour > 0) {
            return ResUtil.getResource().getString(R.string.time_hh_mm_1, hour, remainMinute);
        } else {
            return ResUtil.getResource().getString(R.string.time_mm_1, remainMinute);
        }
    }

    public static String getHourMinute(long timeSecond) {
        return getHourMinute(timeSecond, true);
    }

    public static String getHourMinute(long timeSecond, boolean showHour) {
        long hour = timeSecond / 60 / 60;
        long remainMinute = timeSecond / 60 % 60;
        long remainSecond = timeSecond % 60;
        String text = "";
        if (hour != 0 || showHour) {
            if (hour < 10) {
                text = "0" + hour + ":";
            } else {
                text = String.valueOf(hour) + ":";
            }
        }
        if (remainMinute < 10) {
            text = text + "0" + remainMinute;
        } else {
            text = text + String.valueOf(remainMinute);
        }
        if (remainSecond < 10) {
            text = text + ":" + "0" + remainSecond;
        } else {
            text = text + ":" + String.valueOf(remainSecond);
        }
        return text;
    }

    public static String getDayHourMin(long timeSecond) {
        long minute = timeSecond / 60;
        long hour = minute / 60;
        long day = hour / 24;
        long remainMinute = minute % 60;
        long remainHour = hour % 24;
        return ResUtil.getStr(R.string.expire_in, day, remainHour, remainMinute);
    }

    public static String formDay(long timeSecond) {
        long day = timeSecond / 60 / 60 / 24;
        if (day <= 0) {
            return "";
        } else {
            return day + ResUtil.getStr(R.string.days_ago);
        }
    }

    public static String getGrabBoxTime(long time) {
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTimeInMillis(time);
        int targetYear = targetCalendar.get(Calendar.YEAR);
        int targetDay = targetCalendar.get(Calendar.DAY_OF_YEAR);

        Calendar todayCalender = Calendar.getInstance();
        todayCalender.setTimeInMillis(getServerTime());
        int todayYear = todayCalender.get(Calendar.YEAR);
        int todayDay = todayCalender.get(Calendar.DAY_OF_YEAR);

        if (targetYear == todayYear && targetDay == todayDay) {
            return format02.format(new Date(time));
        } else {
            return format07.format(new Date(time));
        }
    }

    public static String formMouthDayHourMin(long time) {
        return format07.format(new Date(time));
    }

    public static String formDayHour(long timeSecond) {
        long minute = timeSecond / 60;
        long hour = minute / 60;
        long day = hour / 24;
        long remainHour = hour % 24;
        //long remainMinute = minute % 60;
        return ResUtil.getStr(R.string.left_time, day, remainHour);
    }

    public static String formDayHourMinSec(long timeSecond) {
        long minute = timeSecond / 60;
        long hour = minute / 60;
        long day = hour / 24;
        long remainHour = hour % 24;
        long remainMinute = minute % 60;
        long remainSec = timeSecond % 60;
        if (day > 0) {
            return ResUtil.getStr(R.string.left_time, day, remainHour);
        }
        if (hour > 0) {
            return ResUtil.getStr(R.string.left_time_h_m, hour, remainMinute);
        }
        return ResUtil.getStr(R.string.left_time_m_s, minute, remainSec);
    }

    public static boolean isSameDay(long time, long time1) {
        return longToYearMonthDay(time).equals(longToYearMonthDay(time1));
    }

    /**
     * 获取当前农历日期
     *
     * @param time 毫秒
     * @return
     */
    public static String getLunarTimeString(long time) {
        LunarCalender lunarCalender = new LunarCalender();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        LunarCalender.LunarCalendarInfo info = lunarCalender.getLunarCalendarInfo(calendar);
        String res = info.year + "年";
        List<String> mouthList = new ArrayList<>();
        for (int i = 0; i < lunarCalender.getMouthNum(info.year); i++) {
            String mouthStr;
            if (1 + i < 13) {
                mouthStr = LunarCalender.chineseMouthNumber[i] + "月";
                mouthList.add(mouthStr);
            } else {
                int index = lunarCalender.getLeapMouthIndex(info.year);
                mouthStr = "闰" + LunarCalender.chineseMouthNumber[index - 1] + "月";
                mouthList.add(index, mouthStr);
            }
        }
        res += mouthList.get(info.mouth - 1) + LunarCalender.chineseDayNumber[info.day - 1];
        return res;
    }

    public static String getTimeString(long time, boolean isLunar) {
        if (isLunar) {
            return getLunarTimeString(time);
        } else {
            return new SimpleDateFormat("yyyy-MM-dd").format(time);
        }
    }

    /**
     * 获取当前农历日期
     *
     * @param time 毫秒
     * @return
     */
    public static int getLunarNextBirthDay(long time) {
        LunarCalender lunarCalender = new LunarCalender();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return lunarCalender.getNextBirthDay(calendar);
    }

    public static int getNextBirthDay(long time) {
        if (time >= System.currentTimeMillis()) return 0;
        calendar.setTimeInMillis(System.currentTimeMillis());
        int curMonth = calendar.get(Calendar.MONTH);
        int curDay = calendar.get(Calendar.DAY_OF_MONTH);
        int curYear = calendar.get(Calendar.YEAR);

        calendar.setTimeInMillis(time);
        int birthdayMonth = calendar.get(Calendar.MONTH);
        int birthdayDay = calendar.get(Calendar.DAY_OF_MONTH);
        if (curMonth == birthdayMonth && curDay == birthdayDay) {
            return 0;
        } else {
            boolean isSpecial = birthdayMonth == Calendar.FEBRUARY && birthdayDay == 29;
            int year = curYear;
            if (isSpecial) {
                year = getNextSpecialYear(System.currentTimeMillis());
            } else {
                if (birthdayMonth < curMonth) {
                    year = curYear + 1;
                } else if (birthdayMonth == curMonth && birthdayDay < curDay) {
                    year = curYear + 1;
                }
            }
            calendar.set(Calendar.YEAR, year);
            long diff = calendar.getTimeInMillis() - System.currentTimeMillis();
            return (int) Math.abs((diff / (60 * 60 * 24 * 1000)) + 1);
        }
    }

    public static int getNextBirthDay(long time, boolean isLunar) {
        if (isLunar) {
            return getLunarNextBirthDay(time);
        } else {
            return getNextBirthDay(time);
        }
    }

    //获取下一个闰年,如果已经超过2.29则不算今年
    public static int getNextSpecialYear(long time) {
        Calendar tmpCalendar = Calendar.getInstance();
        tmpCalendar.setTimeInMillis(time);
        int year = tmpCalendar.get(Calendar.YEAR);
        int month = tmpCalendar.get(Calendar.MONTH);
        if (month >= Calendar.MARCH) year++;
        while (!isSpecialYear(year)) {
            year++;
        }
        return year;
    }

    public static boolean isSpecialYear(int year) {
        if (year % 400 == 0) return true;
        return year % 4 == 0 && year % 100 != 0;
    }

    public static String timeToYearAndMonth(long time) {
        long lt = time * 1000L;
        Date date = new Date(lt);
        return format12.format(date);
    }

    public static String getVipDeadlineTime(long time) {
        long lt = time * 1000L;
        Calendar tmpCalendar = Calendar.getInstance();
        tmpCalendar.setTimeInMillis(lt);
        int month = tmpCalendar.get(Calendar.MONTH) + 1;
        int day = tmpCalendar.get(Calendar.DATE);
        int hour = tmpCalendar.get(Calendar.HOUR_OF_DAY);
        int min = tmpCalendar.get(Calendar.MINUTE);
        String hourStr = hour < 10 ? ("0" + hour) : ("" + hour);
        String minStr = min < 10 ? ("0" + min) : ("" + min);
        return month + "." + day + " " + hourStr + ":" + minStr;
    }

    public static String timeToYearAndMonthAndHourAndMinute(long time) {
        long lt = time * 1000L;
        Date date = new Date(lt);
        return format15.format(date);
    }

    public static int timeToDay(long time) {
        long lt = getServerTime() - time * 1000;
        return (int) (lt / 1000 / 60 / 60 / 24) + 1;
    }


    public static int expireTimeToDay(long time) {
        long lt = time * 1000 - getServerTime();
        return (int) (lt / 1000 / 60 / 60 / 24) + 1;
    }
}
