package com.czm.zkfingerlibrary.callback;

/**
 * USB授权回调
 */
public interface USBAuthCallback {
    /**
     * 授权成功返回实际的PID
     * @param PID
     */
    void sucess(int PID);
    void fail();
}

