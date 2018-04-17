package com.czm.module.netty;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.czm.module.common.base.BaseActivity;
import com.czm.module.common.base.ViewManager;
import com.czm.module.common.utils.ToastUtils;
import com.czm.module.common.utils.Utils;
import com.czm.module.netty.client.HelloClient;
import com.czm.module.netty.client.TimeClient;
import com.czm.module.netty.server.HelloServer;
import com.czm.module.netty.server.Server;

@Route(path = "/netty/NettyLearnActivity")
public class NettyLearnActivity extends BaseActivity {

    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netty_learn);
        //启动netty Server
        new Thread(new Runnable() {
            @Override
            public void run() {
                new HelloServer().run();
            }
        }).start();
        new Thread(() -> {
            //启动netty Client
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new HelloClient().run();
                }
            },5000);
        }).start();

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
