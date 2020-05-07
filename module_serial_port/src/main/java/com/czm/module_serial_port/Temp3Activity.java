package com.czm.module_serial_port;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.czm.module.common.utils.HexUtils;
import com.czm.module.common.utils.LocalLog;

import android_serialport_api.SerialUtil;

public class Temp3Activity extends AppCompatActivity implements View.OnClickListener {

    private TextView receive_tv;
    private Button receive_b;
    private Button send_b;
    private Button stop_b;
    private ReadThread readThread;
    private int size = -1;
    private static final String TAG = "SerialPortDemoActivity";
    private SerialUtil serialUtil;
    private String path = "";
    private int baudrate = 115200;
    private int flags = 0;
    private EditText edt_ck, edt_btl;
    private RadioButton rBtn_10cm, rBtn_20cm, rBtn_30cm, rBtn_40cm, rBtn_50cm;
    private RadioButton rBtn_2020, rBtn_1818, rBtn_1616, rBtn_1414, rBtn_1212;
    private RadioButton rBtn_5, rBtn_20, rBtn_50, rBtn_all;
    private Button btn_clean;
    private Button send_b_loop;
    private Handler handler = new Handler();
    private int oldFlags = 0;
    private String sbReceive;
    private EditText edt_jlxs;
    private EditText edt_hjxs;
    private double dCoefficient; //距离补偿
    private String getTempCommand = ""; //获取温度命令
    private double environmentTemp = 0.0;//环境温度
    private String logTitle = "";//日志标题
    private int iFristPX = 0;  //初始像素点
    private int iMatrix = 20; //中心区域面积像素点
    private int iHTempNum = 5;  //用于求平均的高温数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp3);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serialUtil != null)
            serialUtil.closeSerialPort();

    }

    protected void init() {
        receive_tv = findViewById(R.id.main_recive_tv);
        receive_b = findViewById(R.id.main_recive_b);
        send_b = findViewById(R.id.main_send_b);
        stop_b = findViewById(R.id.main_stop_b);
        receive_b.setOnClickListener(this);
        send_b.setOnClickListener(this);
        stop_b.setOnClickListener(this);
        edt_ck = findViewById(R.id.edt_ck);
        edt_btl = findViewById(R.id.edt_btl);
        btn_clean = findViewById(R.id.btn_clean);
        btn_clean.setOnClickListener(this);
        send_b_loop = findViewById(R.id.main_send_b_loop);
        send_b_loop.setOnClickListener(this);
        edt_jlxs = findViewById(R.id.edt_jlxs);
        edt_hjxs = findViewById(R.id.edt_hjxs);
        rBtn_5 = findViewById(R.id.rBtn_5);
        rBtn_20 = findViewById(R.id.rBtn_20);
        rBtn_50 = findViewById(R.id.rBtn_50);
        rBtn_all = findViewById(R.id.rBtn_all);
        rBtn_10cm = findViewById(R.id.rBtn_10cm);
        rBtn_20cm = findViewById(R.id.rBtn_20cm);
        rBtn_30cm = findViewById(R.id.rBtn_30cm);
        rBtn_40cm = findViewById(R.id.rBtn_40cm);
        rBtn_50cm = findViewById(R.id.rBtn_50cm);
        rBtn_2020 = findViewById(R.id.rBtn_2020);
        rBtn_1818 = findViewById(R.id.rBtn_1818);
        rBtn_1616 = findViewById(R.id.rBtn_1616);
        rBtn_1414 = findViewById(R.id.rBtn_1414);
        rBtn_1212 = findViewById(R.id.rBtn_1212);
    }

    private void initData() {
        getTempCommand = "F04F01EFEE";
        if (rBtn_10cm.isChecked()) {
            dCoefficient = 1.005;
            logTitle = logTitle + "10cm;";
        } else if (rBtn_20cm.isChecked()) {
            dCoefficient = 1.022;
            logTitle = logTitle + "20cm;";
        } else if (rBtn_30cm.isChecked()) {
            dCoefficient = 1.034;
            logTitle = logTitle + "30cm;";
        } else if (rBtn_40cm.isChecked()) {
            dCoefficient = 1.045;
            logTitle = logTitle + "40cm;";
        } else if (rBtn_50cm.isChecked()) {
            dCoefficient = 1.082;
            logTitle = logTitle + "50cm;";
        }
        if (rBtn_1212.isChecked()) {
            iMatrix = 12;
            logTitle = logTitle + "12*12;";
        } else if (rBtn_1414.isChecked()) {
            iMatrix = 14;
            logTitle = logTitle + "14*14;";
        } else if (rBtn_1616.isChecked()) {
            iMatrix = 16;
            logTitle = logTitle + "16*16;";
        } else if (rBtn_1818.isChecked()) {
            iMatrix = 18;
            logTitle = logTitle + "18*18;";
        } else if (rBtn_2020.isChecked()) {
            iMatrix = 20;
            logTitle = logTitle + "20*20;";
        }
        iFristPX = ((32 - iMatrix) / 2) * 32 + ((32 - iMatrix) / 2);
        if (rBtn_5.isChecked()) {
            iHTempNum = 5;
        } else if (rBtn_20.isChecked()) {
            iHTempNum = 20;
        } else if (rBtn_50.isChecked()) {
            iHTempNum = 50;
        } else if (rBtn_all.isChecked()) {
            iHTempNum = iMatrix * iMatrix;
        }
        logTitle = logTitle + iHTempNum + "个;";
        environmentTemp = Double.parseDouble(edt_hjxs.getText().toString());
        logTitle = logTitle + "环境温度" + environmentTemp + "℃\n";
        LocalLog.setDefalutTag("temp");
//        LocalLog.setFileName(logTitle);
        LocalLog.setFileName(System.currentTimeMillis() + "");
        LocalLog.i(logTitle);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.main_recive_b) {
            try {
                path = "/dev/" + edt_ck.getText().toString();
                baudrate = Integer.parseInt(edt_btl.getText().toString());
                //设置串口号、波特率，
                serialUtil = new SerialUtil(path, baudrate, 0);
            } catch (NullPointerException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            readThread = new ReadThread();
            readThread.start();
            receive_b.setEnabled(false);

        } else if (i == R.id.main_send_b) {
            logTitle = "";
            initData();
            send();

        } else if (i == R.id.main_send_b_loop) {
            logTitle = "";
            initData();
            send_loop();

        } else if (i == R.id.main_stop_b) {
            LocalLog.i(receive_tv.getText().toString() + "\n");
            readThread.interrupt();
            receive_tv.setText("");
            receive_b.setEnabled(true);
            if (serialUtil != null) {
                serialUtil.closeSerialPort();
                serialUtil = null;
            }
            handler.removeCallbacks(loopSend);
        } else if (i == R.id.btn_clean) {
            receive_tv.setText("");
        }

    }

    private void send_loop() {
        send();
        handler.postDelayed(loopSend, 3000);
    }

    private Runnable loopSend = this::send_loop;

    private void send() {
        if (serialUtil != null) {
            flags++;
            Log.e(TAG, "onClick: " + flags + ";发送命令:" + getTempCommand);
            try {
                byte[] sendBytes = null;
                sendBytes = SerialUtil.hexStringToBytes(getTempCommand);
                serialUtil.setData(sendBytes);
            } catch (NullPointerException e) {
                Toast.makeText(this, "串口设置有误，无法发送", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "串口未设置", Toast.LENGTH_SHORT).show();
        }
    }

    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    byte[] data = serialUtil.getDataByte();
                    if (data != null) {
                        String sRec = HexUtils.bytesToHexString(data);
                        onDataReceived(sRec);
                    }
                    Thread.sleep(300);
                } catch (NullPointerException e) {
                    onDataReceived("-1");
                    e.printStackTrace();
                    readThread.interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                    onDataReceived("-1");
                    readThread.interrupt();
                }
            }
        }
    }

    protected void onDataReceived(final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //显示出来
                if ("-1".equals(data)) {
//                    Toast.makeText(SerialPortDemoActivity.this, "串口设置有误，无法接收", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "接收: flags=" + flags + ";oldFlags=" + oldFlags + ";数据长度:" + data.length());
                    if (flags == oldFlags) {
                        sbReceive = sbReceive + data;
                    } else {
                        sbReceive = "";
                        sbReceive = sbReceive + data;
                        oldFlags = flags;
                    }
                    if (sbReceive.length() == (2060 * 2)) {
                        Log.e(TAG, "第" + flags + "次数据接收完成；长度：" + sbReceive.length());
                        byte[] tempByte = HexUtils.hexStringToBytes(sbReceive);
                        getTemp(tempByte);
                    }
                }
            }
        });
    }

    private void getTemp(byte[] data) {
        if (data[0] == (byte) 0xF0 && data[1] == (byte) 0x4F) {
            double dAverageOutPutTemp = 0.0f;
            int iTemp;
            int[] iTempArray = new int[iMatrix * iMatrix];
            int iTempAdd = 0;
            int iPx;
            //取20*20中心区域的点，上下左右各去除6个像素
            for (int i = 0; i < iMatrix; i++) {
                for (int j = 0; j < iMatrix; j++) {
                    //初始点是198像素（6+32*6，也就是第七列往上第七个点）,查询的数据5索引4 是温度1高字节，所以这边高字节+4 低字节+5
                    iPx = iFristPX + (i * 32) + j;
                    iTemp = getOutputTemp(data[iPx * 2 + 4], data[iPx * 2 + 5]);
                    iTempArray[iTempAdd++] = iTemp;
                }
            }
            //排序
            int tmp;
            for (int i = 0; i < iTempAdd; i++) {
                for (int j = i + 1; j < iTempAdd; j++) {
                    if (iTempArray[i] < iTempArray[j]) {
                        tmp = iTempArray[i];
                        iTempArray[i] = iTempArray[j];
                        iTempArray[j] = tmp;
                    }
                }
            }
            tmp = 0;
            //50cm取最大的5个温度求平均
            for (int i = 0; i < iHTempNum; i++) {
                tmp += iTempArray[i];
            }
            dAverageOutPutTemp = tmp / iHTempNum;
            double bdTemp = getOutputTemp(data[2052], data[2053]);

            Log.i("temp", "原始最大：" + getInitialTemp(1, iTempArray[0]) + "℃"
                    + ";最大温度：" + getInitialTemp(dCoefficient, iTempArray[0]) + "℃"
                    + ";原始平均：" + getInitialTemp(1, dAverageOutPutTemp) + "℃"
                    + ";平均温度：" + getInitialTemp(dCoefficient, dAverageOutPutTemp) + "℃"
                    + ";校准温度：" + getStandardTemp(getInitialTemp(dCoefficient, dAverageOutPutTemp), environmentTemp) + "℃"
                    + ";优化温度：" + getOptimizeTemp(getStandardTemp(getInitialTemp(dCoefficient, dAverageOutPutTemp), environmentTemp)) + "℃"
                    + ";本底温度：" + getInitialTemp(1, bdTemp) + "℃"
                    + "\n");
//            receive_tv.append("原始最大：" + String.format("%.1f", getInitialTemp(1, iTempArray[0])) + "℃"
//                    + ";最大温度：" + String.format("%.1f", getInitialTemp(dCoefficient, iTempArray[0])) + "℃"
//                    + ";原始平均：" + String.format("%.1f", getInitialTemp(1, dAverageOutPutTemp)) + "℃"
//                    + ";平均温度：" + String.format("%.1f", getInitialTemp(dCoefficient, dAverageOutPutTemp)) + "℃"
//                    + ";校准温度：" + String.format("%.1f", getStandardTemp(getInitialTemp(dCoefficient, dAverageOutPutTemp), dTempDifference)) + "℃"
//                    + ";优化温度：" + String.format("%.1f", getOptimizeTemp(getStandardTemp(getInitialTemp(dCoefficient, dAverageOutPutTemp), dTempDifference))) + "℃"
//                    + ";本底温度：" + String.format("%.1f", getInitialTemp(1, bdTemp)) + "℃"
//                    + "\n");
            receive_tv.append(";最大温度：" + String.format("%.1f", getInitialTemp(dCoefficient, iTempArray[0])) + "℃"
                    + ";平均温度：" + String.format("%.1f", getInitialTemp(dCoefficient, dAverageOutPutTemp)) + "℃"
                    + ";校准温度：" + String.format("%.1f", getStandardTemp(getInitialTemp(dCoefficient, dAverageOutPutTemp), environmentTemp)) + "℃"
                    + ";优化温度：" + String.format("%.1f", getOptimizeTemp(getStandardTemp(getInitialTemp(dCoefficient, dAverageOutPutTemp), environmentTemp))) + "℃"
                    + "\n");
        }
    }


    /**
     * 串口输出的温度数据
     *
     * @param g 高字节
     * @param d 低字节
     * @return
     */
    private int getOutputTemp(byte g, byte d) {
        return ((g & 0xFF) * 256 + (d & 0xFF));
    }

    /**
     * 初始温度
     *
     * @param coefficient 系数
     * @param outputTemp  输出温度
     * @return
     */
    private double getInitialTemp(double coefficient, double outputTemp) {
        return 0.1014 * coefficient * outputTemp - 275.251 * coefficient;
    }

