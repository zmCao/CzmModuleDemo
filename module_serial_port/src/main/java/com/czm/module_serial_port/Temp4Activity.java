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

import android_serialport_api.SerialUtil;

public class Temp4Activity extends AppCompatActivity implements View.OnClickListener {

    private TextView receive_tv;
    private Button receive_b;
    private EditText send_et;
    private Button send_b;
    private Button stop_b;
    private ReadThread readThread;
    private int size = -1;
    private static final String TAG = "SerialPortDemoActivity";
    private SerialUtil serialUtil;
    //    private String path="/dev/ttyUSB9";
    private String path = "";
    //    private int baudrate = 57600;
    private int baudrate = 115200;
    private int flags = 0;
    private EditText edt_ck, edt_btl;
    private RadioButton rBtn_string, rBtn_hex;
    private Button btn_clean;
    private Button send_b_loop;
    private Handler handler = new Handler();
    private int oldFlags = 0;
    private String sbReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_port_4);
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
        send_et = findViewById(R.id.main_send_et);
        send_b = findViewById(R.id.main_send_b);
        stop_b = findViewById(R.id.main_stop_b);
        receive_b.setOnClickListener(this);
        send_b.setOnClickListener(this);
        stop_b.setOnClickListener(this);
        edt_ck = findViewById(R.id.edt_ck);
        edt_btl = findViewById(R.id.edt_btl);
        rBtn_hex = findViewById(R.id.rBtn_hex);
        rBtn_string = findViewById(R.id.rBtn_string);
        btn_clean = findViewById(R.id.btn_clean);
        btn_clean.setOnClickListener(this);
        send_b_loop = findViewById(R.id.main_send_b_loop);
        send_b_loop.setOnClickListener(this);
        edt_ck.setText("ttyS3");
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
            send();

        } else if (i == R.id.main_send_b_loop) {
            send_loop();

        } else if (i == R.id.main_stop_b) {
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
            String context = send_et.getText().toString();
            Log.e(TAG, "onClick: " + flags + ";发送命令:" + context);
            try {
                //serialUtil.setData(SerialUtil.hexStringToBytes(SerialUtil.bytesToHexString(context.getBytes(), context.getBytes().length) + "0d0a"));
//                    context = context.replace("\r","\n").replace("\n","\r\n").replace("\\r\\n","\r\n");
                byte[] sendBytes = null;
                if (rBtn_string.isChecked()) {
                    sendBytes = context.getBytes();
                } else if (rBtn_hex.isChecked()) {
                    sendBytes = SerialUtil.hexStringToBytes(context);
                }
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
//                        Log.e(TAG, "接收: " + flags + ";数据:" + HexUtils.bytesToHexString(data));
                        String s = new String(data);
                        String sRec = HexUtils.bytesToHexString(data);
                        if (rBtn_hex.isChecked()) {
                            onDataReceived(sRec);
                        } else if (rBtn_string.isChecked()) {
                            s = s.substring(1, s.length() - 1);
                            onDataReceived(s);
                        }
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
                    Toast.makeText(Temp4Activity.this, "串口有误，无法接收", Toast.LENGTH_SHORT).show();
                } else {
//                    Log.e(TAG, "接收: flags=" + flags + ";oldFlags=" + oldFlags + ";数据:" + data);
//                    if (flags == oldFlags) {
//                        sbReceive = sbReceive + data;
//                    } else {
//                        sbReceive = "";
//                        sbReceive = sbReceive + data;
//                        oldFlags = flags;
//                    }
                    receive_tv.append(data+"\n");
                }
            }
        });
    }


}
