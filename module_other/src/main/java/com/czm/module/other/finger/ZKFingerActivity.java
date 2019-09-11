package com.czm.module.other.finger;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.czm.module.other.R;
import com.czm.zkfingerlibrary.callback.USBAuthCallback;
import com.czm.zkfingerlibrary.callback.ZKDeviceCallback;
import com.czm.zkfingerlibrary.callback.EnrollCallback;
import com.czm.zkfingerlibrary.callback.IdentifyCallback;
import com.czm.zkfingerlibrary.entiy.FingerInfo;
import com.czm.zkfingerlibrary.utils.USBManagerUtil;
import com.czm.zkfingerlibrary.utils.ZKDeviceUtil;
import com.zkteco.android.biometric.nidfpsensor.exception.NIDFPException;
import com.zkteco.zkfinger.FingerprintService;

import java.util.ArrayList;
import java.util.List;

public class ZKFingerActivity extends AppCompatActivity {
    private TextView textView = null;
    private ImageView fpImageView = null;
    private int uid = 1;
    private Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zkfinger);
        textView = (TextView) findViewById(R.id.textView);
        fpImageView = (ImageView) findViewById(R.id.imageView);
        mContext = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZKDeviceUtil.getInstance().onDestory();
    }

    public void OnBnBegin(View view) throws NIDFPException {
        USBManagerUtil.getInstance().RequestDevicePermission(mContext, new USBAuthCallback() {
            @Override
            public void sucess(int PID) {
                ZKDeviceUtil.getInstance().beginFingerprintSensor(mContext, PID, new ZKDeviceCallback() {
                    @Override
                    public void captureOK(Bitmap bitmap) {
                        fpImageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void sensorException(NIDFPException e) {
                        textView.setText("captureError  errno=" + e.getErrorCode() +
                                ",Internal error code: " + e.getInternalErrorCode() + ",message=" + e.getMessage());
                    }

                    @Override
                    public void sensorOpenSuccess() {
                        textView.setText("指纹设备打开成功");
                    }
                });
            }

            @Override
            public void fail() {
                textView.setText("USB 授权失败");
            }
        });

    }

    public void OnBnStop(View view) {
        ZKDeviceUtil.getInstance().stopFingerprintSensor();
    }

    public void OnBnEnroll(View view) {
        boolean enrollRet = ZKDeviceUtil.getInstance().enroll(new EnrollCallback() {

            @Override
            public void enrollIng(int enrollidx) {
                textView.setText("请按第" + (enrollidx + 1) + "次指纹");
            }

            @Override
            public void enrollSuccess(byte[] regTemp) {
                //id:用户自己定义规则
                //缓存到指纹算法SDK，实际操作应该是进行指纹验证的时候，获取数据库指纹 缓存到指纹算法SDK
                FingerprintService.save(regTemp, "test" + uid++);
                textView.setText("录入成功");
                //保存到数据库动作
            }

            @Override
            public void enrollFailed(String msg) {
                textView.setText(msg);
            }
        });
        if (enrollRet)
            textView.setText("录入指纹初始化成功，请录入3次指纹");
        else
            textView.setText("请先开启设备捕获功能");
    }

    public void OnBnVerify(View view) {

        //获取数据库指纹库
        List<FingerInfo> fingerInfoArrayList = new ArrayList<>();
        boolean verifyRet = ZKDeviceUtil.getInstance().verify(fingerInfoArrayList, new IdentifyCallback() {

            @Override
            public void identifySuccess(String id, String score) {
                textView.setText("比对成功, id:" + id + ", score:" + score);
            }

            @Override
            public void identifyFailed() {
                textView.setText("比对失败");
            }
        });
        if (verifyRet)
            textView.setText("指纹验证初始化成功");
        else
            textView.setText("请先开启设备捕获功能");
    }

}
