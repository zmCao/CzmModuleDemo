package com.czm.zkfingerlibrary.callback;

import android.graphics.Bitmap;

/**
 * 指纹录入回调
 */
public interface EnrollCallback {

    /**
     * 指纹录入中
     * @param enrollidx 第几次录入
     */
    public void enrollIng(int enrollidx);

    /**
     * 录入成功（去保存数据到服务器或本地）
     * @param regTemp 指纹模板
     */
    public void enrollSuccess(byte[] regTemp);

    /**
     * 指纹录入失败
     * @param msg 失败原因
     */
    public void enrollFailed(String msg);
}
