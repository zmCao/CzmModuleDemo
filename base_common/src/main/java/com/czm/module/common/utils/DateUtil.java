package com.czm.module.common.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {
    /**
     * 当前日期是否在给定日期的后面
     *
     * @param Day    给定日期
     * @param Format 格式
     * @return true, false
     */
    public static boolean isAfter(String Day, String Format) {
        Date day = null;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(Format);
        try {
            day = sdf.parse(Day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calNow = Calendar.getInstance();
        return calNow.getTime().after(day);
    }

    /**
     * 当前日期是否在给定日期的前面
     *
     * @param Day    日期
     * @param Format 格式
     * @return true, false
     */
    public static boolean isBefore(String Day, String Format) {
        Date day = null;
        SimpleDateFormat sdf = new SimpleDateFormat(Format);
        try {
            day = sdf.parse(Day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calNow = Calendar.getInstance();
        return calNow.getTime().before(day);
    }

    /**
     * 将日期类型数据按格式转换成字符串
     *
     * @param date   日期
     * @param format 格式
     * @return true, false
     */
    public static String getStringByFormat(Date date, String format) {
        String strDate = "";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
        strDate = mSimpleDateFormat.format(date);
        return strDate;
    }

    /**
     * 描述：判断是否是闰年()
     * <p>(year能被4整除 并且 不能被100整除) 或者 year能被400整除,则该年为闰年.
     *
     * @param year 年代（如2012）
     * @return boolean 是否为闰年
     */
    public static boolean isLeapYear(int year) {
        if ((year % 4 == 0 && year % 400 != 0) || year % 400 == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 描述：标准化日期时间类型的数据，不足两位的补0.
     *
     * @param dateTime 预格式的时间字符串，如:2012-3-2 12:2:20
     * @return String 格式化好的时间字符串，如:2012-03-20 12:02:20
     */
    public static String dateTimeFormat(String dateTime) {
        StringBuilder sb = new StringBuilder();
        try {
            if (StringUtils.isEmpty(dateTime)) {
                return null;
            }
            String[] dateAndTime = dateTime.split(" ");
            if (dateAndTime.length > 0) {
                for (String str : dateAndTime) {
                    if (str.indexOf("-") != -1) {
                        String[] date = str.split("-");
                        for (int i = 0; i < date.length; i++) {
                            String str1 = date[i];
                            sb.append(StringUtils.strFormat2(str1));
                            if (i < date.length - 1) {
                                sb.append("-");
                            }
                        }
                    } else if (str.indexOf(":") != -1) {
                        sb.append(" ");
                        String[] date = str.split(":");
                        for (int i = 0; i < date.length; i++) {
                            String str1 = date[i];
                            sb.append(StringUtils.strFormat2(str1));
                            if (i < date.length - 1) {
                                sb.append(":");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();
    }

    /**
     * String类型的日期时间转化为Date类型.
     *
     * @param strDate String形式的日期时间
     * @param format  格式化字符串，如："yyyy-MM-dd HH:mm:ss"
     * @return Date Date类型日期时间
     */
    public static Date getDateByFormat(String strDate, String format) {
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = mSimpleDateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取年龄
     *
     * @return
     */
    public static int getAge(String sCalendar) {
        Calendar cParam = Calendar.getInstance();
        cParam.setTime(getDateByFormat(sCalendar, "yyyy-MM-dd"));
        int iParamDate = cParam.get(Calendar.DAY_OF_YEAR);
        Calendar calNow = Calendar.getInstance();
        int iNowDate = calNow.get(Calendar.DAY_OF_YEAR);

        if (iNowDate > iParamDate) {
            return (calNow.get(Calendar.YEAR) - cParam.get(Calendar.YEAR));
        } else {
            return (calNow.get(Calendar.YEAR) - cParam.get(Calendar.YEAR) - 1);
        }
    }

    /**
     * 把具体时间转换成 n分钟前、n小时前、n天前、n月年、1年前
     *
     * @param strDate
     * @return
     */
    public static String ToDateName(String strDate) {
        String strDateName = "";
        try {
            Date dtData = null;
            if (strDate.length() > 10) {
                dtData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);
            } else {
                dtData = new SimpleDateFormat("yyyy-MM-dd").parse(strDate);
            }

            Date dtNow = Calendar.getInstance().getTime();
            GregorianCalendar calData = new GregorianCalendar();
            calData.setTime(dtData);
            GregorianCalendar calNow = new GregorianCalendar();
            calNow.setTime(dtNow);
            long idiff = calNow.getTimeInMillis() - calData.getTimeInMillis();//毫秒间隔
            long dMonth = 30 * 24 * 60 * 60 * 1000L;
            long dYear = 12 * 30 * 24 * 60 * 60 * 1000L;
            if (idiff < 1 * 60 * 1000) {
                strDateName = "1分钟前";
            } else if (idiff < 60 * 60 * 1000) {
                strDateName = idiff / (1 * 60 * 1000) + "分钟前";
            } else if (idiff < 24 * 60 * 60 * 1000) {
                strDateName = idiff / (60 * 60 * 1000) + "小时前";
            } else if (idiff < dMonth) {
                strDateName = idiff / (24 * 60 * 60 * 1000) + "天前";
            } else if (idiff < dYear) {
                strDateName = idiff / dMonth + "月前";
            } else {
                strDateName = "1年前";
            }
        } catch (ParseException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        return strDateName;
    }

    /**
     * 获取分钟数
     *
     * @param calendar
     * @return
     */
    public static int getMinutes(Calendar calendar) {
        int iMinute = 0;
        iMinute = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        return iMinute;
    }

    /**
     * 根据字符串时间获取分钟数
     *
     * @param Time
     * @return
     */
    public static int getMinutesByTime(String Time) {
        int iMinute = 0;
        String[] arrTime = Time.split(":");
        if (arrTime != null && arrTime.length > 1) {
            iMinute = Integer.parseInt(arrTime[0].trim()) * 60 + Integer.parseInt(arrTime[1].trim());
        }
        return iMinute;
    }


    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate, Date bdate) {
        int between_days = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            smdate = sdf.parse(sdf.format(smdate));
            bdate = sdf.parse(sdf.format(bdate));
            Calendar cal = Calendar.getInstance();
            cal.setTime(smdate);
            long time1 = cal.getTimeInMillis();
            cal.setTime(bdate);
            long time2 = cal.getTimeInMillis();
            between_days = (int) ((time2 - time1) / (1000 * 3600 * 24));
        } catch (Exception ignored) {
        }
        return between_days;
    }

    /**
     * 字符串的日期格式的计算
     */
    public static int daysBetween(String smdate, String bdate) {
        int between_days = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(smdate));
            long time1 = cal.getTimeInMillis();
            cal.setTime(sdf.parse(bdate));
            long time2 = cal.getTimeInMillis();
            between_days = (int) ((time2 - time1) / (1000 * 3600 * 24));
        } catch (Exception ignored) {
        }
        return between_days;
    }

    /**
     * 计算给定日期与当前相差几天
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static int daysBetweenNow(String date) {
        long time1 = 0;
        long time2 = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(sdf.format(cal.getTime())));
            time1 = cal.getTimeInMillis();
            cal.setTime(sdf.parse(date));
            time2 = cal.getTimeInMillis();
        } catch (ParseException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        //加1是为了当前时间跟下一天实际未满1天，计算结果是为0
        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 给定日期于今天相比
     *
     * @param date 日期
     * @return 大于0：今天之后；小于0：今天之前
     */
    public static long compareToToday(String date) {
        long ldiff = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(sdf.format(cal.getTime())));
            long time1 = cal.getTimeInMillis();
            cal.setTime(sdf.parse(date));
            long time2 = cal.getTimeInMillis();
            ldiff = time2 - time1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ldiff;
    }

    /**
     * 后面的日期大于前面的日期
     *
     * @param smdate
     * @param bdate
     * @return
     */
    public static long dateCompare(String smdate, String bdate) {
        long ldiff = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(smdate));
            long time1 = cal.getTimeInMillis();
            cal.setTime(sdf.parse(bdate));
            long time2 = cal.getTimeInMillis();
            ldiff = time2 - time1;
        } catch (Exception ignored) {
        }
        return ldiff;
    }

    /**
     * 获取星期
     *
     * @param calendar
     * @return
     */
    public static String getDateName(Calendar calendar) {
        String mDateName = "";
        mDateName = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mDateName)) {
            mDateName = "星期天";
        } else if ("2".equals(mDateName)) {
            mDateName = "星期一";
        } else if ("3".equals(mDateName)) {
            mDateName = "星期二";
        } else if ("4".equals(mDateName)) {
            mDateName = "星期三";
        } else if ("5".equals(mDateName)) {
            mDateName = "星期四";
        } else if ("6".equals(mDateName)) {
            mDateName = "星期五";
        } else if ("7".equals(mDateName)) {
            mDateName = "星期六";
        }
        return mDateName;
    }

    /**
     * 秒转换成00:00:00格式显示
     *
     * @param time
     * @return
     */
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    private static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    /**
     * 毫秒转换成日期时间
     *
     * @param milliseconds
     * @return
     */
    public static String getTimeFrom(long milliseconds) {
        Date dat = new Date(milliseconds);
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(dat);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(gc.getTime());
    }

    /**
     * 描述：标准化日期时间类型的数据，不足两位的补0.
     *
     * @param dateTime 预格式的时间字符串，如:2012-3-2 12:2:20
     * @return String 格式化好的时间字符串，如:2012-03-20 12:02:20
     */
    public static String DateTimeFormat(String dateTime) {
        StringBuilder sb = new StringBuilder();
        try {
            if (TextUtils.isEmpty(dateTime)) {
                return null;
            }
            String[] dateAndTime = dateTime.split(" ");
            if (dateAndTime.length > 0) {
                for (String str : dateAndTime) {
                    if (str.indexOf("-") != -1) {
                        String[] date = str.split("-");
                        for (int i = 0; i < date.length; i++) {
                            String str1 = date[i];
                            sb.append(strFormat2(str1));
                            if (i < date.length - 1) {
                                sb.append("-");
                            }
                        }
                    } else if (str.indexOf(":") != -1) {
                        sb.append(" ");
                        String[] date = str.split(":");
                        for (int i = 0; i < date.length; i++) {
                            String str1 = date[i];
                            sb.append(strFormat2(str1));
                            if (i < date.length - 1) {
                                sb.append(":");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();
    }

    /**
     * 描述：不足2个字符的在前面补“0”.
     *
     * @param str 指定的字符串
     * @return 至少2个字符的字符串
     */
    public static String strFormat2(String str) {
        try {
            if (str.length() <= 1) {
                str = "0" + str;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 描述：获取偏移之后的Date.
     *
     * @param date          日期时间
     * @param calendarField Calendar属性，对应offset的值， 如(Calendar.DATE,表示+offset天,Calendar.HOUR_OF_DAY,表示＋offset小时)
     * @param offset        偏移(值大于0,表示+,值小于0,表示－)
     * @return Date 偏移之后的日期时间
     */
    public static Date getDateByOffset(Date date, int calendarField, int offset) {
        Calendar c = new GregorianCalendar();
        try {
            c.setTime(date);
            c.add(calendarField, offset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c.getTime();
    }

    /**
     * 将秒转换成XX'YY''
     *
     * @param Second
     * @return
     */
    public static String formatMinAndSec(int Second) {
        String sMinAndSec = "";
        if (Second <= 60)
            sMinAndSec = String.valueOf(Second) + "\"";
        else if (Second > 60) {
            sMinAndSec = String.valueOf(Second / 60) + "\'" + String.valueOf(Second % 60) + "\"";
        }
        return sMinAndSec;
    }

    /**
     * 将秒转换成 XX:YY:MM
     *
     * @param Second
     * @return XX:YY:MM
     */
    public static String formatTime(int Second) {
        String sMinAndSec = "";
        if (Second > 3600) {
            sMinAndSec = ((Second / 3600) < 10 ? "0" + (Second / 3600) : (Second / 3600))
                    + ":" + (((Second % 3600) / 60) < 10 ? "0" + ((Second % 3600) / 60) : ((Second % 3600) / 60))
                    + ":" + ((Second % 60) < 10 ? "0" + (Second % 60) : (Second % 60));
        } else if (Second > 60) {
            sMinAndSec = "00:" + ((Second / 60) < 10 ? "0" + (Second / 60) : (Second / 60))
                    + ":" + ((Second % 60) < 10 ? "0" + (Second % 60) : (Second % 60));
        } else if (Second <= 60)
            sMinAndSec = "00:00:" + (Second < 10 ? "0" + Second : Second);
        return sMinAndSec;
    }

    /**
     * 秒转换成时间
     *
     * @param iSeconds
     * @param sFormat
     * @return
     */
    public static String SecondsToTime(int iSeconds, String sFormat) {
        Date date = new Date((long) iSeconds * 1000);
        return new SimpleDateFormat(sFormat).format(date);
    }

    /**
     * 判断时期是否是今天
     *
     * @param str       日期字符串
     * @param formatStr 校验的日期形式，比如我需要校验 “20161212”是否是当天，那么formatStr为"yyyyMMdd"，比如我们需要校验“2016-12-12”是不是当天，就为“yyyy-MM-dd”，比如需要校验“2016/12/12”的字符串，就为“yyyy/MM/dd”，依次类推即可
     * @return true  or  false
     * @throws Exception
     */
    public static boolean isToday(String str, String formatStr) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            Log.e("error", "解析日期错误");
        }
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        int year1 = c1.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH) + 1;
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(new Date());
        int year2 = c2.get(Calendar.YEAR);
        int month2 = c2.get(Calendar.MONTH) + 1;
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        if (year1 == year2 && month1 == month2 && day1 == day2) {
            return true;
        }
        return false;
    }
}
