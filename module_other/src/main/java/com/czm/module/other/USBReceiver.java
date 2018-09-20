package com.czm.module.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class USBReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            String mountPath = intent.getData().getPath();
            if (!TextUtils.isEmpty(mountPath)) {
                //读取到U盘路径再做其他业务逻辑
                //发送广播
                Toast.makeText(context, mountPath, Toast.LENGTH_SHORT).show();
                getJson(mountPath+"/Peripheral.json");
            }
        } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED) || action.equals(Intent.ACTION_MEDIA_EJECT)) {
            Toast.makeText(context, "No services information detected !", Toast.LENGTH_SHORT).show();
        } else if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            //如果是开机完成，则需要调用另外的方法获取U盘的路径
            Toast.makeText(context, "请重新插入u盘", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "请重新插入u盘", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 读取USB文件中JSON字符串
     */
    private String getJson(String sPath) {
        File file = new File(sPath);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream instream = new FileInputStream(file);
            InputStreamReader inputreader = new InputStreamReader(instream);
            BufferedReader bf = new BufferedReader(inputreader);
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
            bf.close();
            inputreader.close();
            instream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
