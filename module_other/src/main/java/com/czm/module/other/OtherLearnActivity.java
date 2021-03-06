package com.czm.module.other;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.czm.module.common.base.BaseActivity;
import com.czm.module.common.base.ViewManager;
import com.czm.module.common.utils.ToastUtils;
import com.czm.module.common.utils.Utils;
import com.czm.module.other.finger.ZKFingerActivity;

import java.io.IOException;

@Route(path = "/other/OtherLearnActivity")
public class OtherLearnActivity extends BaseActivity {

    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_learn);
        Button btn_notice_board_matches = findViewById(R.id.btn_notice_board_matches);
//        Button btn_notice_board_matches=null;
        btn_notice_board_matches.setOnClickListener(v -> gotoActivity(NoticeBoardMatchesActivity.class));
        Button btn_open_app = findViewById(R.id.btn_open_app);
        btn_open_app.setOnClickListener(v -> {
//            Intent intent = new Intent();
//            //这里是采用的自定义action
//            intent.setAction("prison.outside.app");
//            startActivity(intent);
            PackageManager packageManager = getPackageManager();
            if (checkPackInfo("com.lb.prison.outside")) {
                Intent intent = packageManager.getLaunchIntentForPackage("com.lb.prison.outside");
                startActivity(intent);
            } else {
                Toast.makeText(OtherLearnActivity.this, "没有安装" + "com.lb.prison.outside", Toast.LENGTH_SHORT).show();
            }
        });
        Button btn_zxing = findViewById(R.id.btn_zxing);
        btn_zxing.setOnClickListener(v -> {
            gotoActivity(ZXingTestActivity.class);
        });
        Button btn_changeIp = findViewById(R.id.btn_changeIp);
        btn_changeIp.setOnClickListener(v -> {
            gotoActivity(SetWifiIpActivity.class);
        });
        Button btn_changeIp1 = findViewById(R.id.btn_changeIp1);
        btn_changeIp1.setOnClickListener(v -> {
            gotoActivity(SetEthernetIpActivity.class);
        });
        Button btn_usb = findViewById(R.id.btn_usb);
        btn_usb.setOnClickListener(v -> {
            gotoActivity(USBActivity.class);
        });
        Button btn_dpi = findViewById(R.id.btn_dpi);
        btn_dpi.setOnClickListener(v -> {
            gotoActivity(DpiActivity.class);
        });
        Button btn_zk_finger = findViewById(R.id.btn_zk_finger);
        btn_zk_finger.setOnClickListener(v -> {
            gotoActivity(ZKFingerActivity.class);
        });
        Button btn_base4toimg = findViewById(R.id.btn_base4toimg);
        btn_zk_finger.setOnClickListener(v -> {
            gotoActivity(ZKFingerActivity.class);
        });

        Button btn_reboot = findViewById(R.id.btn_reboot);
        btn_reboot.setOnClickListener(v -> {

//            String cmd = "su -c reboot";
//            try {
//                Runtime.getRuntime().exec(cmd);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            PowerManager pManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            pManager.reboot("重启");
        });
        Button btn_set_time = findViewById(R.id.btn_set_time);
        btn_set_time.setOnClickListener(v -> {
            gotoActivity(SetTimeActivity.class);
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

    /**
     * 检查包是否存在
     *
     * @param packname
     * @return
     */
    private boolean checkPackInfo(String packname) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(packname, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }
}
