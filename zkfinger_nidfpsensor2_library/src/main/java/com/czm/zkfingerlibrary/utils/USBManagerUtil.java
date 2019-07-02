package com.czm.zkfingerlibrary.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

import com.czm.module.common.utils.ToastUtils;

/**
 * USB设备管理工具类
 */
public class USBManagerUtil {
    private static final int VID = 6997;    //Silkid VID always 6997
    private static final int PID = 289;     //Silkid PID always 289
    private static volatile USBManagerUtil instance = null;
    private UsbManager musbManager = null;
    private final String ACTION_USB_PERMISSION = "com.zkteco.android.biometric.USB_PERMISSION";

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
                    } else {
                        ToastUtils.toastShort(context, "USB未授权");
                    }
                }
            }
        }
    };

    /**
     * 检测USB权限，弹窗提示
     * @param mContext
     */
    public void RequestDevicePermission(Context mContext) {
        musbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        mContext.registerReceiver(mUsbReceiver, filter);

        for (UsbDevice device : musbManager.getDeviceList().values()) {
            if (device.getVendorId() == VID && device.getProductId() == PID) {
                Intent intent = new Intent(ACTION_USB_PERMISSION);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
                musbManager.requestPermission(device, pendingIntent);
            }
        }
    }
}
