package com.czm.module.common.update;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.czm.module.common.base.Constant;
import com.czm.module.common.utils.DateUtil;
import com.czm.module.common.utils.FileProviderAPI24;
import com.czm.module.common.utils.MobileUtil;
import com.czm.module.common.utils.SPUtils;
import com.czm.module.common.utils.Utils;
import com.czm.module.common.utils.ViewManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

public class UpdateManager {
    private String curVersion;
    private String newVersion;
    private String newContent;
    private UpdateCallback callback;
    private Context ctx;
    private int progress;
    private Boolean hasNewVersion;
    private Boolean canceled;
    public String UPDATE_SAVENAME = "SmartLock.apk";
    private static final int UPDATE_CHECKERROR = 0;
    private static final int UPDATE_CHECKCOMPLETED = 1;
    private static final int UPDATE_DOWNLOADING = 2;
    private static final int UPDATE_DOWNLOAD_ERROR = 3;
    private static final int UPDATE_DOWNLOAD_COMPLETED = 4;
    private static final int UPDATE_DOWNLOAD_CANCELED = 5;
    private static final int UPDATE_CONNECTERROR = 6;
    private String UPDATE_DOWNURL;
    private String applicationId;
    private int curVersionCode;
    private int newVersionCode;

    /**
     * 构造函数
     *
     * @param context
     * @param AppUpdateCallback
     * @param ApkName
     */
    public UpdateManager(Context context, UpdateCallback AppUpdateCallback, String ApkName, String applicationId) {
        ctx = context;
        callback = AppUpdateCallback;
        UPDATE_SAVENAME = ApkName;
        canceled = false;
        this.applicationId = applicationId;
        curVersion = MobileUtil.getVersionName(Utils.getContext());
        curVersionCode= MobileUtil.getVersionCode(Utils.getContext());
    }

    /**
     * 获取新版本
     */
    public String getNewVersionName() {
        return newVersion;
    }

    /**
     * 获取当前版本
     */
    public String getCurersionName() {
        return curVersion;
    }

    /**
     * 检查服务器和本地APK的版本号
     */
    public void checkUpdate(String desc, String sUrl, String ver,int versionCode,boolean isSuccess) {
        hasNewVersion = false;
        if (isSuccess) {
            newVersion = ver;
            newVersionCode=versionCode;
            if (!curVersion.equals(newVersion)&& newVersionCode>curVersionCode) {
                hasNewVersion = true;
                newContent = desc;
                UPDATE_DOWNURL = sUrl;
            }
            //告诉handler检查完毕
            updateHandler.sendEmptyMessage(UPDATE_CHECKCOMPLETED);
        } else updateHandler.sendEmptyMessage(UPDATE_CHECKERROR);

    }

    /**
     * 点击APK安装程序
     */
    public void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        FileProviderAPI24.setIntentDataAndType(ctx, intent, "application/vnd.android.package-archive", new File(Constant.PATH_FILE, UPDATE_SAVENAME), applicationId, true);
        ctx.startActivity(intent);
        //记录更新状态
        SharedPreferences sp = ctx.getSharedPreferences(MobileUtil.getMetaValue(ctx, "sharedtag"), Context.MODE_PRIVATE);
        String strDateKey = DateUtil.getStringByFormat(Calendar.getInstance().getTime(), "yyyyMMdd") + "CheckNew";
        SPUtils.put(Utils.getContext(), strDateKey, "2");
        ViewManager.getInstance().exitApp(ctx);
    }

    /**
     * 下载APK文件
     */
    public void downloadPackage() {
        new Thread() {
            @Override
            public void run() {
                try {
                    int iConntectType = MobileUtil.getConnectedType(ctx);
                    String strConntectType = "";
                    if (iConntectType == 0) {
                        strConntectType = "手机网络";
                    } else if (iConntectType == 1) {
                        strConntectType = "Wifi网络";
                    }

                    URL url = new URL(UPDATE_DOWNURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.addRequestProperty("User-Agent", URLEncoder.encode("智开锁 Linux Android Mobile zh_CN " + strConntectType, "utf-8"));
                    conn.connect();
                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();
                    File ApkFile = new File(Constant.PATH_FILE, UPDATE_SAVENAME);
                    File apkDir = new File(Constant.PATH_FILE);
                    if (!apkDir.exists()) {
                        apkDir.mkdirs();
                    }
                    if (ApkFile.exists()) {
                        ApkFile.delete();
                    }
                    FileOutputStream fos = new FileOutputStream(ApkFile);
                    int count = 0;
                    byte buf[] = new byte[512];
                    int numread = 0;

                    do {
                        numread = is.read(buf);
                        count += numread;
                        progress = (int) (((float) count / length) * 100);
                        updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOADING));
                        if (numread <= 0) {
                            updateHandler.sendEmptyMessage(UPDATE_DOWNLOAD_COMPLETED);
                            break;
                        }
                        fos.write(buf, 0, numread);
                    } while (!canceled);

                    if (canceled) {
                        updateHandler.sendEmptyMessage(UPDATE_DOWNLOAD_CANCELED);
                    }
                    fos.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOAD_ERROR, e.getMessage()));
                }

            }
        }.start();
    }

    public void cancelDownload() {
        canceled = true;
    }

    @SuppressLint("HandlerLeak")
    private Handler updateHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UPDATE_CONNECTERROR:
                    callback.noInternet();
                    break;

                case UPDATE_CHECKCOMPLETED:
                    callback.checkUpdateCompleted(hasNewVersion, newVersion,newVersionCode, newContent);
                    break;

                case UPDATE_CHECKERROR:
                    callback.checkUpdateError();
                    break;

                case UPDATE_DOWNLOADING:
                    callback.downloadProgressChanged(progress);
                    break;

                case UPDATE_DOWNLOAD_ERROR:
                    callback.downloadCompleted(false, msg.obj.toString());
                    break;

                case UPDATE_DOWNLOAD_COMPLETED:
                    callback.downloadCompleted(true, "");
                    break;

                case UPDATE_DOWNLOAD_CANCELED:
                    callback.downloadCanceled();
                    break;
                default:
                    break;
            }
        }
    };
}
