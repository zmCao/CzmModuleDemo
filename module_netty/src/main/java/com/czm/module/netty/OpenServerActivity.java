package com.czm.module.netty;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.czm.module.common.base.BaseActivity;
import com.czm.module.netty.server.Server;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

public class OpenServerActivity extends BaseActivity {
    private TextView txt_receive;
    private Button btn_Send;
    private EditText edt_Send;
    private Server mNettyServer;
    public Channel channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_server);
        EventBus.getDefault().register(this);
        //启动netty Server
        new Thread(() -> {
            mNettyServer = new Server(8085);
            mNettyServer.run();
            channel = mNettyServer.getmChannerl();
        }).start();
        txt_receive = findViewById(R.id.txt_receive);
        btn_Send = findViewById(R.id.btn_Send);
        edt_Send = findViewById(R.id.edt_Send);
        btn_Send.setOnClickListener(v -> {
            byte[] req = edt_Send.getText().toString().getBytes();
            ByteBuf sMessage = Unpooled.buffer(req.length);
            sMessage.writeBytes(req);
            channel.writeAndFlush(sMessage);
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ClientMsg(String sMsg) {
        txt_receive.setText(sMsg);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
