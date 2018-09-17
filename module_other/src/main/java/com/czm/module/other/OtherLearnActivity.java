package com.czm.module.other;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.czm.module.common.base.BaseActivity;
import com.czm.module.common.base.ViewManager;
import com.czm.module.common.utils.ToastUtils;
import com.czm.module.common.utils.Utils;

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
        Button btn_bga_refreshlayout = findViewById(R.id.btn_bga_refreshlayout);
        btn_bga_refreshlayout.setOnClickListener(v -> {

        });
        Button btn_zxing=findViewById(R.id.btn_zxing);
        btn_zxing.setOnClickListener(v -> {
            gotoActivity(ZXingTestActivity.class);
        });
        Button btn_changeIp=findViewById(R.id.btn_changeIp);
        btn_changeIp.setOnClickListener(v -> {
            gotoActivity(SetWifiIpActivity.class);
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
