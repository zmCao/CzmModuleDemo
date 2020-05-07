package com.czm.module_serial_port;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.czm.module.common.base.BaseActivity;
import com.czm.module.common.utils.DateUtil;

public class TestActivity extends BaseActivity implements View.OnClickListener {

    private Button btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        btnBack=findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back) {
            Log.e("生命周期", "点击退出时间:" + DateUtil.getTimeFrom(System.currentTimeMillis()));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("生命周期", "onDestroy时间:" + DateUtil.getTimeFrom(System.currentTimeMillis()));
    }
}
