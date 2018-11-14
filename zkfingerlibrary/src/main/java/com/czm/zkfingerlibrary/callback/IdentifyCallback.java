package com.czm.zkfingerlibrary.callback;

import android.graphics.Bitmap;

public interface IdentifyCallback {
    /**
     * 指纹验证成功
     *
     * @param id      指纹ID
     * @param score   分数
     */
    public void identifySuccess(String id, String score);

    /**
     * 指纹验证失败
     */
    public void identifyFailed();
}
