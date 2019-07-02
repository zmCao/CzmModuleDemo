package com.czm.zkfingerlibrary.utils;

import com.czm.zkfingerlibrary.callback.EnrollCallback;
import com.czm.zkfingerlibrary.callback.IdentifyCallback;
import com.zkteco.android.biometric.core.utils.LogHelper;
import com.zkteco.android.biometric.module.fingerprintreader.ZKFingerService;
import com.zkteco.zkfinger.FingerprintService;

/**
 * 中控指纹算法API 工具类
 */
public class ZKFingerUtil {
    private static volatile ZKFingerUtil instance = null;
    private boolean isRegister = false;
    private int enrollidx = 0; //录入索引
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

    int init() {
        int ret = -1;
        isRegister = false;
        enrollidx = 0;
        ret = ZKFingerService.init();
        if (ret != 0) {
            LogHelper.e("初始化算法库失败");
        }
        return ret;
    }

    void close() {
        isRegister = false;
        enrollidx = 0;
        ZKFingerService.free();
    }

    /**
     * 指纹验证(1:N比对)
     *
     * @param tmpBuffer 指纹模板
     */
    void identify(byte[] tmpBuffer, IdentifyCallback identifyCallback) {
        byte[] bufids = new byte[256];
        int ret = ZKFingerService.identify(tmpBuffer, bufids, 55, 1);
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
     * 将指纹模板与缓存中指定id的指纹模板进行匹配（即1:1比对），返回匹配分数。
     *
     * @param temp 指纹模板数据数组
     * @param id   被匹配的目标指纹模板id。id类型为字符串，字符串长度不能超过20个字节。
     */
    void verifyId(String id, byte[] temp, IdentifyCallback identifyCallback) {
        int ret = ZKFingerService.verifyId(temp, id);
        if (ret > 55) {
            identifyCallback.identifySuccess(id, ret + "");
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
        int ret = ZKFingerService.identify(tmpBuffer, bufids, 55, 1);
        if (ret > 0) {
            String strRes[] = new String(bufids).split("\t");
            enrollCallback.enrollFailed("指纹录入失败，已存在指纹ID：" + strRes[0]);
            isRegister = false;
            enrollidx = 0;
            return;
        }
        if (enrollidx > 0 && ZKFingerService.verify(regtemparray[enrollidx - 1], tmpBuffer) <= 0) {
            enrollCallback.enrollFailed("指纹录入失败，请重新录入3次指纹");
            enrollidx = 0;
            return;
        }
        System.arraycopy(tmpBuffer, 0, regtemparray[enrollidx], 0, 2048);
        enrollidx++;
        if (enrollidx == 3) {
            byte[] regTemp = new byte[2048];
            int mergeRet = ZKFingerService.merge(regtemparray[0], regtemparray[1], regtemparray[2], regTemp);
            if (mergeRet > 0) {
                enrollCallback.enrollSuccess(regTemp);
                isRegister = false;
            } else {
                enrollCallback.enrollFailed("指纹录入失败，请重新录入3次指纹");
                enrollidx = 0;
            }

        } else {
            enrollCallback.enrollIng(enrollidx);
        }
    }
}
