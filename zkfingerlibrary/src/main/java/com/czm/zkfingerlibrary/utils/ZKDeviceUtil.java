package com.czm.zkfingerlibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.czm.module.common.utils.HexUtils;
import com.czm.zkfingerlibrary.callback.EnrollCallback;
import com.czm.zkfingerlibrary.callback.IdentifyCallback;
import com.czm.zkfingerlibrary.callback.ZKDeviceCallback;
import com.czm.zkfingerlibrary.entiy.FingerInfo;
import com.zkteco.android.biometric.core.device.ParameterHelper;
import com.zkteco.android.biometric.core.device.TransportType;
import com.zkteco.android.biometric.core.utils.LogHelper;
import com.zkteco.android.biometric.core.utils.ToolUtils;
import com.zkteco.android.biometric.module.fingerprint.FingerprintCaptureListener;
import com.zkteco.android.biometric.module.fingerprint.FingerprintFactory;
import com.zkteco.android.biometric.module.fingerprint.FingerprintSensor;
import com.zkteco.android.biometric.module.fingerprint.exception.FingerprintSensorException;
import com.zkteco.zkfinger.FingerprintService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 中控指纹设备API 工具类
 */
public class ZKDeviceUtil {
    private static volatile ZKDeviceUtil instance = null;
    private static final int VID = 6997;    //Silkid VID always 6997
    private static final int PID = 289;     //Silkid PID always 289
    private FingerprintSensor fingerprintSensor = null;
    private final String fpSerialName = "/dev/ttyS2";
    private final int fpBaudrate = 115200;
    private boolean bstart = false;
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

    public FingerprintSensor getFingerprintSensor() {
        return fingerprintSensor;
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
     *
     * @param context
     */
    public void createFingerprintSensorForUSB(Context context) {
        mContext = context;
        // Define output log level
        LogHelper.setLevel(Log.WARN);
        // Start fingerprint sensor
        Map fingerprintParams = new HashMap();
        //set vid
        fingerprintParams.put(ParameterHelper.PARAM_KEY_VID, VID);
        //set pid
        fingerprintParams.put(ParameterHelper.PARAM_KEY_PID, PID);
        fingerprintSensor = FingerprintFactory.createFingerprintSensor(mContext, TransportType.USB, fingerprintParams);
    }

    /**
     * 实例化指纹采集设备类（串口）
     *
     * @param context
     */
    public void createFingerprintSensorForSERIALPORT(Context context) {
        mContext = context;
        // Define output log level
        LogHelper.setLevel(Log.VERBOSE);
        // Start fingerprint sensor
        Map fingerprintParams = new HashMap();
        fingerprintParams.put(ParameterHelper.PARAM_SERIAL_SERIALNAME, fpSerialName);
        fingerprintParams.put(ParameterHelper.PARAM_SERIAL_BAUDRATE, fpBaudrate);
        fingerprintSensor = FingerprintFactory.createFingerprintSensor(mContext, TransportType.SERIALPORT, fingerprintParams);

    }

    /**
     * 开始初始化指纹采集设备（打开,设置监听,异步取像）
     */
    public void beginFingerprintSensor(ZKDeviceCallback captureCallback) {
        try {
            if (bstart) {
                LogHelper.e("指纹设备已开启");
                captureCallback.sensorOpenSuccess();
                return;
            }
            this.captureCallback = captureCallback;
            ZKFingerUtil.getInstance().init();
            fingerprintSensor.open(0);
            fingerprintSensor.setFingerprintCaptureListener(0, fingerprintCaptureListener);
            fingerprintSensor.startCapture(0);
            fingerprintSensor.setFingerprintCaptureMode(0, FingerprintCaptureListener.MODE_CAPTURE_TEMPLATEANDIMAGE);
            bstart = true;
            captureCallback.sensorOpenSuccess();
        } catch (FingerprintSensorException e) {
            captureCallback.sensorException(e);
        }
    }

    /**
     * 停止指纹采集设备
     * 关闭指纹算法库，同时释放缓存
     */
    public void stopFingerprintSensor() {
        try {
            if (bstart) {
                fingerprintSensor.stopCapture(0);
                bstart = false;
                fingerprintSensor.close(0);
                ZKFingerUtil.getInstance().setRegister(false);
                ZKFingerUtil.getInstance().setEnrollidx(0);
            }
        } catch (FingerprintSensorException e) {
            captureCallback.sensorException(e);
        }
    }

    /**
     * 录入指纹
     */
    public boolean enroll(EnrollCallback enrollCallback) {
        if (bstart) {
            this.enrollCallback = enrollCallback;
            byte[] temp = new byte[2048];
            try {
                //clear last template
                fingerprintSensor.acquireTemplate(0, temp);
            } catch (FingerprintSensorException e) {
                e.printStackTrace();
            }
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
                FingerprintService.save(HexUtils.hexStringToBytes(fingerInfo.getTemplateBuffer()), fingerInfo.getFingerId().toString());
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
        FingerprintFactory.destroy(fingerprintSensor);
        ZKFingerUtil.getInstance().close();
    }

    private FingerprintCaptureListener fingerprintCaptureListener = new FingerprintCaptureListener() {
        @Override
        public void captureOK(int captureMode, byte[] imageBuffer, int[] imageAttributes, byte[] templateBuffer) {
            final int[] attributes = imageAttributes;
            final byte[] imgBuffer = imageBuffer;
            final byte[] tmpBuffer = templateBuffer;
            final int capMode = captureMode;
            if (mContext != null) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap mBitMap = null;
                        if (capMode == FingerprintCaptureListener.MODE_CAPTURE_TEMPLATEANDIMAGE) {
                            mBitMap = ToolUtils.renderCroppedGreyScaleBitmap(imgBuffer, attributes[0], attributes[1]);
                            captureCallback.captureOK(mBitMap);
                        }
                        if (ZKFingerUtil.getInstance().isRegister()) {
                            //注册
                            if (enrollCallback != null)
                                ZKFingerUtil.getInstance().enroll(tmpBuffer, enrollCallback);
                        } else {
                            //识别
                            if (identifyCallback != null) {
                                if (TextUtils.isEmpty(verifyId)) {
                                    ZKFingerUtil.getInstance().identify(tmpBuffer, identifyCallback);
                                } else {
                                    ZKFingerUtil.getInstance().verifyId(verifyId, tmpBuffer, identifyCallback);
                                }
                            }
                        }
                    }
                });
            }
        }

        @Override
        public void captureError(FingerprintSensorException e) {
            if (mContext != null) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        captureCallback.sensorException(e);
                        LogHelper.e("captureError  errno=" + e.getErrorCode() +
                                ",Internal error code: " + e.getInternalErrorCode() + ",message=" + e.getMessage());
                    }
                });
            }
        }
    };
}
