package com.czm.module.other;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.czm.module.common.base.VantageBaseActivity;
import com.czm.module.common.permission.IRequestPermission;
import com.czm.module.common.permission.PermissionUtil;
import com.yanzhenjie.permission.Permission;

public class USBActivity extends VantageBaseActivity {
    private USBReceiver usbReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setEvent() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        PermissionUtil.getInstance().requestPermission(this, iRequestPermission, Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(usbReceiver!=null) {
            unregisterReceiver(usbReceiver);
        }
    }
    private IRequestPermission iRequestPermission = new IRequestPermission() {
        @Override
        public void onSuccess() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
            intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
            intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
            intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
            intentFilter.addDataScheme("file");
            usbReceiver = new USBReceiver();
            registerReceiver(usbReceiver, intentFilter);
        }

        @Override
        public void onFailed() {
            PermissionUtil.getInstance().requestPermission(USBActivity.this, iRequestPermission, Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE);
        }

        @Override
        public void onSettingComeback() {
            PermissionUtil.getInstance().requestPermission(USBActivity.this, iRequestPermission, Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE);
        }

        @Override
        public void onSettingCancel() {
            PermissionUtil.getInstance().requestPermission(USBActivity.this, iRequestPermission, Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE);
        }
    };
}
