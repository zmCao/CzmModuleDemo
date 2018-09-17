package com.czm.module.other;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.czm.module.common.base.VantageBaseActivity;
import com.czm.module.other.SetIP.IPUtils2;
import com.czm.module.other.SetIP.WifiStaticIPUtil;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Android6.0以上app不具备删除，修改WiFi权限。
 * 如果想要在Android6.0代码设置WIFI连接方式为静态IP，
 * 就得提示用户，去WiFi设置里面取消保存（或忘记、删除）该WiFi，
 * 然后用户在APP里选择WIFI，输入密码，代码连接WiFi，然后通过反射调用系统隐藏方法更改连接方式。
 * 注意：WiFi由静态IP改为DHCP或由DHCP改为静态IP需要重启WIFI（直接在手机设置里面改连接方式，WIFI也是会断开在重新连接的），既调用以下方法：
 */
public class SetWifiIpActivity extends VantageBaseActivity {
    Button aaa, aaa2, aaa3, aaa4, aaa5, aaa6;
    long netmaskIpL;
    WifiStaticIPUtil s;
    WifiConfiguration mWifiConfiguration;
    private static final int SELECTED_PREMMSION_STORAGE = 6;
    private WifiManager mwifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_ip);
    }

    @Override
    protected void initView() {
        aaa = (Button) findViewById(R.id.aaa);
        aaa.setOnClickListener(ccc);

        aaa2 = (Button) findViewById(R.id.aaa2);
        aaa2.setOnClickListener(ccc);

        aaa3 = (Button) findViewById(R.id.aaa3);
        aaa3.setOnClickListener(ccc);

        aaa4 = (Button) findViewById(R.id.aaa4);
        aaa4.setOnClickListener(ccc);

        aaa5 = (Button) findViewById(R.id.aaa5);
        aaa5.setOnClickListener(ccc);

        aaa6 = (Button) findViewById(R.id.aaa6);
        aaa6.setOnClickListener(ccc);

        s = new WifiStaticIPUtil(SetWifiIpActivity.this);
        mwifiManager = (WifiManager) SetWifiIpActivity.this.getApplicationContext().getSystemService(WIFI_SERVICE);
    }

    @Override
    protected void setEvent() {

    }

    @Override
    protected void initData() {

    }

    View.OnClickListener ccc = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.aaa:

                    //IP 网络前缀长度24 DNS1域名1 网关
                    Boolean b = s.setIpWithTfiStaticIp(false, "192.168.1.235", 24, "255.255.255.0", "192.168.1.1");
                    Toast.makeText(SetWifiIpActivity.this, "" + b, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.aaa2:
                    //IP 网络前缀长度24 DNS1域名1 网关
                    Boolean c = s.setIpWithTfiStaticIp(true, "192.168.1.235", 24, "255.255.255.0", "192.168.1.1");
                    Toast.makeText(SetWifiIpActivity.this, "" + c, Toast.LENGTH_SHORT).show();

                    break;
                case R.id.aaa3:
                    WifiInfo w = mwifiManager.getConnectionInfo();
                    String ip = IPUtils2.intToIp(w.getIpAddress());
                    Log.e("IP", ip);
                    Toast.makeText(SetWifiIpActivity.this, ip, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.aaa4:
                    Toast.makeText(SetWifiIpActivity.this, s.getWifiSetting(SetWifiIpActivity.this), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.aaa5:
                    String ssid = "OpenWrt_2.4G";
                    String password = "wh20001222";
                    //检测指定SSID的WifiConfiguration 是否存在
                    WifiConfiguration tempConfig = s.IsExsits(ssid);
                    if (tempConfig == null) {
                        mWifiConfiguration = s.CreateWifiInfo(ssid, password, 3);
                        int wcgID = mwifiManager.addNetwork(mWifiConfiguration);
                        boolean bbb = mwifiManager.enableNetwork(wcgID, true);
                    } else {//发现指定WiFi，并且这个WiFi以前连接成功过
                        mWifiConfiguration = tempConfig;
                        boolean bbb = mwifiManager.enableNetwork(mWifiConfiguration.networkId, true);
                    }
                    break;

                case R.id.aaa6:
                    try {
                        WifiInfo connectionInfo = mwifiManager.getConnectionInfo();  //得到连接的wifi网络

                        List<WifiConfiguration> configuredNetworks = mwifiManager
                                .getConfiguredNetworks();
                        for (WifiConfiguration conf : configuredNetworks) {
                            if (conf.networkId == connectionInfo.getNetworkId()) {
                                mWifiConfiguration = conf;
                                break;
                            }

                        }
                        IPUtils2.setStaticIpConfiguration(mwifiManager, mWifiConfiguration,
                                InetAddress.getByName("106.168.1.235"), 24,
                                InetAddress.getByName("192.168.1.1"),
                                InetAddress.getAllByName("8.8.8.8"));
                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (NoSuchFieldException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (UnknownHostException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    break;

            }
        }
    };


}
