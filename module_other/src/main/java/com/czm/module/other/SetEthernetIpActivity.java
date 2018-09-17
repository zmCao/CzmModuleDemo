package com.czm.module.other;

import android.annotation.SuppressLint;
import android.net.DhcpInfo;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.NetworkUtils;
import android.net.StaticIpConfiguration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.czm.module.common.base.VantageBaseActivity;
import com.czm.module.other.SetIP.EthernetIPUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.Inet4Address;
import java.net.InetAddress;

public class SetEthernetIpActivity extends VantageBaseActivity implements View.OnClickListener {
    private EthernetManager mEthManager;
    private static String mEthIpAddress = "192.168.1.247";  //IP
    private static String mEthNetmask = "255.255.255.0";  //  子网掩码
    private static String mEthGateway = "192.168.1.1";   //网关
    private static String mEthdns1 = "8.8.8.8";   // DNS1
    private static String mEthdns2 = "8.8.4.4";   // DNS2
    private Button aaa1;
    private Button aaa2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_ethernet_ip);
    }


    @Override
    protected void initView() {
        aaa1 = findViewById(R.id.aaa1);
        aaa2 = findViewById(R.id.aaa2);

    }

    @Override
    protected void setEvent() {
        aaa1.setOnClickListener(this);
        aaa2.setOnClickListener(this);
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void initData() {
        mEthManager = (EthernetManager) getSystemService("ethernet");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.aaa1) {

            InetAddress ipaddr = NetworkUtils.numericToInetAddress(mEthIpAddress);
            DhcpInfo dhcpInfo = new DhcpInfo();
            dhcpInfo.ipAddress = NetworkUtils.inetAddressToInt((Inet4Address) ipaddr);
            Inet4Address inetAddr = EthernetIPUtils.getIPv4Address(mEthIpAddress);
            int prefixLength = EthernetIPUtils.maskStr2InetMask(mEthNetmask);
            InetAddress gatewayAddr = EthernetIPUtils.getIPv4Address(mEthGateway);
            InetAddress dnsAddr = EthernetIPUtils.getIPv4Address(mEthdns1);
            if (inetAddr.getAddress().toString().isEmpty() || prefixLength == 0 || gatewayAddr.toString().isEmpty()
                    || dnsAddr.toString().isEmpty()) {
                return;
            }
            Class<?> clazz = null;
            try {
                clazz = Class.forName("android.net.LinkAddress");
            } catch (Exception e) {
                // TODO: handle exception
            }

            Class[] cl = new Class[]{InetAddress.class, int.class};
            Constructor cons = null;

            try {
                cons = clazz.getConstructor(cl);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            Object[] x = {inetAddr, prefixLength};
            StaticIpConfiguration mStaticIpConfiguration = new StaticIpConfiguration();
            String dnsStr2 = mEthdns2;
            //mStaticIpConfiguration.ipAddress = new LinkAddress(inetAddr, prefixLength);
            try {
                mStaticIpConfiguration.ipAddress = (LinkAddress) cons.newInstance(x);
                Log.d("ip", "chanson 1111111");
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            mStaticIpConfiguration.gateway = gatewayAddr;
            mStaticIpConfiguration.dnsServers.add(dnsAddr);

            if (!dnsStr2.isEmpty()) {
                mStaticIpConfiguration.dnsServers.add(EthernetIPUtils.getIPv4Address(dnsStr2));
            }
            Log.d("ip", "chanson mStaticIpConfiguration  ====" + mStaticIpConfiguration);

            IpConfiguration mIpConfiguration = new IpConfiguration(IpConfiguration.IpAssignment.STATIC,
                    IpConfiguration.ProxySettings.NONE, mStaticIpConfiguration, null);


            mEthManager.setConfiguration(mIpConfiguration);
        } else if (v.getId() == R.id.aaa2) {
            IpConfiguration ipConfiguration = new IpConfiguration(IpConfiguration.IpAssignment.DHCP, IpConfiguration.ProxySettings.NONE, null, null);
            mEthManager.setConfiguration(ipConfiguration);
        }
    }
}
