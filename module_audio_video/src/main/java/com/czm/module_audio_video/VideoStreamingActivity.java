package com.czm.module_audio_video;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.czm.module.common.base.BaseActivity;
import com.czm.module.module_audio_video.R;
import com.czm.module_audio_video.camera.CameraSurfaceView;
import com.czm.module_audio_video.camera.CameraUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class VideoStreamingActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "VideoStreamingActivity";

    //这里是为了发送视频到vlc客户端进行测试。
    private InetAddress address;
    private DatagramSocket socket;
    private UdpSendTask netSendTask;
    //-----------------------------------------------------------


    //开始录制按钮
    Button record;
    //切换前后摄像头按钮
    Button change;
    // 记录是否正在进行录制
    private boolean isRecording = false;
    private Camera mCamera;
    //视频采集分辨率
    int width = 1280;
    int height = 720;
    byte[] h264;//接收H264
    //h264硬编码器
    AvcEncoder avcEncoder;
    //h264硬解码器
//    AvcDecode avcDecode;
    private CameraSurfaceView mCameraSurfaceView;
    // CameraSurfaceView 容器包装类
    private FrameLayout mAspectLayout;
    private int mOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_streaming);
        // 选择支持半透明模式,在有surfaceview的activity中使用。
        //getWindow().setFormat(PixelFormat.TRANSLUCENT);
        // 获取程序界面中的按钮
        record = findViewById(R.id.record);
        change = findViewById(R.id.change);


        // 未开始录制时让切换相机按钮不可用。
        change.setEnabled(false);
        //把按钮设为灰色
        change.setBackgroundColor(getResources().getColor(R.color.white));
        // 为两个按钮的单击事件绑定监听器
        record.setOnClickListener(this);
        change.setOnClickListener(this);
        mAspectLayout = (FrameLayout) findViewById(R.id.layout_aspect);
        CameraUtils.setDefaultHeight(height);
        CameraUtils.setDefaultWidth(width);
        mCameraSurfaceView = new CameraSurfaceView(this);
        mCameraSurfaceView.setBackgroundResource(R.color.transparent);
        mAspectLayout.addView(mCameraSurfaceView);
       // mOrientation = CameraUtils.calculateCameraPreviewOrientation(VideoStreamingActivity.this);
        //-------------启动发送数据线程-----------------
        netSendTask = new UdpSendTask();
        netSendTask.init();
        netSendTask.start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.record) {
            //变成红色
            change.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            //初始化视频编解码器
            avcEncoder = new AvcEncoder(width, height, 10, 125000);
            getPreViewImage();
        } else if (v.getId() == R.id.change) {
            switchCamera();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CameraUtils.startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraUtils.stopPreview();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRecording = false;
    }

    /**
     * 切换相机
     */
    private void switchCamera() {
        if (mCameraSurfaceView != null) {
            CameraUtils.switchCamera(1 - CameraUtils.getCameraID(), mCameraSurfaceView.getHolder());
            // 切换相机后需要重新计算旋转角度
            mOrientation = CameraUtils.calculateCameraPreviewOrientation(VideoStreamingActivity.this);
        }
    }

    /**
     * 取帧图片
     */
    private void getPreViewImage() {
        CameraUtils.getPreViewImage(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                h264=new byte[data.length];
                try {
                    if (isRecording) {
                        //摄像头数据转h264
                        int ret = avcEncoder.offerEncoder(data, h264);
                        if (ret > 0) {
                            //发送h264到vlc
//                            send(h264, "192.168.1.205");
                            netSendTask.pushBuf(h264, ret);
                        }
                    } else isRecording = true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    //发送数据的线程
    class UdpSendTask extends Thread {
        private ArrayList<ByteBuffer> mList;

        public void init() {
            try {
                socket = new DatagramSocket();
                //设置IP
                address = InetAddress.getByName("192.168.1.191");
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            mList = new ArrayList<ByteBuffer>();

        }

        //添加数据
        public void pushBuf(byte[] buf, int len) {
            ByteBuffer buffer = ByteBuffer.allocate(len);
            buffer.put(buf, 0, len);
            mList.add(buffer);
        }

        @Override
        public void run() {
            Log.d(TAG, "fall in udp send thread");
            while (true) {
                if (mList.size() <= 0) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while (mList.size() > 0) {
                    ByteBuffer sendBuf = mList.get(0);
                    try {

                        //发送数据到指定地址
                        Log.d(TAG, "send udp packet len:" + sendBuf.capacity());
                        DatagramPacket packet = new DatagramPacket(sendBuf.array(), sendBuf.capacity(), address, 8080);

                        socket.send(packet);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    //移除已经发送的数据
                    mList.remove(0);
                }
            }
        }
    }
    public void send(final byte[] ml, final String sIP) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramSocket s = null;
                try {
                    s = new DatagramSocket();
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                InetAddress local = null;
                try {
                    local = InetAddress.getByName(sIP);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                DatagramPacket p = new DatagramPacket(ml, ml.length, local, 80);
                try {
                    Log.d(TAG, "send udp packet len:" + ml.length);
                    s.send(p);
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
