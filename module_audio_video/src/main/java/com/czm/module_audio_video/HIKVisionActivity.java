package com.czm.module_audio_video;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.czm.module.common.base.BaseActivity;
import com.czm.module.common.utils.BitMapUtil;
import com.czm.module.common.utils.StringUtils;
import com.czm.module.common.utils.ToastUtils;
import com.czm.module.common.utils.Utils;
import com.czm.module.module_audio_video.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hik.mcrsdk.MCRSDK;
import com.hik.mcrsdk.rtsp.RtspClient;
import com.hik.mcrsdk.talk.TalkClientSDK;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.consts.HttpConstants;
import com.hikvision.sdk.consts.SDKConstant;
import com.hikvision.sdk.net.bean.LoginData;
import com.hikvision.sdk.net.bean.RootCtrlCenter;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.hikvision.sdk.net.bean.SubResourceParam;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.hikvision.sdk.utils.FileUtils;
import com.hikvision.sdk.utils.SDKUtil;
import com.loopj.android.http.RequestParams;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HIKVisionActivity extends BaseActivity implements View.OnClickListener, SurfaceHolder.Callback {

    private RelativeLayout relTitleLay;
    private LinearLayout ll_address_gone;

    private ImageView imgBack;
    private TextView txtTitle;
    private TextView txt_address;
    private FrameLayout framelayout_video;
    private SurfaceView video_view;
    private ImageView img_snapshot;
    private ImageView img_play;
    private TextView txt_video_name;
    private ProgressDialog progressDialog;
    private CustGridView gridView;
    private SubResourceNodeBean subResourceNodeBean;
    private List<SubResourceNodeBean> subResourceNodeBeanList = new ArrayList<>();
    private HIKVisionAdapter hikVisionAdapter;
    private ImageView img_full_screen;
    private ImageView img_capture;
    private ImageView img_record;
    private SubResourceNodeBean mCamera = null;//监控点资源
    /**
     * 播放窗口1
     */
    private int PLAY_WINDOW_ONE = 1;
    /**
     * 是否正在录像
     */
    private boolean mIsRecord;

    //是否是横屏
    private boolean isFullScreen = false;
    //是否在播放
    private boolean isPlay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hikvision);
        initView();
        initEvent();
        getLoginInfo();
    }

    private void initView() {
        ll_address_gone = findViewById(R.id.ll_address_gone);

        txt_address = (TextView) findViewById(R.id.txt_address);
        framelayout_video = (FrameLayout) findViewById(R.id.framelayout_video);
        img_snapshot = (ImageView) findViewById(R.id.img_snapshot);
        img_play = (ImageView) findViewById(R.id.img_play);
        txt_video_name = (TextView) findViewById(R.id.txt_video_name);
        framelayout_video.post(new Runnable() {
            @Override
            public void run() {
                BitMapUtil.SetImageHeight(framelayout_video, (9 / 16.0));
            }
        });
        gridView = (CustGridView) findViewById(R.id.gridView_video);
        hikVisionAdapter = new HIKVisionAdapter(this, subResourceNodeBeanList);
        gridView.setAdapter(hikVisionAdapter);
        video_view = (SurfaceView) findViewById(R.id.video_view);
        img_full_screen = (ImageView) findViewById(R.id.img_full_screen);
        img_capture = findViewById(R.id.img_capture);
        ininHik();
    }

    private void initEvent() {
        imgBack.setOnClickListener(this);
        img_play.setOnClickListener(this);
        img_full_screen.setOnClickListener(this);
        img_capture.setOnClickListener(this);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isPlay) {
                    boolean stopLiveResult = VMSNetSDK.getInstance().stopLiveOpt(1);
                    if (stopLiveResult) {
                        mCamera = (SubResourceNodeBean) parent.getAdapter().getItem(position);
                        startLiveOpt();
                    } else {
                        ToastUtils.showLongToastSafe("停止预览失败");
                    }
                } else {
                    mCamera = (SubResourceNodeBean) parent.getAdapter().getItem(position);
                    startLiveOpt();
                }
            }
        });
        video_view.getHolder().addCallback(this);
    }

    private void initData(String url, String name, String password, String img) {
//        loginOpt("192.168.1.20", "admin", "yq@123456");
        loginOpt(url, name, password);
        Glide.with(Utils.getContext())
                .load(img)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(img_snapshot);
    }

    private void ininHik() {
        MCRSDK.init();
        // 初始化RTSP
        RtspClient.initLib();
        MCRSDK.setPrint(1, null);
        // 初始化语音对讲
        TalkClientSDK.initLib();
        // SDK初始化
        VMSNetSDK.init(getApplication());
    }

    @Override
    public void onClick(View v) {
       if (v.getId() == R.id.img_play) {

            startLiveOpt();
        } else if (v.getId() == R.id.img_full_screen) {
            if (isFullScreen) {
                cancelFullScreen();
            } else {
                setFullScreen();
            }
        } else if (v.getId() == R.id.img_capture) {
            //抓拍按钮点击操作
            String fileName = "Picture" + System.currentTimeMillis() + ".jpg";
            int opt = VMSNetSDK.getInstance().captureLiveOpt(PLAY_WINDOW_ONE, OConstants.IMAGE_FILE_SAVE, fileName);
            switch (opt) {
                case SDKConstant.LiveSDKConstant.SD_CARD_UN_USABLE:
                    ToastUtils.showLongToastSafe("SD卡不可用");
                    break;
                case SDKConstant.LiveSDKConstant.SD_CARD_SIZE_NOT_ENOUGH:
                    ToastUtils.showLongToastSafe("SD卡空间不足");
                    break;
                case SDKConstant.LiveSDKConstant.CAPTURE_FAILED:
                    ToastUtils.showLongToastSafe("抓拍失败");
                    break;
                case SDKConstant.LiveSDKConstant.CAPTURE_SUCCESS:
                    ToastUtils.showLongToastSafe("抓拍成功");
                    BitMapUtil.refreshPhoto(getApplicationContext(), OConstants.IMAGE_FILE_SAVE, fileName);
                    break;
            }
        } else if (v.getId() == R.id.img_record) {
            //录像按钮点击操作
            if (!mIsRecord) {
                int recordOpt = VMSNetSDK.getInstance().startLiveRecordOpt(PLAY_WINDOW_ONE, FileUtils.getVideoDirPath().getAbsolutePath(), "Video" + System.currentTimeMillis() + ".mp4");
                switch (recordOpt) {
                    case SDKConstant.LiveSDKConstant.SD_CARD_UN_USABLE:
                        ToastUtils.showLongToastSafe("SD卡不可用");
                        break;
                    case SDKConstant.LiveSDKConstant.SD_CARD_SIZE_NOT_ENOUGH:
                        ToastUtils.showLongToastSafe("SD卡空间不足");
                        break;
                    case SDKConstant.LiveSDKConstant.RECORD_FAILED:
                        mIsRecord = false;
                        img_record.setImageResource(R.mipmap.icon_shexiang);
                        ToastUtils.showLongToastSafe("启动录像失败");
                        break;
                    case SDKConstant.LiveSDKConstant.RECORD_SUCCESS:
                        mIsRecord = true;
                        ToastUtils.showLongToastSafe("启动录像成功");
                        img_record.setImageResource(R.mipmap.icon_shexiang_sel);
                        break;
                }
            } else {
                VMSNetSDK.getInstance().stopLiveRecordOpt(PLAY_WINDOW_ONE);
                mIsRecord = false;
                img_record.setImageResource(R.mipmap.icon_shexiang);
                ToastUtils.showLongToastSafe("停止录像成功");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /***
     * 登录方法
     */
    private void loginOpt(final String url, final String userName, final String password) {
        String macAddress = getMacAddress();
        // 检查登录参数合法性
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(userName)
                || StringUtils.isEmpty(password) || StringUtils.isEmpty(macAddress)) {
            ToastUtils.showLongToastSafe("参数不能为空");
            return;
        }
        // 登录请求
        String loginAddress = HttpConstants.HTTPS + url;
//        showLoadingDialog("数据加载中...");
        VMSNetSDK.getInstance().Login(loginAddress, userName, password, macAddress, new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
//                dismissLoadingDialog();
            }

            @Override
            public void onSuccess(Object obj) {
                if (obj instanceof LoginData) {
                    //存储登录数据
//                    TempDatas.getIns().setLoginData((LoginData) obj);
//                    TempDatas.getIns().setLoginAddr(url);
//                    PublicFunction.setPrefString(Constants.USER_NAME, userName);
//                    PublicFunction.setPrefString(Constants.PASSWORD, password);
//                    PublicFunction.setPrefString(Constants.ADDRESS_NET, url);
                    //解析版本号
                    String appVersion = ((LoginData) obj).getVersion();
                    SDKUtil.analystVersionInfo(appVersion);

                    getRootControlCenter();
                }
            }
        });
    }

    /**
     * 获取根控制中心
     */
    private void getRootControlCenter() {
        VMSNetSDK.getInstance().getRootCtrlCenterInfo(1, SDKConstant.SysType.TYPE_VIDEO, 999, new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                ToastUtils.showLongToastSafe("获取根控制中心失败");
            }

            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (obj instanceof RootCtrlCenter) {
                    int parentNodeType = Integer.parseInt(((RootCtrlCenter) obj).getNodeType());
                    int parentId = ((RootCtrlCenter) obj).getId();
                    getSubResourceList(parentNodeType, parentId);
                }
            }
        });
    }

    /**
     * 获取父节点资源列表
     *
     * @param parentNodeType 父节点类型
     * @param pId            父节点ID
     */
    private void getSubResourceList(int parentNodeType, int pId) {
        VMSNetSDK.getInstance().getSubResourceList(1, 999, SDKConstant.SysType.TYPE_VIDEO, parentNodeType, String.valueOf(pId), new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                super.onFailure();
                ToastUtils.showLongToastSafe("获取父节点下资源列表失败");
            }

            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (obj instanceof SubResourceParam) {
                    List<SubResourceNodeBean> list = ((SubResourceParam) obj).getNodeList();
                    if (list != null && list.size() > 0) {
                        if (list.get(0).getNodeType() == SDKConstant.NodeType.TYPE_CAMERA_OR_DOOR) {
                            if (mCamera == null) {
                                mCamera = list.get(0);
                            }
                            subResourceNodeBeanList.addAll(list);
                            hikVisionAdapter.notifyDataSetChanged();
//                            dismissLoadingDialog();
                        } else {
                            for (SubResourceNodeBean bean : list)
                                getSubResourceList(bean.getNodeType(), bean.getId());
                        }
                    }
                } else {
                    ToastUtils.showLongToastSafe("获取父节点下资源列表失败");
                }
            }
        });
    }

    private void startLiveOpt() {
//        showLoadingDialog("加载中...");

        //开始预览按钮点击操作
        if (null == mCamera) {
            isPlay = false;
//            dismissLoadingDialog();
            ToastUtils.showLongToastSafe("启动失败");
            return;
        }

        img_snapshot.setVisibility(View.GONE);
        img_play.setVisibility(View.GONE);
        txt_video_name.setVisibility(View.GONE);
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                VMSNetSDK.getInstance().startLiveOpt(PLAY_WINDOW_ONE, mCamera.getSysCode(), video_view, SDKConstant.LiveSDKConstant.MAIN_HIGH_STREAM, new OnVMSNetSDKBusiness() {
                    @Override
                    public void onFailure() {
                        isPlay = false;
//                        dismissLoadingDialog();
                        ToastUtils.showLongToastSafe("获取失败");
                    }

                    @Override
                    public void onSuccess(Object obj) {
                        isPlay = true;
//                        dismissLoadingDialog();
                        getLog(mCamera.getName());//播放視頻日志
                    }
                });
                Looper.loop();
            }
        }.start();


    }

    /**
     * 获取登录设备mac地址
     *
     * @return Mac地址
     */
    private String getMacAddress() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectionInfo = wm.getConnectionInfo();
        String mac = connectionInfo.getMacAddress();
        return mac == null ? "02:00:00:00:00:00" : mac;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //页面销毁时停止预览
        boolean stopLiveResult = VMSNetSDK.getInstance().stopLiveOpt(1);
    }


    //设置全屏
    private void setFullScreen() {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏

            relTitleLay.setVisibility(View.GONE);
            ll_address_gone.setVisibility(View.GONE);

            ViewGroup.LayoutParams lp = framelayout_video.getLayoutParams();
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            framelayout_video.setLayoutParams(lp);

            isFullScreen = true;
        }
    }

    //推出全屏
    private void cancelFullScreen() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏

        relTitleLay.setVisibility(View.VISIBLE);
        ll_address_gone.setVisibility(View.VISIBLE);

        framelayout_video.post(new Runnable() {
            @Override
            public void run() {
                BitMapUtil.SetImageHeight(framelayout_video, (9 / 16.0));
            }
        });

        isFullScreen = false;

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (isFullScreen) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                cancelFullScreen();
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    //获取视频登陆信息
    private void getLoginInfo() {
////        showLoadingDialog("数据加载中...");
////        String url = OConstants.SHOWYUANHEZHISHANG;
////        LogUtils.i("用戶信息:::" + PublicFunction.getPrefString(OConstants.DEFAULT_PROJECT_ID, ""));
//        String url = OConstants.SERVER_ADDR + "rest/cameraInfo/queryAccount?projectId=" + PublicFunction.getPrefString(OConstants.DEFAULT_PROJECT_ID, "");
//        AsyncUtils client = new AsyncUtils(HIKVisionActivity.this);
////        HashMap<String, Object> map = new HashMap<>();
////        map.put("projectId", PublicFunction.getPrefString(OConstants.DEFAULT_PROJECT_ID, ""));
//        client.get(url, new AsyncUtils.MyAsyncUtilInterface() {
//
//            @Override
//            public void onSuccess(String responseBody) {
//                LogUtils.i("视频登陆信息:::" + responseBody);
//                try {
//                    Gson gson = new Gson();
//                    QueryAccoutBean bean = gson.fromJson(responseBody, new TypeToken<QueryAccoutBean>() {
//                    }.getType());
//                    initData(bean.getData().getServerUrl() + ":" + bean.getData().getServerPort()
//                            , bean.getData().getAccount()
//                            , bean.getData().getPassword()
//                            , bean.getData().getCover());
//                    hikVisionAdapter.setImgUrl(bean.getData().getCover());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
////                dismissLoadingDialog();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                LogUtils.i("视频登陆信息:::" + "失败了" + error.getMessage());
//
////                dismissLoadingDialog();
//            }
//        });
    }

    //获取视频登陆信息
    private void getLog(String cameraName) {
////        showLoadingDialog("数据加载中...");
//        String url = OConstants.SERVER_ADDR + "rest/cameraInfo/log";
//        AsyncUtils client = new AsyncUtils(HIKVisionActivity.this);
//        RequestParams params = new RequestParams();
//        params.put("userId", PublicFunction.getPrefString(OConstants.USER_ID, "1"));
//        params.put("cameraName", cameraName);
//        client.post(url, params, new AsyncUtils.MyAsyncUtilInterface() {
//
//            @Override
//            public void onSuccess(String responseBody) {
//                LogUtils.i("视频播放LOG:::" + responseBody);
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                LogUtils.i("视频播放LOG:::" + "失败了" + error.getMessage());
//
//            }
//        });
    }

}
