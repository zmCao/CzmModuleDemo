package com.czm.module_serial_port;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.LogUtils;
import com.czm.module.common.base.BaseActivity;
import com.czm.module.common.base.ViewManager;
import com.czm.module.common.utils.DateUtil;
import com.czm.module.common.utils.MobileUtil;
import com.czm.module.common.utils.ToastUtils;
import com.czm.module.common.utils.Utils;
import com.czm.module_serial_port.sample.MainMenu;
import com.orhanobut.logger.Logger;

@Route(path = "/port/SerialPortLearnActivity")
public class SerialPortLearnActivity extends BaseActivity {

    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_serial_port);
        Button btn_MainMenu = findViewById(R.id.btn_MainMenu);
        btn_MainMenu.setOnClickListener(v -> {
//            gotoActivity(MainMenu.class);
            gotoActivity(TestActivity.class);
        });
        Button btn_Demo = findViewById(R.id.btn_Demo);
        btn_Demo.setOnClickListener(v -> {
            gotoActivity(SerialPortDemoActivity.class);
        });
        Button btn_temp = findViewById(R.id.btn_temp);
        btn_temp.setOnClickListener(v -> {
            gotoActivity(TempActivity.class);
        });
        Button btn_uart = findViewById(R.id.btn_uart);
        btn_uart.setOnClickListener(v -> {
            gotoActivity(Temp2Activity.class);
//            IFPServer.setDeviceState(IFPServer.Device.Device_Relay, true);
//            btn_uart.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    IFPServer.setDeviceState(IFPServer.Device.Device_Relay, false);
//                }
//            }, 3000);

        });


        Button btn_temp3 = findViewById(R.id.btn_temp3);
        btn_temp3.setOnClickListener(v -> {
            gotoActivity(Temp3Activity.class);
        });

        TextView txtMac = findViewById(R.id.txt_mac);
        txtMac.setText("MAC:" + MobileUtil.getMacAddress(Utils.getContext()));
        TextView txt_wifi_mac = findViewById(R.id.txt_wifi_mac);
        txt_wifi_mac.setText("wifi_MAC:" + MobileUtil.getMacWlan0(Utils.getContext()));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            //两秒之内按返回键就会退出
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtils.toastShort(Utils.getContext(), getString(R.string.app_exit_hint));
                mExitTime = System.currentTimeMillis();
            } else {
                ViewManager.getInstance().exitApp(this);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("生命周期", "onDestroy:" + DateUtil.getTimeFrom(System.currentTimeMillis()));
    }
}
