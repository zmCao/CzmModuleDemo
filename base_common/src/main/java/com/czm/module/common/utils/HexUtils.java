package com.czm.module.common.utils;

/**
 * Description:
 *
 * @author: Cloud
 * Date: 2018-05-15
 * Time: 15:34
 */
public class HexUtils {
    public static String bytesToHexString(byte src) {
        String hex = "";
        int v = src & 0xFF;
        String hv = Integer.toHexString(v).toUpperCase();
        if (hv.length() < 2) {
            hex += "0";
        }
        hex += hv;
        return hex;
    }

    /**
     * Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
     *
     * @param src byte[] data
     * @return hex string
     */
    public static String bytesToHexString(byte[] src, boolean format) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v).toUpperCase();
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            if (format) {
                stringBuilder.append(" ");
            }
        }
        return stringBuilder.toString();
    }

    public static String bytesToHexString(byte[] src) {
        return bytesToHexString(src, false);
    }

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static void writeLong(long num, byte[] buffer, int pos) {
        for (int ix = 0; ix < Long.BYTES; ++ix) {
            int offset = Long.SIZE - (ix + 1) * Byte.SIZE;
            buffer[ix + pos] = (byte) ((num >> offset) & 0xff);
        }
    }

    public static long bytes2Long(byte[] byteNum, int pos) {
        long num = 0;
        for (int ix = 0; ix < 8; ++ix) {
            num <<= 8;
            num |= (byteNum[ix + pos] & 0xff);
        }
        return num;
    }

    public static int twoBytes2Int(byte[] buf, int pos) {
        return ((buf[pos] << 8) & 0xFF00) | (buf[pos + 1] & 0xFF);
    }

    public static void writeTwoBytes(int val, byte[] buffer, int pos) {
        //数据长度    高
        buffer[pos] = (byte) ((val >> 8) & 0xFF);
        //数据长度    低
        buffer[pos + 1] = (byte) (val & 0xFF);
    }

    public static void writeOneByte(int val, byte[] buffer, int pos) {
        buffer[pos] = (byte) (val & 0xFF);
    }

    public static int oneByte2Int(byte[] buf, int pos) {
        return buf[pos] & 0xFF;
    }
}
