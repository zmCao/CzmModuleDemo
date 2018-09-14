package com.czm.module.common.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * Created by czm on 2017/12/6.
 * 适配android7.0 以后在应用间共享文件 辅助类
 */

public class FileProviderAPI24 {
    public static Uri getUriForFile(Context context, File file, String applicationId) {
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= 24) {
            fileUri = getUriForFile24(context, file,applicationId);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    private static Uri getUriForFile24(Context context, File file, String applicationId) {
        return FileProvider.getUriForFile(context, applicationId+".fileProviderAPI24", file);
    }


    public static void setIntentDataAndType(Context context, Intent intent, String type, File file, String applicationId, boolean writeAble) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//这一步的话，最后安装好了，点打开，是不会打开新版本应用的
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setDataAndType(getUriForFile(context, file,applicationId), type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }
    }
}
