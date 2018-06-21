package com.czm.module.main;

import android.os.Bundle;
import android.widget.Button;

import com.alibaba.android.arouter.launcher.ARouter;
import com.czm.module.common.base.BaseActivity;

public class  MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnCustomControlDemo = findViewById(R.id.btnCustomControlDemo);
        btnCustomControlDemo.setOnClickListener(v -> ARouter.getInstance().build("/customcontrol/CustomControlLearnActivity").navigation());
        Button btnGreenDaoDemo=findViewById(R.id.btnGreenDaoDemo);
        btnGreenDaoDemo.setOnClickListener(v -> ARouter.getInstance().build("/GreenDao/GreenDaoLearnActivity").navigation());
        Button btnNetty=findViewById(R.id.btnNetty);
        btnNetty.setOnClickListener(v -> ARouter.getInstance().build("/netty/NettyLearnActivity").navigation());
        Button btnOther=findViewById(R.id.btnOther);
        btnOther.setOnClickListener(v -> ARouter.getInstance().build("/other/OtherLearnActivity").navigation());
        Button btnSerialPort=findViewById(R.id.btnSerialPort);
        btnSerialPort.setOnClickListener(v -> ARouter.getInstance().build("/other/SerialPortLearnActivity").navigation());
    }
}
