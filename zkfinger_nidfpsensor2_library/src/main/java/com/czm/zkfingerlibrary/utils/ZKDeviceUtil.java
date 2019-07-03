package com.czm.zkfingerlibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.czm.module.common.utils.HexUtils;
import com.czm.zkfingerlibrary.callback.EnrollCallback;
import com.czm.zkfingerlibrary.callback.IdentifyCallback;
import com.czm.zkfingerlibrary.callback.USBAuthCallback;
import com.czm.zkfingerlibrary.callback.ZKDeviceCallback;
import com.czm.zkfingerlibrary.entiy.FingerInfo;
import com.orhanobut.logger.Logger;
import com.zkteco.android.biometric.core.device.ParameterHelper;
import com.zkteco.android.biometric.core.device.TransportType;
import com.zkteco.android.biometric.core.utils.LogHelper;
import com.zkteco.android.biometric.core.utils.ToolUtils;
import com.zkteco.android.biometric.module.fingerprintreader.ZKFingerService;
import com.zkteco.android.biometric.nidfpsensor.NIDFPFactory;
import com.zkteco.android.biometric.nidfpsensor.NIDFPSensor;
import com.zkteco.android.biometric.nidfpsensor.exception.NIDFPException;
import com.zkteco.zkfinger.FingerprintService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 中控指纹设备API 工具类
 */
public class ZKDeviceUtil {
    private static volatile ZKDeviceUtil instance = null;
    private static final int VID = 6997;    //Silkid VID always 6997
    //目前光学ZK7000A 为770， 半导体FS200 为772
    private int PID = 770;    //NIDFPSensor PID 根据实际设置
    private NIDFPSensor nidfpSensor = null;
    private byte[] fpImage = null;
    private CountDownLatch countdownLatch = null;
    private boolean bstart = false;
    private boolean bTheadStop = true;
    private CaptureThread captureThread = null;
    private Context mContext;
    private EnrollCallback enrollCallback;
    private IdentifyCallback identifyCallback;
    private ZKDeviceCallback captureCallback;
    private String verifyId = "";//需要别对的人员ID

    public static ZKDeviceUtil getInstance() {
        if (instance == null) {
            synchronized (ZKDeviceUtil.class) {
                if (instance == null) {
                    instance = new ZKDeviceUtil();
                }
            }
        }
        return instance;
    }

    public NIDFPSensor getNidfpSensor() {
        return nidfpSensor;
    }

    /**
     * 设备是否开启
     *
     * @return
     */
    public boolean isBstart() {
        return bstart;
    }

    /**
     * 实例化指纹采集设备类（USB）
     */
    public void createFingerprintSensorForUSB() {
        // Define output log level
        LogHelper.setLevel(Log.WARN);
        // Start fingerprint sensor
        Map fingerprintParams = new HashMap();
        //set vid
        fingerprintParams.put(ParameterHelper.PARAM_KEY_VID, VID);
        //set pid
        fingerprintParams.put(ParameterHelper.PARAM_KEY_PID, PID);
        nidfpSensor = NIDFPFactory.createNIDFPSensor(mContext, TransportType.USBSCSI, fingerprintParams);
    }

    /**
     * 开始初始化指纹采集设备（打开,线程采集）
     */
    public void beginFingerprintSensor(Context context, int PID, ZKDeviceCallback captureCallback) {
        mContext = context;
        this.PID = PID;
        try {
            if (bstart) {
                LogHelper.e("指纹设备已开启");
                captureCallback.sensorOpenSuccess();
                return;
            }
            createFingerprintSensorForUSB();
            this.captureCallback = captureCallback;
            nidfpSensor.setIDFPSupport(false);//不支持15.0(身份证指纹比对)
            nidfpSensor.open(0);
            if (ZKFingerUtil.getInstance().init() != 0) {
                nidfpSensor.close(0);
                return;
            }
            nidfpSensor.setParameter(0, NIDFPSensor.PARAM_CODE_CAPTURE_MODE, 0);
            fpImage = new byte[nidfpSensor.getFpImgWidth() * nidfpSensor.getFpImgHeight()];
            countdownLatch = new CountDownLatch(1);
            captureThread = new CaptureThread();
            captureThread.start();// 线程启动
            bstart = true;
            captureCallback.sensorOpenSuccess();
        } catch (NIDFPException e) {
            captureCallback.sensorException(e);
        }
    }

