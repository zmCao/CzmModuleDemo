package com.czm.zkfingerlibrary.utils;

import android.graphics.Bitmap;

import com.czm.zkfingerlibrary.callback.EnrollCallback;
import com.czm.zkfingerlibrary.callback.IdentifyCallback;
import com.zkteco.android.biometric.core.utils.LogHelper;
import com.zkteco.zkfinger.FingerprintService;

/**
 * 中控指纹算法API 工具类
 */
public class ZKFingerUtil {
    private static volatile ZKFingerUtil instance = null;
    private static final int VID = 6997;    //Silkid VID always 6997
    private static final int PID = 289;     //Silkid PID always 289
    private boolean isRegister = false;
    private int enrollidx = 0;
    private byte[][] regtemparray = new byte[3][2048];  //register template buffer array

    static ZKFingerUtil getInstance() {
        if (instance == null) {
            synchronized (ZKFingerUtil.class) {
                if (instance == null) {
                    instance = new ZKFingerUtil();
                }
            }
        }
        return instance;
    }

    boolean isRegister() {
        return isRegister;
    }

    void setRegister(boolean register) {
        isRegister = register;
    }

    void setEnrollidx(int enrollidx) {
        this.enrollidx = enrollidx;
    }

    void init() {
        isRegister = false;
        enrollidx = 0;
        int limit[] = new int[1];
        //init algorithm share library
        if (0 != FingerprintService.init(limit)) {
            LogHelper.e("初始化指纹识别引擎失败");
            return;
        }
    }

    void close() {
        isRegister = false;
        enrollidx = 0;
        FingerprintService.close();
    }

    /**
     * 指纹验证(1:N比对)
     *
     * @param tmpBuffer 指纹模板
     */
    void identify(byte[] tmpBuffer, IdentifyCallback identifyCallback) {
        byte[] bufids = new byte[256];
        int ret = FingerprintService.identify(tmpBuffer, bufids, 55, 1);
        if (ret > 0) {
            String strRes[] = new String(bufids).split("\t");
            identifyCallback.identifySuccess(strRes[0], strRes[1]);
            LogHelper.d("identify succ, userid:" + strRes[0] + ", score:" + strRes[1]);
        } else {
            identifyCallback.identifyFailed();
            LogHelper.e("identify fail");
        }
    }

    /**
     * 指纹录入
     *
     * @param tmpBuffer      指纹模板
     * @param enrollCallback 录入回调
     */
    void enroll(byte[] tmpBuffer, EnrollCallback enrollCallback) {
        byte[] bufids = new byte[256];
        int ret = FingerprintService.identify(tmpBuffer, bufids, 55, 1);
        if (ret > 0) {
            String strRes[] = new String(bufids).split("\t");
            enrollCallback.enrollFailed("指纹录入失败，已存在指纹ID：" + strRes[0]);
            isRegister = false;
            enrollidx = 0;
            return;
        }
        if (enrollidx > 0 && FingerprintService.verify(regtemparray[enrollidx - 1], tmpBuffer) <= 0) {
            enrollCallback.enrollFailed("指纹录入失败，请重新录入3次指纹");
            return;
        }
        System.arraycopy(tmpBuffer, 0, regtemparray[enrollidx], 0, 2048);
        enrollidx++;
        if (enrollidx == 3) {
            byte[] regTemp = new byte[2048];
            int mergeRet = FingerprintService.merge(regtemparray[0], regtemparray[1], regtemparray[2], regTemp);
            if (mergeRet >0) {
                enrollCallback.enrollSuccess(regTemp);
                isRegister = false;
            } else {
                enrollCallback.enrollFailed("指纹录入失败，请重新录入3次指纹");
                enrollidx=0;
            }

        } else {
            enrollCallback.enrollIng(enrollidx);
        }
    }
}
