package com.czm.module.common.utils;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author :         czm
 * @date :     2020/4/9 16:04
 */
public class LocalLog {
    static private String logPath = Utils.getContext().getExternalFilesDir(null) + "/CrashLog_out/";
    static private boolean androidLogOn = true;
    static private boolean localLogOn = true;
    static private String defalutTag = "LocalLog";
    static private String fileName = "LocalLog";


    static private String fileType = "txt";
    public static final int INFO = 2;
    public static final int ERROR = 3;
    public static final int FAIL = 4;
    public static final int SUCCESS = 5;

    /**
     * 设置log保存的文件类型，如 txt log等
     *
     * @param fileType
     */
    public static void setFileType(String fileType) {
        LocalLog.fileType = fileType;
    }

    /**
     * 设置默认的tag
     *
     * @param defalutTag
     */
    public static void setDefalutTag(String defalutTag) {
        LocalLog.defalutTag = defalutTag;
    }

    /**
     * 修改log的存放路径，如 /sdcard/mylog
     *
     * @param logPath
     */
    public static void setLogPath(String logPath) {
        LocalLog.logPath = logPath;
    }

    /**
     * 修改log的文件名前缀
     *
     * @param fileName
     */
    public static void setFileName(String fileName) {
        LocalLog.fileName = fileName;
    }

    /**
     * 切换log的保存状态
     *
     * @param androidLogOn Android自带的log开启状态
     * @param localLogOn   txt文件记录状态
     */
    public static void switchLog(boolean androidLogOn, boolean localLogOn) {
        LocalLog.androidLogOn = androidLogOn;
        LocalLog.localLogOn = localLogOn;
    }


    public static void i(String msg) {
        byte[] buffer = msg.getBytes();
        if (androidLogOn)
            Log.i(defalutTag, msg);
        if (localLogOn)
            printToFile(INFO, defalutTag, buffer);
    }

    public static void i(String tag, String msg) {
        byte[] buffer = msg.getBytes();
        if (androidLogOn)
            Log.i(tag, msg);
        if (localLogOn)
            printToFile(INFO, tag, buffer);
    }

    public static void e(String msg) {
        byte[] buffer = msg.getBytes();
        if (androidLogOn)
            Log.e(defalutTag, msg);
        if (localLogOn)
            printToFile(ERROR, defalutTag, buffer);
    }

    public static void e(String tag, String msg) {
        byte[] buffer = msg.getBytes();
        if (androidLogOn)
            Log.e(tag, msg);
        if (localLogOn)
            printToFile(ERROR, tag, buffer);
    }

    public static void f(String msg) {
        byte[] buffer = msg.getBytes();
        if (androidLogOn)
            Log.d(defalutTag, msg);
        if (localLogOn)
            printToFile(FAIL, defalutTag, buffer);
    }

    public static void f(String tag, String msg) {
        byte[] buffer = msg.getBytes();
        if (androidLogOn)
            Log.d(tag, msg);
        if (localLogOn)
            printToFile(FAIL, tag, buffer);
    }

    public static void s(String msg) {
        byte[] buffer = msg.getBytes();
        if (androidLogOn)
            Log.i(defalutTag, msg);
        if (localLogOn)
            printToFile(SUCCESS, defalutTag, buffer);
    }

    public static void s(String tag, String msg) {
        byte[] buffer = msg.getBytes();
        if (androidLogOn)
            Log.i(tag, msg);
        if (localLogOn)
            printToFile(SUCCESS, tag, buffer);
    }

    private static void printToFile(int priority, String tag,
                                    byte[] buffer) {
        String logpath = logPath;
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        int millisecond = cal.get(Calendar.MILLISECOND);

        String timeString = String.format("%d-%02d-%02d %02d:%02d:%02d.%d",
                year, month, day, hour, minute, second, millisecond);
        String headString = String.format("\r\n%s\t(%d)\ttag:%s\tdata:",
                timeString, priority, tag) + "\n";
        byte[] headBuffer = headString.getBytes();
        String logFileName;
        switch (priority) {
            case INFO:
                logFileName = "%s/" + fileName + "_Info%d%02d%02d.%s";
                break;
            case ERROR:
                logFileName = "%s/" + fileName + "_Error%d%02d%02d.%s";
                break;
            case FAIL:
                logFileName = "%s/" + fileName + "_Fail%d%02d%02d.%s";
                break;
            case SUCCESS:
                logFileName = "%s/" + fileName + "_Success%d%02d%02d.%s";
                break;
            default:
                logFileName = "%s/" + fileName + "%d%02d%02d.%s";
        }
        logFileName = String.format(logFileName, logpath, year, month, day, fileType);
        FileOutputStream fo = null;
        try {
            File file = new File(logFileName);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            fo = new FileOutputStream(file, true);
            fo.write(headBuffer);
            fo.write(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fo != null) {
                try {
                    fo.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
