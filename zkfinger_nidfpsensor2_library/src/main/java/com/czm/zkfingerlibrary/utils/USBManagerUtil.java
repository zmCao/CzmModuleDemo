package com.czm.zkfingerlibrary.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

import com.czm.module.common.utils.ToastUtils;
import com.czm.zkfingerlibrary.callback.USBAuthCallback;
import com.czm.zkfingerlibrary.callback.ZKDeviceCallback;
import com.zkteco.android.biometric.nidfpsensor.exception.NIDFPException;

/**
 * USB设备管理工具类
 */
public class USBManagerUtil {
    private static final int VID = 6997;    //Silkid VID always 6997
    //目前光学ZK7000A 为770， 半导体FS200 为772
    private static int PID = 770;    //NIDFPSensor PID 根据实际设置
    private static volatile USBManagerUtil instance = null;
    private UsbManager musbManager = null;
    private final String ACTION_USB_PERMISSION = "com.zkteco.android.biometric.USB_PERMISSION";
    private USBAuthCallback usbAuthCallback;

    public static USBManagerUtil getInstance() {
        if (instance == null) {
            synchronized (USBManagerUtil.class) {
                if (instance == null) {
                    instance = new USBManagerUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 监听usb广播
     */
    public BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        usbAuthCallback.sucess(PID);
                    } else {
                        usbAuthCallback.fail();
                    }
                }
            }
        }
    };

    /**
     * 检测USB权限，弹窗提示
     *
     * @param mContext
     */
    public void RequestDevicePermission(Context mContext, USBAuthCallback usbAuthCallback) {
        this.usbAuthCallback = usbAuthCallback;
        musbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        mContext.registerReceiver(mUsbReceiver, filter);

        for (UsbDevice device : musbManager.getDeviceList().values()) {
            if (VID == device.getVendorId() && (device.getProductId() >= 0x300 && device.getProductId() <= 0x3FF)) {
                PID = device.getProductId();
                Intent intent = new Intent(ACTION_USB_PERMISSION);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
                musbManager.requestPermission(device, pendingIntent);
            }
        }
    }
}
