package com.czm.module.netty;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.czm.module.common.base.BaseActivity;
import com.czm.module.netty.client.TimeClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class OpenClientActivity extends BaseActivity {
    private TextView txt_receive;
    private Button btn_Send;
    private EditText edt_Send;
    private TimeClient mTimeClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_client);
        EventBus.getDefault().register(this);
        new Thread(() -> {
            mTimeClient = new TimeClient();
            mTimeClient.run();
        }).start();
        txt_receive = findViewById(R.id.txt_receive);
        btn_Send = findViewById(R.id.btn_Send);
        edt_Send = findViewById(R.id.edt_Send);
        btn_Send.setOnClickListener(v -> {
           mTimeClient.sendMessage(edt_Send.getText().toString());
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ServerMsg(String sMsg) {
        txt_receive.setText(sMsg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
