package com.czm.module.common.utils;

import java.io.UnsupportedEncodingException;

/**
 * Description:
 *
 * @author: Cloud
 * Date: 2018-05-31
 * Time: 10:44
 */
public class ByteUtils {
    public final static String CHAR_SET = "utf-8";

    /**
     * string转byte数组
     *
     * @param txt
     * @return
     */
    public static byte[] stringToBytes(String txt) {
        byte[] buffer = new byte[0];
        if (txt != null) {
            try {
                buffer =  txt.getBytes(CHAR_SET);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return buffer;
    }

    /**
     * byte数组转string
     * @param bytes
     * @return
     */
    public static String bytesToString(byte[] bytes) {
        try {
            return new String(bytes, CHAR_SET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
