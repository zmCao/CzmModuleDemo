package com.czm.module_serial_port;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.czm.module.common.base.BaseActivity;
import com.czm.module.common.base.ViewManager;
import com.czm.module.common.utils.ToastUtils;
import com.czm.module.common.utils.Utils;
import com.czm.module_serial_port.sample.MainMenu;

@Route(path = "/port/SerialPortLearnActivity")
public class SerialPortLearnActivity extends BaseActivity {

    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_serial_port);
        Button btn_MainMenu = findViewById(R.id.btn_MainMenu);
        btn_MainMenu.setOnClickListener(v -> {
            gotoActivity(MainMenu.class);
        });
        Button btn_Demo = findViewById(R.id.btn_Demo);
        btn_Demo.setOnClickListener(v -> {
            gotoActivity(SerialPortDemoActivity.class);

        });
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
}