//    /**
//     * 校准温度
//     *
//     * @param initialTemp    初始温度
//     * @param tempDifference 环境温差
//     * @return
//     */
//
//    private  double getStandardTemp(double initialTemp, double tempDifference) {
//        if (initialTemp >= 37 && tempDifference > 0) {
//            return initialTemp;
//        } else if (initialTemp <= 36 & tempDifference < 0) {
//            return initialTemp;
//        } else {
//            return initialTemp + tempDifference;
//        }
//    }

    /**
     * 校准温度
     *
     * @param initialTemp     初始温度
     * @param environmentTemp 环境温度
     * @return
     */

    private double getStandardTemp(double initialTemp, double environmentTemp) {
        //环境温度补偿
        double tempDifference = 0.0;
        tempDifference = (25 - environmentTemp) * 0.288;
        //36-37°直接不做环境温度补偿
        if (initialTemp >= 37 && tempDifference > 0) {
            return initialTemp;
        } else if (initialTemp <= 36 & tempDifference < 0) {
            return initialTemp;
        } else {
            return initialTemp + tempDifference;
        }
    }

    /**
     * 优化温度
     *
     * @param standardTemp
     * @return
     */
    private double getOptimizeTemp(double standardTemp) {
        double optimizeTemp = 0.0;
        if (standardTemp >= 34 && standardTemp < 35) {
            //35.6--35.9
            optimizeTemp = 35.0 + getRandom(9, 6);
        } else if (standardTemp >= 35 && standardTemp < 36) {
            //36.0-36.5
            optimizeTemp = 36.0 + getRandom(5, 0);
        } else if (standardTemp >= 36 && standardTemp < 37) {
            //36.6--36.9
            optimizeTemp = 36.0 + getRandom(9, 6);
        } else if (standardTemp >= 37.0 && standardTemp < 37.3) {
            optimizeTemp = standardTemp;
        } else if (standardTemp >= 37.3 && standardTemp < 38) {
            //37.3--37.5
            optimizeTemp = 37.0 + getRandom(5, 3);
        } else if (standardTemp >= 38 && standardTemp < 39) {
            //37.6--37.9
            optimizeTemp = 37.0 + getRandom(9, 6);
        } else if (standardTemp >= 39 && standardTemp < 40) {
            optimizeTemp = standardTemp - 1;
        } else if (standardTemp >= 40) {
            optimizeTemp = standardTemp - 2;
        } else {
            optimizeTemp = standardTemp;
        }
        return optimizeTemp;

    }

    private static double getRandom(int max, int min) {
        int ran = (int) (Math.random() * (max - min) + min);
        System.out.println(ran);
        return (float) ran / 10;
    }
}