    /**
     * 停止指纹采集设备
     * 关闭指纹算法库，同时释放缓存
     */
    public void stopFingerprintSensor() {
        if (bstart) {
            //停止采集线程
            bTheadStop = true;
            if (null != countdownLatch) {
                try {
                    //等待线程退出，10S
                    countdownLatch.await(10 * 1000, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ZKFingerUtil.getInstance().close();
            try {
                nidfpSensor.close(0);  //关闭设备
            } catch (NIDFPException e) {
                e.printStackTrace();
            }
            fpImage = null;
        }
        bstart = false;
    }

    /**
     * 录入指纹
     */
    public boolean enroll(EnrollCallback enrollCallback) {
        if (bstart) {
            this.enrollCallback = enrollCallback;
            ZKFingerUtil.getInstance().setRegister(true);
            ZKFingerUtil.getInstance().setEnrollidx(0);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 指纹验证1:N
     */
    public boolean verify(List<FingerInfo> fingerInfoArrayList, IdentifyCallback identifyCallback) {
        if (bstart) {
            for (FingerInfo fingerInfo : fingerInfoArrayList) {
                ZKFingerService.save(HexUtils.hexStringToBytes(fingerInfo.getTemplateBuffer()), fingerInfo.getFingerId().toString());
            }
            this.identifyCallback = identifyCallback;
            ZKFingerUtil.getInstance().setRegister(false);
            ZKFingerUtil.getInstance().setEnrollidx(0);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 指纹验证1:1
     */
    public void setVerifyId(String verifyId) {
        this.verifyId = verifyId;
    }


    /**
     * 释放指纹设备
     */
    public void onDestory() {
        mContext = null;
        verifyId = "";
        NIDFPFactory.destroy(nidfpSensor);
        nidfpSensor = null;
    }

    /**
     * 获得指纹图像和指纹模板
     *
     * @param index
     * @param fpImage    返回指纹图像(最少分配图像宽*高Bytes);流模式不管是否按压都一直返回图像；探测模式按压一次返回一幅指纹图像
     * @param fpTemplate 返回12.0 指纹模板
     * @param quality    返回指纹模板质量
     * @return
     */
    public int AcquireImageAndTemplate(int index, byte[] fpImage, byte[] fpTemplate, int[] quality) {
        if (!bstart) {
            return -1;
        }
        int ret = 0;
        try {
            nidfpSensor.GetFPRawData(0, fpImage);
        } catch (NIDFPException e) {
            e.printStackTrace();
            return -2;
        }
        ret = ZKFingerService.extract(fpImage, nidfpSensor.getFpImgWidth(), nidfpSensor.getFpImgHeight(), fpTemplate, quality);
        return ret;
    }

    /**
     * 采集线程
     */
    private class CaptureThread extends Thread {
        @Override
        public void run() {
            super.run();
            bTheadStop = false;
            while (!bTheadStop) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //指纹模板
                byte[] fpTemplate = new byte[2048];
                //指纹模板质量
                int[] quality = new int[1];
                int ret = AcquireImageAndTemplate(0, fpImage, fpTemplate, quality);
                if (ret <= 0) {
                    Logger.e("AcquireImageAndTemplate ret=" + ret);
                    continue;
                }
                if (mContext != null) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //获取最后一张指纹图像
                            Bitmap mBitMap = ToolUtils.renderCroppedGreyScaleBitmap(fpImage, nidfpSensor.getFpImgWidth(), nidfpSensor.getFpImgHeight());
                            captureCallback.captureOK(mBitMap);
                            if (ZKFingerUtil.getInstance().isRegister()) {
                                //注册
                                if (enrollCallback != null)
                                    ZKFingerUtil.getInstance().enroll(fpTemplate, enrollCallback);
                            } else {
                                //识别
                                if (identifyCallback != null) {
                                    if (TextUtils.isEmpty(verifyId)) {
                                        ZKFingerUtil.getInstance().identify(fpTemplate, identifyCallback);
                                    } else {
                                        ZKFingerUtil.getInstance().verifyId(verifyId, fpTemplate, identifyCallback);
                                    }
                                }
                            }
                        }
                    });
                }
                countdownLatch.countDown();
            }
        }
    }
}
