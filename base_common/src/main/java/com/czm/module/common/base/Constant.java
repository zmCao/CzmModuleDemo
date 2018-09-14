package com.czm.module.common.base;

import android.os.Environment;

import com.czm.module.common.utils.Utils;

import java.io.File;

public class Constant {
    //网络请求状态
    public static final int TOKEN_EXPRIED=401;
    public static final int WEB_RESP_CODE_SUCCESS=200;

    private static final String PATH_DATA = Utils.getContext().getCacheDir().getAbsolutePath() + File.separator + "data";
    public static final String PATH_CACHE = PATH_DATA + "/NetCache";
    public static final String PATH_FILE =Environment.getExternalStorageDirectory().getPath()+ File.separator+"APK";
}
