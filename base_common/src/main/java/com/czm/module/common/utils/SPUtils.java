package com.czm.module.common.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class SPUtils {

    private static String FILLNAME = "config";

    /**
     * 存入某个key对应的value值
     *
     * @param context
     * @param key
     * @param value
     */
    public static void put(Context context, String key, Object value) {
        SharedPreferences sp = context.getSharedPreferences(FILLNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        if (value instanceof String) {
            edit.putString(key, (String) value);
        } else if (value instanceof Integer) {
            edit.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            edit.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            edit.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            edit.putLong(key, (Long) value);
        }
        edit.apply();
//        SharedPreferencesCompat.EditorCompat.getInstance().apply(edit);
    }

    /**
     * 得到某个key对应的值
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static Object get(Context context, String key, Object defValue) {
        SharedPreferences sp = context.getSharedPreferences(FILLNAME, Context.MODE_PRIVATE);
        if (sp.contains(key)) {
            if (defValue instanceof String) {
                return sp.getString(key, (String) defValue);
            } else if (defValue instanceof Integer) {
                return sp.getInt(key, (Integer) defValue);
            } else if (defValue instanceof Boolean) {
                return sp.getBoolean(key, (Boolean) defValue);
            } else if (defValue instanceof Float) {
                return sp.getFloat(key, (Float) defValue);
            } else if (defValue instanceof Long) {
                return sp.getLong(key, (Long) defValue);
            }
        }
        return null;
    }

    /**
     * SharedPreferences 是否含有KEY
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean hasValue(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILLNAME, Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有数据
     *
     * @param context
     * @return
     */
    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILLNAME, Context.MODE_PRIVATE);
        return sp.getAll();
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param context
     * @param key
     */
    public static void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILLNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.remove(key);
        edit.apply();
//        SharedPreferencesCompat.EditorCompat.getInstance().apply(edit);
    }

    /**
     * 清除所有内容
     *
     * @param context
     */
    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILLNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.clear();
        edit.apply();
        // SharedPreferencesCompat.EditorCompat.getInstance().apply(edit);
    }
}
