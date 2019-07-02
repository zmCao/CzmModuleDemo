package com.czm.zkfingerlibrary.callback;

import android.graphics.Bitmap;
import com.zkteco.android.biometric.nidfpsensor.exception.NIDFPException;

/**
 * 指纹设备抓捕回调
 */
public interface ZKDeviceCallback {
    /**
     * 抓取成功
     * @param bitmap 指纹图像
     */
    public void captureOK(Bitmap bitmap);

    /**
     * 错误信息
     */
    public void sensorException(NIDFPException e);

    /**
     * 设备打开成功
     */
    public void sensorOpenSuccess();
}
