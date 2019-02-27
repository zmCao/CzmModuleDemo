package com.czm.module_audio_video;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.czm.module.common.base.BaseActivity;
import com.czm.module.module_audio_video.R;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class VideoStreamingActivity extends BaseActivity implements View.OnClickListener, Camera.PreviewCallback {
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

    // 显示视频预览的SurfaceView
    SurfaceView sView, mView;
    // 记录是否正在进行录制
    private boolean isRecording = false;
    private Camera mCamera;
    private int cameraPosition = 0;//1代表前置摄像头，0代表后置摄像头
    private int displayOrientation = 90;//相机预览方向，默认是横屏的，旋转90度为竖屏
    //视频采集分辨率
    int width = 320;
    int height = 240;
    byte[] h264;//接收H264
    //h264硬编码器
    AvcEncoder avcEncoder;
    //h264硬解码器
    AvcDecode avcDecode;

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

        // 获取程序界面中的大预览SurfaceView
        sView = (SurfaceView) this.findViewById(R.id.sView);
        // 设置分辨率
        sView.getHolder().setFixedSize(width, height);
        // 设置该组件让屏幕不会自动关闭
        sView.getHolder().setKeepScreenOn(true);

        // 获取程序界面中的小的预览SurfaceView
        mView = (SurfaceView) this.findViewById(R.id.mView);
        // 设置分辨率
        mView.getHolder().setFixedSize(width, height);


        //-------------启动发送数据线程-----------------
        netSendTask = new UdpSendTask();
        netSendTask.init();
        netSendTask.start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.record) {
            initCameara();
        }
//        switch (v.getId()) {
//            // 单击录制按钮
//            case R.id.record:
//                initCameara();
//                break;
//            case R.id.change:
//                //切换摄像头
//                change();
//                break;
//        }
    }

    //初始化相机
    private void initCameara() {
        try {
            mCamera = Camera.open(cameraPosition);
            mCamera.setPreviewDisplay(mView.getHolder());
            //设置预览方向
            mCamera.setDisplayOrientation(displayOrientation);


            //获取相机配置参数
            Camera.Parameters parameters = mCamera.getParameters();

            //这里只是打印摄像头支持的分辨率，实际对程序没有作用，可以删除
            List<Camera.Size> supportedPreviewSizes = parameters
                    .getSupportedPreviewSizes();
            for (Camera.Size s : supportedPreviewSizes
                    ) {
                Log.v(TAG, s.width + "----" + s.height);
            }

            parameters.setFlashMode("off"); // 无闪光灯
            parameters.setPreviewFormat(ImageFormat.NV21); //设置采集视频的格式，默认为NV21,注意，相机预览只支持NV21和YV12两种格式，其他格式会花屏
            parameters.setPreviewFrameRate(10);//设置帧率
            parameters.setPreviewSize(width, height);//设置分辨率
            parameters.setPictureSize(width, height);

            mCamera.setParameters(parameters); // 将Camera.Parameters设定予Camera

            //设置预览回调
            mCamera.setPreviewCallback((Camera.PreviewCallback) this);
            mCamera.startPreview();

            //开始采集让摄像头切换按钮可用
            change.setEnabled(true);
            //变成红色
            change.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            //初始化视频编解码器
            avcEncoder = new AvcEncoder(width, height, 10, 125000);
            avcDecode = new AvcDecode(width, height, sView.getHolder().getSurface());


        } catch (Exception e) {
            Log.i("jw", "camera error:" + Log.getStackTraceString(e));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyCamera();
        isRecording = false;
    }

    private void destroyCamera() {
        if (mCamera == null) {
            return;
        }
        //！！这个必须在前，不然退出出错
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    //切换前后摄像头
    public void change() {
        //切换前后摄像头
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, cameraInfo);

            if (cameraPosition == 1) {
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    displayOrientation = 90;
                    cameraPosition = 0;
                    break;
                }
            } else {
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    displayOrientation = 90;
                    cameraPosition = 1;
                    break;
                }
            }
        }
        destroyCamera();
        initCameara();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        try {
            if (isRecording) {
                //摄像头数据转h264
                int ret = avcEncoder.offerEncoder(data, h264);
                if (ret > 0) {
                    //发送h264到vlc
                    netSendTask.pushBuf(h264, ret);
                }
            } else isRecording = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //发送数据的线程
    class UdpSendTask extends Thread {
        private ArrayList<ByteBuffer> mList;

        public void init() {
            try {
                socket = new DatagramSocket();
                //设置IP
                address = InetAddress.getByName("192.168.1.205");
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
                        DatagramPacket packet = new DatagramPacket(sendBuf.array(), sendBuf.capacity(), address, 5000);

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
}
