package com.czm.module.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.alibaba.android.arouter.launcher.ARouter;

public class  MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnCustomControlDemo = findViewById(R.id.btnCustomControlDemo);
        btnCustomControlDemo.setOnClickListener(v -> ARouter.getInstance().build("/customcontrol/CustomControlLearnActivity").navigation());
    }
}
